package fr.iut.referendum.vues;

import fr.iut.referendum.Client;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.*;

public class VueChoixReferendums extends VBox {

    private ListView<String> listViewReferendums;
    private Button buttonSelect, buttonReload;
    private Label choisir, statue, leChoix;
    private RadioButton radioOui, radioNon;
    private ToggleGroup toggleGroup;

    private Client client;
    private BufferedReader reader;
    private PrintWriter writer;

    public VueChoixReferendums(Client client, PrintWriter writer, BufferedReader reader) {
        setAlignment(Pos.TOP_CENTER);
        setPrefHeight(600);
        setPrefWidth(800);
        setSpacing(15);
        setPadding(new Insets(10, 10, 10, 10));

        listViewReferendums = new ListView<>();
        buttonSelect = new Button("Sélectionner");
        HBox hBox = new HBox();
        choisir = new Label("Choisir un Référendum");
        buttonReload = new Button("Reload");
        hBox.getChildren().addAll(choisir, buttonReload);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(10);
        statue = new Label();
        leChoix = new Label("Votre choix :");
        toggleGroup = new ToggleGroup();
        radioOui = new RadioButton("Oui");
        radioNon = new RadioButton("Non");
        radioOui.setToggleGroup(toggleGroup);
        radioNon.setToggleGroup(toggleGroup);

        getChildren().addAll(hBox, listViewReferendums, statue, leChoix, radioOui, radioNon, buttonSelect);

        this.client = client;
        this.reader = reader;
        this.writer = writer;

        buttonSelect.setOnMouseClicked(mouseEvent -> actionVoter(new ActionEvent()));
        buttonReload.setOnMouseClicked(mouseEvent -> loadReferendums());

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
            e.printStackTrace();
            statue.setText("Erreur de chargement des référendums");
        }
    }

    private void actionVoter(ActionEvent event) {
        String selectedReferendum = listViewReferendums.getSelectionModel().getSelectedItem();
        if (selectedReferendum == null) {
            statue.setText("Veuillez sélectionner un référendum");
            return;
        }

        if (toggleGroup.getSelectedToggle() == null) {
            statue.setText("Veuillez sélectionner une option (Oui ou Non)");
            return;
        }

        int idReferendum = Integer.parseInt(selectedReferendum.split(" - ")[0]);
        boolean choix = radioOui.isSelected();

        try {
            if (!client.voterReferendum(writer, reader, idReferendum, choix)) {
                statue.setText("Erreur lors de l'envoi du vote");
            } else {
                statue.setText("Vote enregistré");
            }
        } catch (Exception e) {
            statue.setText("Erreur de liaison avec le serveur");
        }
        finally {
            listViewReferendums.getItems().clear();
            loadReferendums();
        }
    }

    public Client getClient() {
        return client;
    }
}
