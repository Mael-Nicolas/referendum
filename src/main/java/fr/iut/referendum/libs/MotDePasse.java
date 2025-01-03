package fr.iut.referendum.libs;

import org.mindrot.jbcrypt.BCrypt;

public class MotDePasse {

    private static final String poivre = "08L9cQpCcudIGT+JTzFsXx";

    public static String hacher(String password) {
        return BCrypt.hashpw(password + poivre, BCrypt.gensalt());
    }

    public static boolean verifierMDP(String password, String hash) {
        return BCrypt.checkpw(password + poivre, hash);
    }

}
