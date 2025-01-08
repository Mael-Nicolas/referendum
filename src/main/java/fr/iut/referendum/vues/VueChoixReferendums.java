package fr.iut.referendum.vues;

import fr.iut.referendum.Crypto.Crypto;
import fr.iut.referendum.libs.ConnexionBD;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.*;
import java.math.BigInteger;
import java.util.Scanner;

public class VueChoixReferendums extends BorderPane {

    @FXML
    private ListView<String> listViewReferendums;
    @FXML
    private Button buttonSelect, buttonReload, buttonResultat, buttonAdmin, buttonCGU, buttonML;
    @FXML
    private Label labelClient, statue;
    @FXML
    private RadioButton radioOui, radioNon;
    @FXML
    private HBox hboxBas;

    private ToggleGroup toggleGroup;

    private String login;
    private BufferedReader reader;
    private PrintWriter writer;

    private ConnexionBD connexionBD;

    public VueChoixReferendums(String login, PrintWriter writer, BufferedReader reader) {
        this.login = login;
        this.reader = reader;
        this.writer = writer;

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

        connexionBD = ConnexionBD.getInstance();
        estAdmin();

        creerBindings();
    }

    private void estAdmin() {
        if (connexionBD.estAdmin(login)) {
            buttonAdmin = new Button("Section administratreur");
            hboxBas.getChildren().add(buttonAdmin);
            buttonAdmin.setOnMouseClicked(mouseEvent -> {
                Scene scene = new Scene(new VueAdmin(login, writer, reader));
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setTitle("Section Administrateur");
                stage.setMaximized(true);
                stage.show();
                Stage currentStage = (Stage) buttonAdmin.getScene().getWindow();
                currentStage.close();
            });
        }
    }

    private void creerBindings() {
        buttonSelect.setOnMouseClicked(mouseEvent -> actionVoter(new ActionEvent()));
        buttonReload.setOnMouseClicked(mouseEvent -> {
            statue.setText("");
            loadReferendums();
        });
        buttonResultat.setOnMouseClicked(mouseEvent -> actionResultat(new ActionEvent()));
        buttonCGU.setOnMouseClicked(mouseEvent -> {
            vueCGU();
        });
        buttonML.setOnMouseClicked(mouseEvent -> {
            vueML();
        });

        loadReferendums();
    }

    private void vueCGU() {
        StringBuilder text = new StringBuilder();
        File file = new File("src/main/Légal/CGU.txt");
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                text.append(scanner.nextLine()).append("\n");
            }
        } catch (FileNotFoundException e) {
            text = new StringBuilder("Erreur de chargement des CGU");
        }

        Scene scene = new Scene(new VueText(text.toString(), "Conditions générales d'utilisation"));
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("CGU");
        stage.show();
    }

    private void vueML() {
        StringBuilder text = new StringBuilder();
        File file = new File("src/main/Légal/ML.txt");
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                text.append(scanner.nextLine()).append("\n");
            }
        } catch (FileNotFoundException e) {
            text = new StringBuilder("Erreur de chargement des mentions légales");
        }

        Scene scene = new Scene(new VueText(text.toString(), "Mentions légales"));
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Mentions légales");
        stage.show();
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

        voterReferendum(idReferendum, choix);
    }

    public void voterReferendum(int idReferendum, boolean choix) {
        try {
            writer.println("VOTER_REFERENDUM");
            writer.println(idReferendum);
            writer.println(login);

            String response = reader.readLine();
            if (response.equals("Erreur")) {
                statue.setText("Vote impossible");
            } else if (response.equals("Déjà voté")) {
                statue.setText("Vous avez déjà voté pour ce référendum");
            } else {
                // réception clé publique
                BigInteger p = new BigInteger(response);
                BigInteger g = new BigInteger(reader.readLine());
                BigInteger h = new BigInteger(reader.readLine());
                BigInteger[] pk = new BigInteger[]{p, g, h};

                // choix vote
                BigInteger choixint = choix ? BigInteger.ONE : BigInteger.ZERO;
                // cryptage
                BigInteger[] choixCrypter = Crypto.encrypt(choixint, pk);

                writer.println(choixCrypter[0]);
                writer.println(choixCrypter[1]);

                if (reader.readLine().equals("Vote enregistré")) {
                    statue.setText("Vote enregistré");
                } else {
                    statue.setText("Vote impossible");
                }
            }
        } catch (Exception e) {
            statue.setText("Erreur de liaison avec le serveur");
        } finally {
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
            return "Erreur";
        }
        return reader.readLine();
    }
}
