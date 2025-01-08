package fr.iut.referendum.libs;
import org.mindrot.jbcrypt.BCrypt;

public class MotDePasse {
    private static EnvLoader instanceEnv = EnvLoader.getInstance();


    public static String hacher(String password) {
        return BCrypt.hashpw(password + instanceEnv.getEnv("poivre"), BCrypt.gensalt());
    }

    public static boolean verifierMDP(String password, String hash) {
        return BCrypt.checkpw(password + instanceEnv.getEnv("poivre"), hash);
    }
}
