package fr.iut.referendum.vues;

import fr.iut.referendum.libs.ConnexionBD;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class VueAdmin extends BorderPane {
    @FXML
    private ListView<String> listViewReferendums, listViewScrutateur;
    @FXML
    private Label label, statue;
    @FXML
    private Button buttonCreerReferendum, buttonSuprReferendum, buttonReload, buttonGererScrutateur, buttonGererClient, buttonRetour;
    @FXML
    private TextField nomReferendum, heureFin;
    @FXML
    private DatePicker datePickerFin;

    private String login;
    private BufferedReader reader;
    private PrintWriter writer;

    private ConnexionBD connexionBD;

    public VueAdmin(String login, PrintWriter writer, BufferedReader reader) {
        this.login = login;
        this.reader = reader;
        this.writer = writer;
        connexionBD = ConnexionBD.getInstance();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/admin.fxml"));
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
            loadReferendums();
            loadScrutateur();
        });

        buttonCreerReferendum.setOnMouseClicked(mouseEvent -> {
            try {
                newReferendum();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        buttonSuprReferendum.setOnMouseClicked(mouseEvent -> {
            try {
                supprimerReferendum();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        buttonGererScrutateur.setOnMouseClicked(mouseEvent -> {
            vueCrudScrutateur();
        });

        buttonGererClient.setOnMouseClicked(mouseEvent -> {
            vueCrudClient();
        });

        buttonRetour.setOnMouseClicked(mouseEvent -> {
            vueChoixReferendum();
        });

        loadReferendums();
        loadScrutateur();
    }

    private void vueChoixReferendum() {
        Scene scene = new Scene(new VueChoixReferendums(login, writer, reader));
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Choix des référendums");
        stage.show();
        Stage currentStage = (Stage) buttonRetour.getScene().getWindow();
        currentStage.close();
    }

    private void vueCrudClient() {
        Scene scene = new Scene(new VueCrudClient(login, writer, reader));
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Gestion des clients");
        stage.show();
        Stage currentStage = (Stage) buttonGererScrutateur.getScene().getWindow();
        currentStage.close();
    }

    private void vueCrudScrutateur() {
        Scene scene = new Scene(new VueCrudScrutateur(login, writer, reader));
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Gestion des scrutateurs");
        stage.show();
        Stage currentStage = (Stage) buttonGererClient.getScene().getWindow();
        currentStage.close();
    }

    private void supprimerReferendum() throws IOException {
        if (listViewReferendums.getSelectionModel().getSelectedItem() == null) {
            statue.setText("Veuillez sélectionner un référendum");
            return;
        }
        writer.println("SUPPR_REFERENDUM");
        String selectedReferendum = listViewReferendums.getSelectionModel().getSelectedItem();
        int idReferendum = Integer.parseInt(selectedReferendum.split(" - ")[0]);
        writer.println(idReferendum);
        statue.setText(reader.readLine());
        loadReferendums();
    }

    private void newReferendum() throws IOException {
        if (nomReferendum.getText().isEmpty() || datePickerFin.getValue() == null || heureFin.getText().isEmpty()) {
            statue.setText("Veuillez remplir tous les champs");
            return;
        }
        if (listViewScrutateur.getSelectionModel().getSelectedItem() == null) {
            statue.setText("Veuillez sélectionner un scrutateur");
            return;
        }
        String[] heure = heureFin.getText().split(":");
        if (!heure[0].matches("[0-9]+") || Integer.parseInt(heure[0]) < 0 || Integer.parseInt(heure[0]) > 23) {
            statue.setText("Heure invalide");
            return;
        }
        if (!heure[1].matches("[0-9]+") || Integer.parseInt(heure[1]) < 0 || Integer.parseInt(heure[1]) > 59) {
            statue.setText("Minute invalide");
            return;
        }
        writer.println("NEW_REFERENDUM");
        writer.println(nomReferendum.getText());
        writer.println(listViewScrutateur.getSelectionModel().getSelectedItem());
        writer.println(datePickerFin.getValue().getYear());
        writer.println(datePickerFin.getValue().getMonthValue());
        writer.println(datePickerFin.getValue().getDayOfMonth());
        writer.println(heure[0]);
        writer.println(heure[1]);
        statue.setText(reader.readLine());
        loadReferendums();
        nomReferendum.clear();
        datePickerFin.getEditor().clear();
        heureFin.clear();
    }

    private void loadReferendums() {
        listViewReferendums.getItems().clear();
        try {
            writer.println("GET_SERVER_INFO");
            String response;
            while (!(response = reader.readLine()).equals("fin")) {
                listViewReferendums.getItems().add(response);
            }
        } catch (Exception e) {
            statue.setText("Erreur de chargement des référendums");
            throw new RuntimeException(e);
        }
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
