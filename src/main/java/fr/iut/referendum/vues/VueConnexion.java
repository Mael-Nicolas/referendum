package fr.iut.referendum.vues;

import fr.iut.referendum.Client;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class VueConnexion extends Stage {

    private final ObjectProperty<Client> clientProperty = new SimpleObjectProperty<>();

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label loginStatusLabel;
    @FXML
    private Button buttonCreer, buttonConnecter;

    public VueConnexion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/connexionClient.fxml"));
            loader.setController(this);
            Scene scene = new Scene(loader.load());
            this.setScene(scene);
            this.setTitle("Connexion Client");
        } catch (IOException e) {
            e.printStackTrace();
        }

        buttonConnecter.setOnAction(event -> handleLogin());
        buttonCreer.setOnAction(event -> handleCreateAccount());
    }

    public Client getClient() {
        return clientProperty.get();
    }

    public void setClient(Client client) {
        this.clientProperty.set(client);
    }

    public ObjectProperty<Client> clientProperty() {
        return clientProperty;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            loginStatusLabel.setText("Veuillez remplir tous les champs.");
        } else {
            // Logic to authenticate the user
            Client client = new Client(username, password);
            setClient(client);
            loginStatusLabel.setText("Connexion réussie.");
            this.close();
        }
    }

    @FXML
    private void handleCreateAccount() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            loginStatusLabel.setText("Veuillez remplir tous les champs.");
        } else {
            // Logic to create a new account
            Client client = new Client(username, password);
            //setClient(client);
            loginStatusLabel.setText("Compte créé avec succès.");
        }
    }
}