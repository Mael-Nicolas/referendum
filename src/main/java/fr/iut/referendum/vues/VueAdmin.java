package fr.iut.referendum.vues;

import fr.iut.referendum.ConnexionBD;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class VueAdmin extends BorderPane {
    @FXML
    private ListView<String> listViewReferendums;
    // @FXML
    // private Button ;
    @FXML
    private Label labelClient, statue;

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
