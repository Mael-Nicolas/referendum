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
        connexionBD = ConnexionBD.getInstance();

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
        String loginScrutateur = listViewScrutateur.getSelectionModel().getSelectedItem();
        if(!connexionBD.supprimerScrutateur(loginScrutateur))
            statue.setText("Erreur de suppression du scrutateur");
        loadScrutateur();
        statue.setText("Scrutateur supprimé");
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
        String loginScrutateur = listViewScrutateur.getSelectionModel().getSelectedItem();
        /*
        A faire si on a le temps
        if (!connexionBD.modifierScrutateur(loginScrutateur, usernameField.getText(), passwordField.getText()))
            statue.setText("Erreur de modification du scrutateur");
        loadScrutateur();
        statue.setText("Scrutateur modifié");
         */
    }

    private void creerScrutateur() {
        if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            statue.setText("Veuillez remplir tous les champs");
            return;
        }
        if(!connexionBD.creerScrutateur(usernameField.getText(), passwordField.getText()))
            statue.setText("Erreur de création du scrutateur");
        loadScrutateur();
        statue.setText("Scrutateur créé");
    }

    private void loadScrutateur() {
        listViewScrutateur.getItems().clear();
        try {
            writer.println("LIST_SCRUTATEUR");
            String response;
            while (!(response = reader.readLine()).equals("fin")) {
                listViewScrutateur.getItems().add(response);
            }
        } catch (Exception e) {
            statue.setText("Erreur de chargement des référendums");
            throw new RuntimeException(e);
        }
    }
}
