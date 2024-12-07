package fr.iut.referendum;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Scanner;

public class Scrutateur {

    private final BigInteger[] pk;
    private final BigInteger sk;

    public Scrutateur() {
        BigInteger[] tab = Crypto.genkey();
        pk = new BigInteger[]{tab[0], tab[1], tab[2]};
        sk = tab[3];
    }

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
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
        System.out.println("Envoie de la clé publique");
        writer.println("CLE_PUBLIQUE_REFERENDUM");
        writer.println(pk[0]);  // p
        writer.println(pk[1]);  // g
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
