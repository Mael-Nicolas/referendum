package fr.iut.referendum.vues;

import fr.iut.referendum.ConnexionBD;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class VueAdmin extends BorderPane {
    @FXML
    private ListView<String> listViewReferendums;
    // @FXML
    // private Button ;
    @FXML
    private Label labelClient, statue;
    @FXML
    private Button buttonCreerReferendum, buttonReload;
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
        connexionBD = new ConnexionBD();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/admin.fxml"));
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
            loadReferendums();
        });

        buttonCreerReferendum.setOnMouseClicked(mouseEvent -> {
            try {
                newReferendum();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        loadReferendums();
    }

    private void newReferendum() throws IOException {
        if (nomReferendum.getText().isEmpty() || datePickerFin.getValue() == null || heureFin.getText().isEmpty()) {
            statue.setText("Veuillez remplir tous les champs");
            return;
        }
        writer.println("NEW_REFERENDUM");
        writer.println(nomReferendum.getText());
        writer.println(datePickerFin.getValue().getYear());
        writer.println(datePickerFin.getValue().getMonthValue());
        writer.println(datePickerFin.getValue().getDayOfMonth());
        String[] heure = heureFin.getText().split(":");
        if (!heure[0].matches("[0-9]+") || Integer.parseInt(heure[0]) < 0 || Integer.parseInt(heure[0]) > 23) {
            statue.setText("Heure invalide");
            return;
        }
        if (!heure[1].matches("[0-9]+") || Integer.parseInt(heure[1]) < 0 || Integer.parseInt(heure[1]) > 59) {
            statue.setText("Minute invalide");
            return;
        }
        writer.println(heure[0]);
        writer.println(heure[1]);
        statue.setText(reader.readLine());
        loadReferendums();
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
}
