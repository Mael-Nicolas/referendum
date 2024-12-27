package fr.iut.referendum;

import fr.iut.referendum.vues.VueChoixReferendums;
import fr.iut.referendum.vues.VueConnexion;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class MainClient extends Application {

    private VueConnexion vueConnexion;
    private VueChoixReferendums vueChoixReferendums;
    private Stage primaryStage;

    private final boolean avecVueConnexion = false;
    private final String hostname = "localhost";
    private final int port = 3390;

    private Client client;

    private PrintWriter writer;
    private BufferedReader reader;


    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        connexionClient();
    }

    public void connexionClient() {
        if (avecVueConnexion) {
            vueConnexion = new VueConnexion();
            vueConnexion.show();
        }
        else {
            demarrerChoixReferendums();
        }
    }

    public void demarrerChoixReferendums() {
        if (avecVueConnexion) {
            client = vueConnexion.getClient();
        }
        else {
            client = new Client("bonsc", "12345678"); // Modif pour voter (Login)
        }
        configurationSocket();
        vueChoixReferendums = new VueChoixReferendums(client, writer, reader);
        Scene sceneVueChoixReferendums = new Scene(vueChoixReferendums);
        primaryStage.setScene(sceneVueChoixReferendums);
        primaryStage.setTitle("Choix Referendums");
        primaryStage.show();
    }

    public void configurationSocket() {
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