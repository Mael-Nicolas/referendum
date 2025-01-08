package fr.iut.referendum.Scrutateur;

import fr.iut.referendum.vues.VueConnexionScrutateur;
import fr.iut.referendum.vues.VueScrutateur;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class MainScrutateur extends Application {

    private VueConnexionScrutateur vueConnexionScrutateur;
    private VueScrutateur vueScrutateur;
    private Stage primaryStage;

    private final boolean avecVueConnexion = false;
    private final String hostname = "localhost";
    private final int port = 3390;

    private String loginScrutateur;

    private PrintWriter writer;
    private BufferedReader reader;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        connexionScrutateur();
    }

    private void connexionScrutateur() {
        if (avecVueConnexion) {
            vueConnexionScrutateur = new VueConnexionScrutateur();
            vueConnexionScrutateur.loginProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    loginScrutateur = newValue;
                    demarrerScrutateur();
                }
            });
            vueConnexionScrutateur.show();
        } else {
            loginScrutateur = "scrut";
            demarrerScrutateur();
        }
    }

    private void demarrerScrutateur() {
        if (loginScrutateur == null) {
            return;
        }
        configurationSocket();
        vueScrutateur = new VueScrutateur(loginScrutateur, writer, reader);
        Scene sceneVueScrutateur = new Scene(vueScrutateur);
        primaryStage.setScene(sceneVueScrutateur);
        primaryStage.setTitle("Choix Referendums");
        primaryStage.show();
    }

    private void configurationSocket() {
        // Configuration SSL
        System.setProperty("javax.net.ssl.trustStore", "keystore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "Admin!123");

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
