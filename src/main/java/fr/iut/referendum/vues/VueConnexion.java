package fr.iut.referendum.vues;

import fr.iut.referendum.ConnexionBD;
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

    private final ObjectProperty<String> loginProperty = new SimpleObjectProperty<>();

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label loginStatusLabel;
    @FXML
    private Button buttonCreer, buttonConnecter;

    private ConnexionBD connexionBD;

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

        connexionBD = ConnexionBD.getInstance();
    }

    public void setLogin(String client) {
        this.loginProperty.set(client);
    }

    public ObjectProperty<String> loginProperty() {
        return loginProperty;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            loginStatusLabel.setText("Veuillez remplir tous les champs.");
        } else {
            // Logic to authenticate the user
            if (!connexionBD.employeConnexion(username, password)) {
                loginStatusLabel.setText("Nom d'utilisateur ou mot de passe incorrect.");
            }
            else {
                setLogin(username);
                this.close();
            }
        }
    }

    @FXML
    private void handleCreateAccount() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            loginStatusLabel.setText("Veuillez remplir tous les champs.");
        } else {
            if (!connexionBD.creerEmploye(username, password)) {
                loginStatusLabel.setText("Nom d'utilisateur ou mot de passe incorrect.");
            }
            else {
                usernameField.clear();
                passwordField.clear();
                loginStatusLabel.setText("Compte créé avec succès.");
            }
        }
    }

    @Override
    public void close() {
        connexionBD.deconnexion();
        super.close();
    }
}