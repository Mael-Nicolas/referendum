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

public class VueCrudScrutateur extends BorderPane {
    @FXML
    private ListView<String> listViewScrutateur;
    @FXML
    private Label label, statue;
    @FXML
    private Button buttonReload, buttonCreerScrutateur, buttonSuprScrutateur, buttonRetour;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private String login;
    private BufferedReader reader;
    private PrintWriter writer;

    public VueCrudScrutateur(String login, PrintWriter writer, BufferedReader reader) {
        this.login = login;
        this.reader = reader;
        this.writer = writer;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/crudScrutateur.fxml"));
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
            loadScrutateur();
        });

        buttonCreerScrutateur.setOnMouseClicked(mouseEvent -> {
            creerScrutateur();
        });

        buttonSuprScrutateur.setOnMouseClicked(mouseEvent -> {
            supprimerScrutateur();
        });

        buttonRetour.setOnMouseClicked(mouseEvent -> {
            vueAdmin();
        });

        loadScrutateur();
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

    private void supprimerScrutateur() {
        String loginScrutateur = listViewScrutateur.getSelectionModel().getSelectedItem();

        if (loginScrutateur == null) {
            statue.setText("Veuillez sélectionner un scrutateur");
            return;
        }
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de suppression");
        confirmationAlert.setHeaderText("Suppression du scrutateur");
        confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer ce scrutateur ?");

        ButtonType buttonOui = new ButtonType("Oui", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonNon = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmationAlert.getButtonTypes().setAll(buttonOui, buttonNon);

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == buttonOui) {
                writer.println("SUPPRIMER_SCRUTATEUR");
                writer.println(loginScrutateur);
                try {
                    if (!reader.readLine().equals("Scrutateur supprimé")) {
                        statue.setText("Le scrutateur est relié à un référendum en cours");
                    } else {
                        loadScrutateur();
                        statue.setText("Scrutateur supprimé");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                statue.setText("Suppression annulée");
            }
        });
    }


    private void creerScrutateur() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            statue.setText("Veuillez remplir tous les champs");
            return;
        }
        if (!mdpValide(password)) {
            statue.setText("Le mot de passe doit contenir au moins 8 caractères, une majuscule, un chiffre et un caractère spécial.");
            return;
        }
        writer.println("CREATION_SCRUTATEUR");
        writer.println(username);
        writer.println(password);
        try {
            if (!reader.readLine().equals("Scrutateur créé")) {
                statue.setText("Erreur de création du scrutateur");
                return;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loadScrutateur();
        usernameField.clear();
        passwordField.clear();
        statue.setText("Scrutateur créé");
    }

    private boolean mdpValide(String password) {
        String pattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&.])[A-Za-z\\d@$!%*?&.]{8,}$";
        return password.matches(pattern);
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
