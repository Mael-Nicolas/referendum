package fr.iut.referendum.Client;

import fr.iut.referendum.libs.EnvLoader;
import fr.iut.referendum.vues.VueChoixReferendums;
import fr.iut.referendum.vues.VueConnexionClient;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class MainClient extends Application {

    private VueConnexionClient vueConnexionClient;
    private VueChoixReferendums vueChoixReferendums;
    private Stage primaryStage;

    private final boolean avecVueConnexion = true;
    private final String hostname = "localhost";
    private final int port = 3390;
    private static EnvLoader instanceEnv = EnvLoader.getInstance();

    private String loginClient;

    private PrintWriter writer;
    private BufferedReader reader;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        connexionClient();
    }

    public void connexionClient() {
        configurationSocket();
        if (avecVueConnexion) {
            vueConnexionClient = new VueConnexionClient(writer, reader);
            vueConnexionClient.loginProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    loginClient = newValue;
                    demarrerChoixReferendums();
                }
            });
            vueConnexionClient.show();
        } else {
            loginClient = "bonsc";
            demarrerChoixReferendums();
        }
    }

    public void demarrerChoixReferendums() {
        if (loginClient == null) {
            return;
        }
        vueChoixReferendums = new VueChoixReferendums(loginClient, writer, reader);
        Scene sceneVueChoixReferendums = new Scene(vueChoixReferendums);
        primaryStage.setScene(sceneVueChoixReferendums);
        primaryStage.setTitle("Choix Referendums");
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public void configurationSocket() {
        // Configuration SSL
        System.setProperty("javax.net.ssl.trustStore", "keystore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", instanceEnv.getEnv("socketmdp"));

        SSLSocket socket = null;
        try {
            // Création d'une socket sécurisée
            SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) socketFactory.createSocket(hostname, port);
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            this.writer = writer;
            this.reader = reader;
        } catch (Exception ex) {
            throw new RuntimeException("Erreur de connexion au serveur.", ex);
        } finally {
            if (socket != null && socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}