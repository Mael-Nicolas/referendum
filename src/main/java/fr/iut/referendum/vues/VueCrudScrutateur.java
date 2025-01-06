package fr.iut.referendum.vues;

import fr.iut.referendum.ConnexionBD;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class VueCrudScrutateur extends BorderPane {
    @FXML
    private ListView<String> listViewScrutateur;
    @FXML
    private Label labelClient, statue;
    @FXML
    private Button buttonReload, buttonCreerScrutateur, buttonModifScrutateur, buttonSuprScrutateur;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private String login;
    private BufferedReader reader;
    private PrintWriter writer;

    private ConnexionBD connexionBD;

    public VueCrudScrutateur(String login, PrintWriter writer, BufferedReader reader) {
        this.login = login;
        this.reader = reader;
        this.writer = writer;
        connexionBD = new ConnexionBD();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/crudScrutateur.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        labelClient.setText("Admin : " + login);

        creerBindings();
    }

    private void creerBindings() {
        buttonReload.setOnMouseClicked(mouseEvent -> {
            statue.setText("");
            loadScrutateur();
        });

        buttonCreerScrutateur.setOnMouseClicked(mouseEvent -> {
            creerScrutateur();
        });

        buttonModifScrutateur.setOnMouseClicked(mouseEvent -> {
            modifierScrutateur();
        });

        buttonSuprScrutateur.setOnMouseClicked(mouseEvent -> {
            supprimerScrutateur();
        });

        loadScrutateur();
    }

    private void supprimerScrutateur() {
        if (listViewScrutateur.getSelectionModel().getSelectedItem() == null) {
            statue.setText("Veuillez sélectionner un scrutateur");
            return;
        }
        // connexionBD.supprimerScrutateur(listViewScrutateur.getSelectionModel().getSelectedItem());
    }

    private void modifierScrutateur() {
        if (listViewScrutateur.getSelectionModel().getSelectedItem() == null) {
            statue.setText("Veuillez sélectionner un scrutateur");
            return;
        }
        if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            statue.setText("Veuillez remplir tous les champs");
            return;
        }
        // connexionBD.modifierScrutateur(listViewScrutateur.getSelectionModel().getSelectedItem());
    }

    private void creerScrutateur() {
        if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            statue.setText("Veuillez remplir tous les champs");
            return;
        }
        // connexionBD.creerScrutateur(usernameField.getText(), passwordField.getText());
        statue.setText("Scrutateur créé");
    }

    private void loadScrutateur() {
        listViewScrutateur.getItems().clear();
        // listViewScrutateur.getItems().addAll(connexionBD.getScrutateurs());
    }
}
