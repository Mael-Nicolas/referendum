package fr.iut.referendum.libs;

import fr.iut.referendum.Serveur.Referendum;

import java.math.BigInteger;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConnexionBD {

    private static volatile ConnexionBD instance;

    private String mdp = "07042004";
    private String url = "jdbc:oracle:thin:@162.38.222.149:1521:iut";
    private String login = "nicolasm";

    private Connection cn;
    private ResultSet rs;

    private ConnexionBD() {
        try {
            cn = DriverManager.getConnection(url, login, mdp);
        } catch (Exception e) {
            throw new RuntimeException("Pas de connexion");
        }
    }

    /*
    Singleton pour la connexion à la BD (avec une gestion des problèmes de thread)
     */
    public static ConnexionBD getInstance() {
        if (instance == null) {
            synchronized (ConnexionBD.class) {
                if (instance == null) {
                    instance = new ConnexionBD();
                }
                }
        }
        return instance;
    }

    /*
    Renvoi vrai si le login et mdp en paramètres correspondent à un employé dans la BD
    A utiliser pour la connexion avec le login et le mdp rentré dans les champs correspondants
    */
    public boolean employeConnexion(String login, String mdp) {
        String query = "SELECT * FROM Employes WHERE loginEmploye = ?";
        try (PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setString(1, login);
            rs = ps.executeQuery();
            if (!rs.next()) {
                System.out.println("Nom d'utilisateur inconnu");
                return false;
            }
            if (!MotDePasse.verifierMDP(mdp, rs.getString("mdpEmploye"))) {
                System.out.println("Mdp d'utilisateur incorrect");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Connexion impossible");
            return false;
        }
        return true;
    }

    /*
    Permet de vérifier si un employé a voté pour le référendum en paramètre (et donc ne peut pas revoter)
    */
    public boolean aVote(String loginEmploye, int idReferendum) {
        String query = "SELECT * FROM Voter WHERE loginEmploye = ? AND idReferendum = ?";
        try (PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setString(1, loginEmploye);
            ps.setInt(2, idReferendum);
            rs = ps.executeQuery();
            if (!rs.next()) {
                System.out.println("Il n'y a pas d'utilisateur de login " + loginEmploye + " qui a voté au référendum d'id : " + idReferendum);
                return false;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Problème dans la requête 1");
            return false;
        }
        return true;
    }

    /*
    Permet de vérifier si un employé est admin
     */
    public boolean estAdmin(String loginEmploye) {
        String query = "SELECT estAdmin FROM Employes WHERE loginEmploye = ?";
        try (PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setString(1, loginEmploye);
            rs = ps.executeQuery();
            if (!rs.next()) {
                System.out.println("Il n'y a pas d'utilisateur de login " + loginEmploye);
                return false;
            }
            if (rs.getInt("estAdmin") == 0) {
                return false;
            }
        } catch (Exception e) {
            System.out.println("Problème dans la requête 2");
            return false;
        }
        return true;
    }

    /*
    Permet de créer un nouveau employé dans la BD sans doublons
    */
    public boolean creerEmploye(String loginEmploye, String mdp) {
        String query = "INSERT INTO Employes VALUES (?, ?, 0)";
        try (PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setString(1, loginEmploye);
            ps.setString(2, MotDePasse.hacher(mdp));
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Employé déjà existant");
            return false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Création impossible");
            return false;
        }
        return true;
    }

    public boolean supprimerEmploye(String loginEmploye) {
        String query = "DELETE FROM Employes WHERE loginEmploye = ?";
        try (PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setString(1, loginEmploye);
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Erreur BD");
            return false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Suppression impossible");
            return false;
        }
        return true;
    }

    /*
    Récupérer les logins des employes existants
     */
    public List<String> getEmployes() {
        String query = "SELECT loginEmploye FROM Employes ORDER BY loginEmploye";
        List<String> employes = new ArrayList<>();
        try (Statement s = cn.createStatement();
             ResultSet rs = s.executeQuery(query)) {
            while (rs.next()) {
                employes.add(rs.getString("loginEmploye"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Problème dans la requête 3");
            return employes;
        }
        return employes;
    }

    /*
    Permet de créer un nouveau employé dans la BD sans doublons
    */
    public boolean creerAdmin(String loginEmploye, String mdp) {
        String query = "INSERT INTO Employes VALUES (?, ?, 1)";
        try (PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setString(1, loginEmploye);
            ps.setString(2, MotDePasse.hacher(mdp));
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Employé déjà existant");
            return false;
        } catch (Exception e) {
            System.out.println("Création impossible");
            return false;
        }
        return true;
    }

    /*
    Permet de faire un utilisateur en admin
     */
    public boolean passerAdmin(String loginEmploye) {
        String query = "UPDATE Employes SET estAdmin = 1 WHERE loginEmploye = ?";
        int res = 0;
        try (PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setString(1, loginEmploye);
            res = ps.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Problème dans la requête 4");
            return false;
        }
        return res > 0;
    }

    /*
    Permet de dire que l'utilisateur a voté sur le referendum en paramètre
    */
    public boolean voter(String loginEmploye, int idReferendum) {
        String query = "INSERT INTO Voter VALUES (?, ?)";
        try (PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setString(1, loginEmploye);
            ps.setInt(2, idReferendum);
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Clés non existantes dans les tables d'origines ou couple déjà existant : " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Problème dans l'ajout d'un vote");
            return false;
        }
        return true;
    }

    /*
    Enregistrement dans la BD d'un nouveau référendum
    */
    public boolean creerReferendum(String nom, LocalDateTime dateFin, String loginScrutateur) {
        String query = "INSERT INTO Referendums (NOMREFERENDUM, DATEFIN, LOGINSCRUTATEUR, AGREGE, AGREGE2, RESULTAT, P, G, H) VALUES (?, ?, ?, '0', '0', 'Pas de résultat', '0', '0', '0')";
        try (PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setString(1, nom);
            ps.setTimestamp(2, Timestamp.valueOf(dateFin));
            ps.setString(3, loginScrutateur);
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Referendum déjà existant");
            return false;
        } catch (Exception e) {
            System.out.println("Problème dans l'ajout d'un referendum");
            return false;
        }
        return true;
    }

    public Referendum getDernierReferendum() {
        String query = "SELECT * FROM Referendums WHERE idReferendum = (SELECT MAX(idReferendum) FROM Referendums)";
        try (PreparedStatement ps = cn.prepareStatement(query)) {
            rs = ps.executeQuery();
            if (!rs.next()) {
                System.out.println("Il n'y a pas de referendum");
                return null;
            }
            return mapResultSetToReferendum(rs);
        } catch (Exception e) {
            System.out.println("Problème dans la requête 5");
            return null;
        }
    }

    public Referendum getReferendum(int idReferendum) {
        String query = "SELECT * FROM Referendums WHERE idReferendum = ?";
        try (PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setInt(1, idReferendum);
            rs = ps.executeQuery();
            if (!rs.next()) {
                System.out.println("Il n'y a pas de referendum");
                return null;
            }
            return mapResultSetToReferendum(rs);
        } catch (Exception e) {
            System.out.println("Problème dans la requête 6");
            return null;
        }
    }

    /*
    Vérifier si un référendum est ouvert
     */
    public boolean estOuvert(int idReferendum) {
        String query = "SELECT referendumOuvert FROM Referendums WHERE idReferendum = ?";
        try (PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setInt(1, idReferendum);
            rs = ps.executeQuery();
            if (!rs.next()) {
                System.out.println("Il n'y a pas de referendum");
                return false;
            }
            return rs.getInt("referendumOuvert") == 1;
        } catch (Exception e) {
            System.out.println("Problème dans la requête 7");
            return false;
        }
    }

    /*
    Ouvrir (1) ou fermer (0) un referendum
     */
    public void changerEtat(int idReferendum, int etat) {
        String query = "UPDATE Referendums SET referendumOuvert = ? WHERE idReferendum = ?";
        try (PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setInt(1, etat);
            ps.setInt(2, idReferendum);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Problème dans la requête 8");
        }
    }

    public List<Referendum> getReferendums() {
        String query = "SELECT * FROM Referendums ORDER BY idReferendum";
        List<Referendum> referendums = new ArrayList<>();
        try (Statement s = cn.createStatement();
             ResultSet rs = s.executeQuery(query)) {
            while (rs.next()) {
                referendums.add(mapResultSetToReferendum(rs));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Problème dans la requête 9");
            return referendums;
        }
        return referendums;
    }

    /*
    Pour éviter la duplication de code
     */
    private Referendum mapResultSetToReferendum(ResultSet rs) throws SQLException {
        BigInteger[] pk = new BigInteger[3];
        BigInteger[] agregeVotes = new BigInteger[2];

        // Récupération des votes agrégés
        agregeVotes[0] = new BigInteger(rs.getString("agrege"));
        agregeVotes[1] = new BigInteger(rs.getString("agrege2"));

        // Récupération de la clé publique
        if ("0".equals(rs.getString("P"))) {
            pk[0] = BigInteger.ZERO;
            pk[1] = BigInteger.ZERO;
            pk[2] = BigInteger.ZERO;
        } else {
            pk[0] = new BigInteger(rs.getString("P"));
            pk[1] = new BigInteger(rs.getString("G"));
            pk[2] = new BigInteger(rs.getString("H"));
        }

        // Création de l'objet Referendum
        return new Referendum(
                rs.getInt("idReferendum"),
                rs.getString("nomReferendum"),
                rs.getTimestamp("dateFin").toLocalDateTime(),
                agregeVotes,
                rs.getString("resultat"),
                pk
        );
    }


    public boolean supprimerReferendum(int idReferendum) {
        String query = "DELETE FROM Referendums WHERE idReferendum = ?";
        try (PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setInt(1, idReferendum);
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Referendum non existant");
            return false;
        } catch (Exception e) {
            System.out.println("Suppression impossible");
            return false;
        }
        return true;
    }


    public boolean changerAgregeReferendum(int idReferendum, BigInteger[] votesAgrege) {
        String query = "UPDATE Referendums SET agrege = ?, agrege2 = ? WHERE idReferendum = ?";
        int res = 0;
        try (PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setString(1, votesAgrege[0].toString());
            ps.setString(2, votesAgrege[1].toString());
            ps.setInt(3, idReferendum);
            res = ps.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Problème dans la requête 10");
            return false;
        }
        return res > 0;
    }

    public void changerClePubliqueReferendum(int idReferendum, BigInteger[] pk) {
        String query = "UPDATE Referendums SET P = ?, G = ?, H = ? WHERE idReferendum = ?";
        int res = 0;
        try (PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setString(1, pk[0].toString());
            ps.setString(2, pk[1].toString());
            ps.setString(3, pk[2].toString());
            ps.setInt(4, idReferendum);
            res = ps.executeUpdate();
            changerEtat(idReferendum, 1);    // ouvrir le référendum quand il reçoit une clé
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Problème dans la requête 11");
        }
    }

    public void changerResultatReferendum(int id, String resultat) {
        String query = "UPDATE Referendums SET Resultat = ? WHERE loginReferendum = ?";
        int res = 0;
        try (PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.setString(2, resultat);
            res = ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Problème dans la requête 12");
        }
    }

    public int nbVotants(int idReferendum) {
        String query = "SELECT COUNT(*) FROM Voter WHERE idReferendum = ?";
        try (PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setInt(1, idReferendum);
            ResultSet res = ps.executeQuery();
            if (res.next()) {
                return res.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Problème dans la requête");
        }
        return 0;
    }

    // scrutateurs

    /*
    Renvoi la liste des IDs des référendums dont s'occupe un scrutateur
     */
    public List<Referendum> getReferendumsScrutateur(String loginScrutateur) {
        String query = "SELECT * FROM Referendums r JOIN Scrutateurs s ON s.loginScrutateur = r.loginScrutateur WHERE s.loginScrutateur = ?";
        List<Referendum> referendums = new ArrayList<>();
        try (PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setString(1, loginScrutateur);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                referendums.add(mapResultSetToReferendum(rs));
            }
        } catch (SQLException e) {
            System.out.println("Problème dans la requête 13");
            System.out.println(e.getMessage());
            return referendums;
        }
        return referendums;
    }

    /*
    Permet de créer un nouveau scrutateur dans la BD sans doublons
    */
    public boolean creerScrutateur(String loginScrutateur, String mdp) {
        String query = "INSERT INTO Scrutateurs (loginScrutateur, mdpScrutateur) VALUES  (?, ?)";
        try (PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setString(1, loginScrutateur);
            ps.setString(2, MotDePasse.hacher(mdp));
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Scrutateur déjà existant");
            return false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Création impossible");
            return false;
        }
        return true;
    }

    /*
    Renvoi vrai si le login et mdp en paramètres correspondent à un scrutateur dans la BD
    */
    public boolean scrutateurConnexion(String login, String mdp) {
        String query = "SELECT * FROM Scrutateurs WHERE loginScrutateur = ?";
        try (PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setString(1, login);
            rs = ps.executeQuery();
            if (!rs.next()) {
                System.out.println("Nom du scrutateur inconnu");
                return false;
            }
            if (!MotDePasse.verifierMDP(mdp, rs.getString("mdpScrutateur"))) {
                System.out.println("Mdp du scrutateur incorrect");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Connexion impossible");
            return false;
        }
        return true;
    }

    /*
    Récupérer les logins des scrutateurs existants
     */
    public List<String> getScrutateurs() {
        String query = "SELECT loginScrutateur FROM Scrutateurs ORDER BY loginScrutateur";
        List<String> scrutateurs = new ArrayList<>();
        try (Statement s = cn.createStatement();
             ResultSet rs = s.executeQuery(query)) {
            while (rs.next()) {
                scrutateurs.add(rs.getString("loginScrutateur"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Problème dans la requête 14");
            return scrutateurs;
        }
        return scrutateurs;
    }

    public boolean supprimerScrutateur(String loginScrutateur) {
        String query = "DELETE FROM Scrutateurs WHERE loginScrutateur = ?";
        try (PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setString(1, loginScrutateur);
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Erreur BD");
            return false;
        } catch (Exception e) {
            System.out.println("Suppression impossible");
            return false;
        }
        return true;
    }

    public void deconnexion() {
        try {
            if (rs != null) {
                rs.close();
            }
            if (cn != null) {
                cn.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("Problème de déconnexion", e);
        }
    }

}