package fr.iut.referendum;

import java.sql.*;

public class ConnexionBD {

    private String mdp = "07042004";
    private String url = "jdbc:oracle:thin:@162.38.222.149:1521:iut";
    private String login = "nicolasm";

    private Connection cn;
    private ResultSet rs;

    public ConnexionBD() {
        try {
            cn = DriverManager.getConnection(url, login, mdp);
        } catch (Exception e) {
            throw new RuntimeException("Pas de connexion");
        }
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
            if (!rs.getString("mdpEmploye").equals(mdp)) {
                System.out.println("Mdp d'utilisateur incorrect");
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("Problème dans la requête", e);
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
            throw new RuntimeException("Problème dans la requête", e);
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
            ps.setString(2, mdp);
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Employé déjà existant");
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Problème dans l'insertion", e);
        }
        return true;
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
            throw new RuntimeException("Problème dans l'ajout d'un vote", e);
        }
        return true;
    }

    /*
    Enregistrement dans la BD d'un nouveau référendum
    */
    public boolean creerReferendum(int id, String nom, java.util.Date dateFinJava) {
        String query = "INSERT INTO Referendums VALUES (?, ?, ?, 1)";
        try (PreparedStatement ps = cn.prepareStatement(query)) {
            java.sql.Date dateFin = new java.sql.Date(dateFinJava.getTime());
            ps.setInt(1, id);
            ps.setString(2, nom);
            ps.setDate(3, dateFin);
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Referendum déjà existant");
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Problème dans l'ajout d'un referendum", e);
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
