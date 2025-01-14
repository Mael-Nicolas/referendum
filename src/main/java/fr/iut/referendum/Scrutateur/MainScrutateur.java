package fr.iut.referendum.Scrutateur;

import fr.iut.referendum.libs.EnvLoader;
import fr.iut.referendum.vues.VueConnexionScrutateur;
import fr.iut.referendum.vues.VueScrutateur;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.*;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class MainScrutateur extends Application {

    private VueConnexionScrutateur vueConnexionScrutateur;
    private VueScrutateur vueScrutateur;
    private Stage primaryStage;
    private static EnvLoader instanceEnv = EnvLoader.getInstance();
    private final boolean avecVueConnexion = true;


    private String loginScrutateur;

    private PrintWriter writer;
    private BufferedReader reader;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        connexionScrutateur();
    }

    private void connexionScrutateur() {
        configurationSocket();
        if (writer == null || reader == null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Erreur");
            confirmationAlert.setHeaderText("Erreur lors de la connexion au serveur.");
            confirmationAlert.showAndWait();
            return;
        }
        if (avecVueConnexion) {
            vueConnexionScrutateur = new VueConnexionScrutateur(writer, reader);
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
        vueScrutateur = new VueScrutateur(loginScrutateur, writer, reader);
        Scene sceneVueScrutateur = new Scene(vueScrutateur);
        primaryStage.setScene(sceneVueScrutateur);
        primaryStage.setTitle("Choix Referendums");
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private void configurationSocket() {
        // Configuration SSL
        System.setProperty("javax.net.ssl.trustStore", "keystore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", instanceEnv.getEnv("socketmdp"));

        SSLSocket socket = null;
        try {
            // Création d'une socket sécurisée
            SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) socketFactory.createSocket(instanceEnv.getEnv("adresse"), Integer.parseInt(instanceEnv.getEnv("port")));
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            this.writer = writer;
            this.reader = reader;
        } catch (Exception ex) {
            System.out.println("Erreur lors de la connexion au serveur.");
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
