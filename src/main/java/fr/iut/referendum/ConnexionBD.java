package fr.iut.referendum;

import java.sql.*;

public class ConnexionBD {

    private String mdp = "07042004.";
    private String url = "jdbc:oracle:thin:@162.38.222.149:1521:iut";
    private String login = "nicolasm";

    private Connection cn;
    private Statement st;
    private ResultSet rs;

    public ConnexionBD() {
        try {
            cn = DriverManager.getConnection(url,login,mdp);
        } catch (Exception e) {
            throw new RuntimeException("Pas de connexion");
        }

        try {
            st = cn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        } catch (Exception e) {
            throw new RuntimeException("Problème dans le statement");
        }
    }

    /*
    Renvoi vrai si le login et mdp en paramètres correspondent à un employé dans la BD
    A utiliser pour la connexion avec le login et le mdp rentré dans les champs correspondants
     */
    public boolean employeConnexion(String login, String mdp) {
        try {
            rs = st.executeQuery("SELECT * FROM Employes WHERE loginEmploye = " + login);
        } catch (Exception e) {
            throw new RuntimeException("Problème dans la requête");
        }
        try {
            if (!rs.next()) {
                System.out.println("Nom d'utilisateur inconnu");
                return false;
            }
            if (!rs.getString("mdpEmploye").equals(mdp)) {
                System.out.println("Mdp d'utilisateur incorrect");
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("Probleme dans la lecture");
        }
        return true;
    }

    /*
    Permet de vérifier si un employé à voté pour le référendum en paramètre (et donc ne peut pas revoter)
     */
    public boolean aVote(String loginEmploye, int idReferendum) {
        try {
            rs = st.executeQuery("SELECT * FROM Voter WHERE loginEmploye = '" + loginEmploye + "' AND idReferendum = '" + idReferendum + "'");
        } catch (Exception e) {
            throw new RuntimeException("Problème dans la requête");
        }
        try {
            if (!rs.next()) {
                System.out.println("Il n'y a pas d'utilisateur de login " + loginEmploye + " qui a voté au référendum d'id : " + idReferendum);
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("Probleme dans la lecture");
        }
        return true;
    }

    /*
    Permet de créer un nouveau employé dans la BD sans doublons
     */
    public boolean creerEmploye(String loginEmploye, String mdp) {
        try {
            int res = st.executeUpdate("INSERT INTO Employes (loginEmploye, mdpEmploye) VALUES ('" + loginEmploye + "', '" + mdp + "')");
            if (res == 0) {
                System.out.println("Création impossible"); // ex: utilisateur existe déjà
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("Problème dans l'insertion'");
        }
        return true;
    }

    /*
    Permet de dire que l'utilisateur a voté sur le referendum en paramètre
     */
    public boolean voter(String loginEmploye, int idReferendum) {
        try {
            int res = st.executeUpdate("INSERT INTO Voter VALUES ('" + loginEmploye + "', '" + idReferendum + "')");
            if (res == 0) {
                System.out.println("AJout impossible"); // ex: couple employe/referendum existe déjà car clé primaire
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("Problème dans l'ajout d'un vote"); // ex : employe ou referendum non existant
        }
        return true;
    }

    /*
    Enregistrement dans la BD d'un nouveau référendum
     */
    public boolean creerReferendum(int id, String nom, Date dateFin) {
        try {
            int res = st.executeUpdate("INSERT INTO Referendums (idReferendum, nomReferendum, dateFin) VALUES " +
                    "('" + id + "', '" + nom + "', '" + dateFin + "', 1)");
            if (res == 0) {
                System.out.println("AJout impossible");
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("Problème dans l'insertion d'un référendum");
        }
        return true;
    }


    // Fonction pour renvoyer la liste des référendums

    // Fonction pour récupérer le nombre de votant sur un référendum (pour decrypt)


    public void deconnexion() {
        try {
            rs.close();
            st.close();
            cn.close();
        } catch (Exception e) {
            throw new RuntimeException("Problème deconnexion");
        }
    }

}
