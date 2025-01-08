package fr.iut.referendum.libs;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvLoader {
    private static EnvLoader instance;
    private Dotenv dotenv;

    private EnvLoader() {
        dotenv = Dotenv.load();
    }

    public static EnvLoader getInstance() {
        if (instance == null) {
            synchronized (EnvLoader.class) {
                if (instance == null) {
                    instance = new EnvLoader();
                }
            }
        }
        return instance;
    }

    public String getEnv(String key) {
        return dotenv.get(key);
    }
}
