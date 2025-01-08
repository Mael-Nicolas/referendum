package fr.iut.referendum.vues;

import fr.iut.referendum.Crypto.Crypto;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.FileChooser;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.util.Base64;
import java.util.Scanner;

public class VueScrutateur extends BorderPane {

    @FXML
    private ListView<String> listViewReferendums;
    @FXML
    private Label labelClient,statue,labelfichier;
    @FXML
    private Button buttonNewFile, buttonEnvoyer, buttonResultat, buttonReload, buttonLoadFile, buttonCGU, buttonML;
    @FXML
    private TextField nomfichier;
    @FXML
    private PasswordField mdpfichier;

    private String login;
    private BufferedReader reader;
    private PrintWriter writer;

    private BigInteger[] pk;
    private BigInteger sk;

    public VueScrutateur(String login, PrintWriter writer, BufferedReader reader) {
        this.login = login;
        this.reader = reader;
        this.writer = writer;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/scrutateur.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        labelClient.setText("Scrutateur : " + login);
        labelfichier.setText("Fichier sélétionner : Aucun");

        loadReferendumsScrutateur();
        creerBindings();
    }

    private void creerBindings() {
        buttonReload.setOnMouseClicked(mouseEvent -> {
            loadReferendumsScrutateur();
        } );
        
        buttonEnvoyer.setOnMouseClicked(mouseEvent -> {
            try {
                envoyerCle();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        buttonNewFile.setOnMouseClicked(mouseEvent -> {
            newFileReferendum();
        });

        buttonLoadFile.setOnMouseClicked(mouseEvent -> {
            loadFile();
        });

        buttonResultat.setOnMouseClicked(mouseEvent -> {
            try {
                resultatReferendum(writer, reader);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        buttonCGU.setOnMouseClicked(mouseEvent -> {
            vueCGU();
        });
        buttonML.setOnMouseClicked(mouseEvent -> {
            vueML();
        });
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

    private void loadFile() {
        statue.setText("Chargement du fichier de sécurisation");

        try {
            // Utilisation d'un FileChooser pour sélectionner le fichier
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Sélectionner un fichier de sécurisation");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Fichiers texte", "*.txt"),
                    new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
            );

            // Ouvre la boîte de dialogue pour sélectionner un fichier
            File file = fileChooser.showOpenDialog(this.getScene().getWindow());

            if (file == null) {
                statue.setText("Aucun fichier sélectionné");
                return;
            }

            if (mdpfichier.getText().isEmpty()) {
                statue.setText("Mot de passe vide");
                return;
            }

            String password = mdpfichier.getText();

            if (password.length() != 16) {
                statue.setText("Le mot de passe doit avoir exactement 16 caractères.");
                return;
            }

            String encryptedData;
            try (Scanner myReader = new Scanner(file)) {
                encryptedData = myReader.nextLine();
            }

            String decryptedData = decryptData(encryptedData, password);

            String[] values = decryptedData.split("\n");
            if (values.length != 5 || !values[4].equals("Fin")) {
                statue.setText("Mot de passe incorrect.");
                return;
            }

            pk = new BigInteger[3];
            pk[0] = new BigInteger(values[0]);
            pk[1] = new BigInteger(values[1]);
            pk[2] = new BigInteger(values[2]);
            sk = new BigInteger(values[3]);

            statue.setText("Chargement du fichier réussi.");
            labelfichier.setText("Fichier sélectionné : " + file.getName());
        } catch (IOException e) {
            statue.setText("Erreur lors du chargement du fichier.");
        } catch (Exception e) {
            statue.setText("Erreur lors du déchiffrement des données.");
        }
    }


    public void newFileReferendum() {
        statue.setText("Création du fichier de sécurisation");
        try {
            if (nomfichier.getText().isEmpty()) {
                statue.setText("Nom de fichier vide");
                return;
            }

            String fileName = nomfichier.getText();
            if (!fileName.endsWith(".txt")) {
                fileName += ".txt";
            }

            File file = new File(fileName);

            if (file.exists()) {
                statue.setText("Fichier existant");
                return;
            }

            if (mdpfichier.getText().isEmpty()) {
                statue.setText("Mot de passe vide");
                return;
            }

            String password = mdpfichier.getText();

            if (password.length() != 16) {
                statue.setText("Le mot de passe doit avoir exactement 16 caractères.");
                return;
            }
            BigInteger[] tab = Crypto.genkey();
            pk = new BigInteger[]{tab[0], tab[1], tab[2]};
            sk = tab[3];

            if (file.createNewFile()) {
                statue.setText("Fichier créé : " + file.getName());
            }

            String dataToEncrypt = pk[0] + "\n" + pk[1] + "\n" + pk[2] + "\n" + sk + "\n" + "Fin";
            String encryptedData = encryptData(dataToEncrypt, password);

            try (FileWriter myWriter = new FileWriter(file)) {
                myWriter.write(encryptedData);
            }
            statue.setText("Écriture dans le fichier réussie.");
            labelfichier.setText("Fichier sélectionné : " + file.getName());
        } catch (IOException e) {
            statue.setText("Erreur lors de la création du fichier.");
            e.printStackTrace();
        } catch (Exception e) {
            statue.setText("Erreur lors du chiffrement des données.");
            e.printStackTrace();
        }
    }


    private void envoyerCle() throws IOException {
        if (listViewReferendums.getSelectionModel().getSelectedItem() == null) {
            statue.setText("Veuillez sélectionner un référendum");
            return;
        }
        if (pk == null || sk == null) {
            statue.setText("Clé non enregistrée");
        } else {
            statue.setText("Envoie de la clé publique");
            writer.println("CLE_PUBLIQUE_REFERENDUM");
            String idReferendum = listViewReferendums.getSelectionModel().getSelectedItem().split(" - ")[0];
            writer.println(idReferendum);
            if (reader.readLine().equals("Erreur")) {
                statue.setText("Clé publique déjà enregistrée ou référendum terminé");
                return;
            }
            writer.println(pk[0]);  // p
            writer.println(pk[1]);  // q
            writer.println(pk[2]);  // h
            statue.setText(reader.readLine());
        }
        loadReferendumsScrutateur();
    }

    private void loadReferendumsScrutateur() {
        listViewReferendums.getItems().clear();
        try {
            writer.println("GET_SERVER_INFO_SCRUTATEUR");
            writer.println(login);
            String response;
            while (!(response = reader.readLine()).equals("fin")) {
                listViewReferendums.getItems().add(response);
            }
        } catch (Exception e) {
            statue.setText("Erreur de chargement des référendums");
            throw new RuntimeException(e);
        }
    }

    private void resultatReferendum(PrintWriter writer, BufferedReader reader) throws IOException {
        if (pk == null || sk == null) {
            statue.setText("Clé non enregistrée");
            return;
        }

        // choix referendum
        String selectedReferendum = listViewReferendums.getSelectionModel().getSelectedItem();
        if (selectedReferendum == null) {
            statue.setText("Veuillez sélectionner un référendum");
            return;
        }
        int idReferendum = Integer.parseInt(selectedReferendum.split(" - ")[0]);
        writer.println("RESULTAT_REFERENDUM");
        writer.println(idReferendum);
        if (reader.readLine().equals("Erreur")) {
            statue.setText("Choix invalide");
            return;
        }
        if (reader.readLine().equals("Error01")){
            statue.setText("Resultat déjà calculé : " + reader.readLine());
            return;
        }
        if (reader.readLine().equals("Error02")){
            statue.setText("Resultat : Egalité (Nombre de votants égal à 0)");
            return;
        }
        else {
            BigInteger c1 = new BigInteger(reader.readLine());
            BigInteger c2 = new BigInteger(reader.readLine());
            BigInteger[] resultatAgrege = {c1, c2};
            int nbVotants = Integer.parseInt(reader.readLine());

            String decrypted = dechiffrer(resultatAgrege, nbVotants);
            writer.println(decrypted);
            if (decrypted.equals("Erreur")) {
                statue.setText("Erreur lors du déchiffrement");
                return;
            }
            statue.setText("Resultat du referendum : " + decrypted);
        }
    }

    public String dechiffrer(BigInteger[] agrege, int nbVotants) {
        statue.setText("Début du déchiffrement");

        BigInteger resultat = Crypto.decrypt(agrege, pk, sk, nbVotants);

        long nbVotantsDiv2 = nbVotants / 2;
        if (resultat == null) {
            return "Erreur";
        } else if (resultat.compareTo(BigInteger.valueOf(nbVotantsDiv2)) == 0 && nbVotants % 2 == 0) {
            return "Egalité";
        }
        else if (resultat.compareTo(BigInteger.valueOf(nbVotantsDiv2)) > 0) {
            return "Oui";
        }
        return "Non";
    }

    private String decryptData(String encryptedData, String password) throws Exception {
        try {
            SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedBytes);
        } catch (Exception e) {
            return null;
        }
    }

    private String encryptData(String data, String password) throws Exception {
        SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
}
