package fr.iut.referendum;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.math.BigInteger;
import java.util.Base64;
import java.util.Scanner;

public class Scrutateur {
    BigInteger[] pk;
    BigInteger sk;

    public void run(String hostname, int port) {
        try {
            // Configuration SSL
            System.setProperty("javax.net.ssl.trustStore", "keystore.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "Admin!123");

            // Création d'une socket sécurisée
            SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            try (SSLSocket socket = (SSLSocket) socketFactory.createSocket(hostname, port);
                 OutputStream output = socket.getOutputStream();
                 PrintWriter writer = new PrintWriter(output, true);
                 InputStream input = socket.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                System.out.println("Pour obtenir les informations des refrendum, tapez info");
                System.out.println("Pour obtenir le résultat d'un referendum, tapez resultat");
                System.out.println("Pour creer un fichier sécurisation (pk et sk), tapez newFile");
                System.out.println("Pour utiliser un fichier existant, tapez loadFile");
                System.out.println("Pour envoyer la clé publique, tapez envoyePK");
                System.out.println("Pour quitter, tapez exit");

                Scanner clavier = new Scanner(System.in);
                boolean running = true;
                while (running) {
                    String commande = clavier.nextLine();
                    if (commande.equals("exit")) {
                        running = exit(writer, reader);
                    } else if (commande.equals("info")) {
                        infoReferendum(writer, reader);
                    } else if (commande.equals("resultat")) {
                        resultatReferendum(writer, reader, clavier);
                    } else if (commande.equals("envoyePK")) {
                        envoyeClePubliqueReferendum(writer, reader);
                    } else if (commande.equals("newFile")) {
                        newFileReferendum();
                    } else if (commande.equals("loadFile")) {
                        loadFileReferendum();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadFileReferendum() {
        System.out.println("Chargement du fichier de sécurisation");
        try {
            System.out.println("Tappez le nom du fichier (avec txt à la fin) : ");
            Scanner clavier = new Scanner(System.in);
            String fileName = clavier.nextLine();
            File file = new File(fileName);

            if (!file.exists()) {
                System.out.println("Fichier inexistant");
                return;
            }

            System.out.println("Tappez le mot de passe pour le fichier : ");
            String password = clavier.nextLine();

            if (password.length() != 16) {
                System.out.println("Le mot de passe doit avoir exactement 16 caractères.");
                return;
            }

            String encryptedData;
            try (Scanner myReader = new Scanner(file)) {
                encryptedData = myReader.nextLine();
            }

            String decryptedData = decryptData(encryptedData, password);

            String[] values = decryptedData.split("\n");
            if (values.length != 5 || !values[4].equals("Fin") ) {
                System.out.println("Mot de passe incorrect.");
                return;
            }

            pk = new BigInteger[3];
            pk[0] = new BigInteger(values[0]);
            pk[1] = new BigInteger(values[1]);
            pk[2] = new BigInteger(values[2]);
            sk = new BigInteger(values[3]);

            System.out.println("Chargement du fichier réussi.");
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement du fichier");
        } catch (Exception e) {
            System.out.println("Erreur lors du déchiffrement des données.");
        }
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

    public void newFileReferendum() {
        System.out.println("Création du fichier de sécurisation");
        try {
            System.out.println("Tappez un mot de passe pour le fichier : ");
            Scanner clavier = new Scanner(System.in);
            String password = clavier.nextLine();

            if (password.length() != 16) {
                System.out.println("Le mot de passe doit avoir exactement 16 caractères.");
                return;
            }

            BigInteger[] tab = Crypto.genkey();
            pk = new BigInteger[]{tab[0], tab[1], tab[2]};
            sk = tab[3];

            File file = new File("secure.txt");
            if (file.createNewFile()) {
                System.out.println("Fichier créé : " + file.getName());
            } else {
                System.out.println("Le fichier existe déjà.");
            }
            String dataToEncrypt = pk[0] + "\n" + pk[1] + "\n" + pk[2] + "\n" + sk + "\n" + "Fin";
            String encryptedData = encryptData(dataToEncrypt, password);

            try (FileWriter myWriter = new FileWriter("secure.txt")) {
                myWriter.write(encryptedData);
            }
            System.out.println("Écriture dans le fichier réussie.");
        } catch (IOException e) {
            System.out.println("Erreur lors de la création du fichier.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Erreur lors du chiffrement des données.");
            e.printStackTrace();
        }
    }

    private String encryptData(String data, String password) throws Exception {
        SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private static boolean exit(PrintWriter writer, BufferedReader reader) throws IOException {
        boolean running;
        System.out.println("Fermeture de la connexion.");
        writer.println("EXIT");
        // Attendre une confirmation de déconnexion
        String response = reader.readLine();
        if (response != null && response.equals("1")) {
            System.out.println("Déconnecté du serveur.");
        }
        running = false;
        return running;
    }

    private void resultatReferendum(PrintWriter writer, BufferedReader reader, Scanner clavier) throws IOException {
        if (pk == null || sk == null) {
            System.out.println("Clé non enregistrée");
            return;
        }
        writer.println("RESULTAT_REFERENDUM");

        // choix referendum
        System.out.println("Choisir ID du referendum : ");
        String idReferendum = clavier.nextLine();
        while (!idReferendum.matches("[0-9]+") || idReferendum.isEmpty() || Integer.parseInt(idReferendum) <= 0) {
            System.out.println("Choix invalide");
            idReferendum = clavier.nextLine();
        }
        writer.println(Integer.parseInt(idReferendum));
        while (reader.readLine().equals("Erreur")) {
            System.out.println("Choix invalide");
            idReferendum = clavier.nextLine();
            while (!idReferendum.matches("[0-9]+") || idReferendum.isEmpty() || Integer.parseInt(idReferendum) <= 0) {
                System.out.println("Choix invalide");
                idReferendum = clavier.nextLine();
            }
            writer.println(Integer.parseInt(idReferendum));
        }

        if (reader.readLine().equals("Error01")){
            System.out.println("Serveur réponse : " + reader.readLine());
            System.out.println("Serveur réponse : " + reader.readLine());
        }
        else {
            BigInteger c1 = new BigInteger(reader.readLine());
            BigInteger c2 = new BigInteger(reader.readLine());
            BigInteger[] resultatAgrege = {c1, c2};
            int nbVotants = Integer.parseInt(reader.readLine());
            String decrypted = dechiffrer(resultatAgrege, nbVotants);
            System.out.println("Résultat du referendum : " + decrypted);
            writer.println(decrypted);
            System.out.println("Serveur réponse : " + reader.readLine());
        }
    }

    public BigInteger[] getPk() {
        return pk;
    }

    public String dechiffrer(BigInteger[] agrege, int nbVotants) {
        System.out.println("Début du déchiffrement");

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

    private void envoyeClePubliqueReferendum(PrintWriter writer, BufferedReader reader) throws IOException {
        if (pk == null || sk == null) {
            System.out.println("Clé non enregistrée");
            return;
        }
        System.out.println("Envoie de la clé publique");
        writer.println("CLE_PUBLIQUE_REFERENDUM");
        writer.println(pk[0]);  // p
        writer.println(pk[1]);  // q
        writer.println(pk[2]);  // h
        System.out.println("Serveur réponse : " + reader.readLine());
    }

    public void infoReferendum(PrintWriter writer, BufferedReader reader) throws IOException {
        writer.println("GET_SERVER_INFO");
        String response;
        while (!(response = reader.readLine()).isEmpty()) {
            System.out.println("Serveur réponse : " + response);
        }
    }
}
