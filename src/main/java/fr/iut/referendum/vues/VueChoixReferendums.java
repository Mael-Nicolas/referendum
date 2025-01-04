package fr.iut.referendum.vues;

import fr.iut.referendum.Client;
import fr.iut.referendum.ConnexionBD;
import fr.iut.referendum.Crypto;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.io.*;
import java.math.BigInteger;

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

    private String login;
    private BufferedReader reader;
    private PrintWriter writer;

    private ConnexionBD connexionBD;

    public VueChoixReferendums(String login, PrintWriter writer, BufferedReader reader) {
        this.login = login;
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

        labelClient.setText("Client : " + login);

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
            if (connexionBD.aVote(login, idReferendum)) {
                statue.setText("Vous avez déjà voté pour ce référendum");
            }
            else if (!voterReferendum(writer, reader, idReferendum, choix)) {
                statue.setText("Vote impossible");
            } else {
                connexionBD.voter(login, idReferendum);
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

    public boolean voterReferendum(PrintWriter writer, BufferedReader reader, int idReferendum, boolean choix) {
        try {
            writer.println("VOTER_REFERENDUM");
            writer.println(idReferendum);

            String response = reader.readLine();
            if ("Erreur".equals(response)) {
                return false;
            }

            // réception clé publique
            BigInteger p = new BigInteger(response);
            BigInteger g = new BigInteger(reader.readLine());
            BigInteger h = new BigInteger(reader.readLine());
            BigInteger[] pk = new BigInteger[]{p,g,h};

            // choix vote
            BigInteger choixint = choix ? BigInteger.ONE : BigInteger.ZERO;
            // cryptage
            BigInteger[] choixCrypter = Crypto.encrypt(choixint, pk);

            writer.println(choixCrypter[0]);
            writer.println(choixCrypter[1]);

            return reader.readLine().equals("Vote enregistré");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
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
            String resultat = resultatReferendum(writer, reader, idReferendum);
            if (resultat.equals("Oui") || resultat.equals("Non")) {
                statue.setText("Résultat : " + resultat);
            } else {
                statue.setText(resultat);
            }
        } catch (Exception e) {
            statue.setText("Erreur de liaison avec le serveur");
        }
    }

    public String resultatReferendum(PrintWriter writer, BufferedReader reader, int idReferendum) throws IOException {
        writer.println("RESULTAT_CLIENT_REFERENDUM");
        writer.println(idReferendum);
        if (reader.readLine().equals("Erreur")) {
            return null;
        }
        return reader.readLine();
    }
}
