package fr.iut.referendum.vues;

import fr.iut.referendum.libs.ConnexionBD;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

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

    private PrintWriter writer;
    private BufferedReader reader;

    public VueConnexion(PrintWriter writer, BufferedReader reader) {
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

        this.reader = reader;
        this.writer = writer;
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
            try {
                writer.println("CONNEXION_CLIENT");
                writer.println(username);
                writer.println(password);
                if (!reader.readLine().equals("Connexion réussie")) {
                    loginStatusLabel.setText("Nom d'utilisateur ou mot de passe incorrect.");
                }
                else {
                    setLogin(username);
                    this.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
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
            try {
                writer.println("CREATION_CLIENT");
                writer.println(username);
                writer.println(password);
                if (!reader.readLine().equals("Client créé")) {
                    loginStatusLabel.setText("Nom d'utilisateur ou mot de passe incorrect.");
                }
                else {
                    usernameField.clear();
                    passwordField.clear();
                    loginStatusLabel.setText("Compte créé avec succès.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}