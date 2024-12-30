package fr.iut.referendum.vues;

import fr.iut.referendum.Client;
import fr.iut.referendum.ConnexionBD;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.io.*;

public class VueChoixReferendums extends BorderPane {

    @FXML
    private ListView<String> listViewReferendums;
    @FXML
    private Button buttonSelect, buttonReload, buttonResultat;
    @FXML
    private Label labelClient, statue;
    @FXML
    private RadioButton radioOui, radioNon;

    private ToggleGroup toggleGroup;

    private Client client;
    private BufferedReader reader;
    private PrintWriter writer;

    private ConnexionBD connexionBD;

    public VueChoixReferendums(Client client, PrintWriter writer, BufferedReader reader) {
        this.client = client;
        this.reader = reader;
        this.writer = writer;
        connexionBD = new ConnexionBD();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/choixReferendums.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        toggleGroup = new ToggleGroup();
        radioOui.setToggleGroup(toggleGroup);
        radioNon.setToggleGroup(toggleGroup);

        labelClient.setText("Client : " + client.getLogin());

        creerBindings();
    }

    private void creerBindings() {
        buttonSelect.setOnMouseClicked(mouseEvent -> actionVoter(new ActionEvent()));
        buttonReload.setOnMouseClicked(mouseEvent -> {
            statue.setText("");
            loadReferendums();
        } );
        buttonResultat.setOnMouseClicked(mouseEvent -> actionResultat(new ActionEvent()));

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
            if (connexionBD.aVote(client.getLogin(), idReferendum)) {
                statue.setText("Vous avez déjà voté pour ce référendum");
            }
            else if (!client.voterReferendum(writer, reader, idReferendum, choix)) {
                statue.setText("Vote impossible");
            } else {
                connexionBD.voter(client.getLogin(), idReferendum);
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

    private void actionResultat(ActionEvent event) {
        String selectedReferendum = listViewReferendums.getSelectionModel().getSelectedItem();
        if (selectedReferendum == null) {
            statue.setText("Veuillez sélectionner un référendum");
            return;
        }

        int idReferendum = Integer.parseInt(selectedReferendum.split(" - ")[0]);

        try {
            String resultat = client.resultatReferendum(writer, reader, idReferendum);
            if (resultat.equals("Oui") || resultat.equals("Non")) {
                statue.setText("Résultat : " + resultat);
            } else {
                statue.setText(resultat);
            }
        } catch (Exception e) {
            statue.setText("Erreur de liaison avec le serveur");
        }
    }

    public Client getClient() {
        return client;
    }
}
