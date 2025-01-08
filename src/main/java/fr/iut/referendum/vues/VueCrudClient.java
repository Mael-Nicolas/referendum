package fr.iut.referendum.vues;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class VueCrudClient extends BorderPane {
    @FXML
    private ListView<String> listViewClient;
    @FXML
    private Label label, statue;
    @FXML
    private Button buttonReload, buttonCreerClient, buttonSuprClient, buttonPassAdmin, buttonRetour;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private CheckBox estAdmin;

    private String login;
    private BufferedReader reader;
    private PrintWriter writer;

    public VueCrudClient(String login, PrintWriter writer, BufferedReader reader) {
        this.login = login;
        this.reader = reader;
        this.writer = writer;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/crudClient.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        label.setText("Admin : " + login);

        creerBindings();
    }

    private void creerBindings() {
        buttonReload.setOnMouseClicked(mouseEvent -> {
            statue.setText("");
            loadClient();
        });

        buttonCreerClient.setOnMouseClicked(mouseEvent -> {
            creerClient();
        });

        buttonSuprClient.setOnMouseClicked(mouseEvent -> {
            supprimerClient();
        });

        buttonPassAdmin.setOnMouseClicked(mouseEvent -> {
            passerAdmin();
        });

        buttonRetour.setOnMouseClicked(mouseEvent -> {
            vueAdmin();
        });

        loadClient();
    }

    private void vueAdmin() {
        Scene scene = new Scene(new VueAdmin(login, writer, reader));
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Section Administrateur");
        stage.setMaximized(true);
        stage.show();
        Stage currentStage = (Stage) buttonRetour.getScene().getWindow();
        currentStage.close();
    }

    private void passerAdmin() {
        if (listViewClient.getSelectionModel().getSelectedItem() == null) {
            statue.setText("Veuillez sélectionner un client");
            return;
        }
        String loginClient = listViewClient.getSelectionModel().getSelectedItem();
        writer.println("PASSER_ADMIN");
        writer.println(loginClient);
        try {
            if(!reader.readLine().equals("Client passé en admin")) {
                statue.setText("Erreur lors du passage en admin");
                return;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loadClient();
        statue.setText("Client passé en admin");
    }

    private void supprimerClient() {
        if (listViewClient.getSelectionModel().getSelectedItem() == null) {
            statue.setText("Veuillez sélectionner un client");
            return;
        }
        String loginClient = listViewClient.getSelectionModel().getSelectedItem();
        writer.println("SUPPRIMER_CLIENT");
        writer.println(loginClient);
        try {
            if(!reader.readLine().equals("Client supprimé")) {
                statue.setText("Erreur de suppression du client");
                return;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loadClient();
        statue.setText("Client supprimé");
    }

    private void creerClient() {
        if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            statue.setText("Veuillez remplir tous les champs");
            return;
        }
        if (estAdmin.isSelected()) {
            writer.println("CREATION_ADMIN");
            writer.println(usernameField.getText());
            writer.println(passwordField.getText());
            try {
                if (!reader.readLine().equals("Admin créé")) {
                    statue.setText("Erreur de création du client admin");
                    return;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            writer.println("CREATION_CLIENT");
            writer.println(usernameField.getText());
            writer.println(passwordField.getText());
            try {
                if (!reader.readLine().equals("Client créé")) {
                    statue.setText("Erreur de création du client");
                    return;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        usernameField.clear();
        passwordField.clear();
        loadClient();
        statue.setText("Client créé");
    }

    private void loadClient() {
        listViewClient.getItems().clear();
        try {
            writer.println("LIST_CLIENTS");
            String response;
            while (!(response = reader.readLine()).equals("fin")) {
                listViewClient.getItems().add(response);
            }
        } catch (Exception e) {
            statue.setText("Erreur de chargement des référendums");
            throw new RuntimeException(e);
        }
    }
}
