package fr.iut.referendum.vues;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;

public class VueText extends VBox {
    @FXML
    private Label labeltitre;
    @FXML
    private Text labeltext;

    public VueText(String text, String nom) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/text.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        labeltitre.setText(nom);
        labeltext.setText(text);
    }
}