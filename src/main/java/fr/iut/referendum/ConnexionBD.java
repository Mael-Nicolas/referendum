package fr.iut.referendum;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

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

        try {
            rs = st.executeQuery("");
        } catch (Exception e) {
            throw new RuntimeException("Problème dans la requête");
        }

    }

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
