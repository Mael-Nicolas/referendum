package fr.iut.referendum;

import fr.iut.referendum.vues.VueConnexion;
import fr.iut.referendum.vues.VueScrutateur;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class MainScrutateur extends Application {

    private VueConnexion vueConnexion;
    private VueScrutateur vueScrutateur;
    private Stage primaryStage;

    private final boolean avecVueConnexion = true;
    private final String hostname = "localhost";
    private final int port = 3390;

    private Scrutateur scrutateur;

    private PrintWriter writer;
    private BufferedReader reader;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        connexionScrutateur();
    }

    private void connexionScrutateur() {
        if (avecVueConnexion) {
            vueConnexion = new VueConnexion();
            vueConnexion.clientProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    // scrutateur = newValue; modifier pour Scrutateur
                    // VueConnexion.clientProperty() devrait retourner un Scrutateur
                    scrutateur = new Scrutateur();
                    demarrerChoixReferendums();
                }
            });
            vueConnexion.show();
        } else {
            demarrerChoixReferendums();
        }
    }

    private void demarrerChoixReferendums() {
        if (scrutateur == null) {
            scrutateur = new Scrutateur();
        }
        configurationSocket();
        vueScrutateur = new VueScrutateur("bonsc", writer, reader);
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
