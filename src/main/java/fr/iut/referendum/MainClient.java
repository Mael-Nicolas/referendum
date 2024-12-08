package fr.iut.referendum;

import fr.iut.referendum.controleur.ControleurClient;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class MainClient extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(MainClient.class.getResource("/fxml/choixReferendum.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        ControleurClient controller = fxmlLoader.getController();

        String hostname = "localhost";
        int port = 3390;

        Client client = new Client("bonsc", "12345678"); // Modif pour voter (Login)

        try {
            // Configuration SSL
            System.setProperty("javax.net.ssl.trustStore", "keystore.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "Admin!123");

            // Création d'une socket sécurisée
            SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            try (SSLSocket socket = (SSLSocket) socketFactory.createSocket(hostname, port);
                 OutputStream output = socket.getOutputStream();
                 PrintWriter writer = new PrintWriter(output, true);
                 InputStream input = socket.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {

                controller.setCommunication(writer, reader, client);
                stage.setTitle("Choix du Référendum");
                stage.setScene(scene);
                stage.show();
            }
        } catch (Exception ex) {
            throw new RuntimeException("Erreur de connexion au serveur.");
        }
    }

    public static void main(String[] args) {
        launch();
    }
}