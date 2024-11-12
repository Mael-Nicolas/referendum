package fr.iut.referendum;

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
        pk = new BigInteger[]{tab[0],tab[1],tab[2]};
        sk = tab[3];
    }

    public BigInteger[] getPk() {
        return pk;
    }

    public String dechiffrer(BigInteger[] agrege, int nbVotants) {
        BigInteger resultat = Crypto.decrypt(agrege, pk, sk, nbVotants);
        if (resultat == null) {
            return "Erreur";
        }
        else if ((resultat.compareTo(BigInteger.valueOf(nbVotants).divide(BigInteger.TWO))) > 0){
            return "Oui";
        }
        return "Non";
    }

    public void run(String hostname, int port) {
        try (Socket socket = new Socket(hostname, port);
             // pour envoyer des messages au serveur
             OutputStream output = socket.getOutputStream();
             PrintWriter writer = new PrintWriter(output, true);
             // pour recevoir des messages du serveur
             InputStream input = socket.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {

            System.out.println("Pour obtenir les informations des refrendum, tapez info");
            System.out.println("Pour obtenir le résultat d'un referendum, tapez resultat");
            System.out.println("Pour envoyer la clé publique, tapez envoyePK");
            System.out.println("Pour quitter, tapez exit");

            Scanner clavier = new Scanner(System.in);
            String s;
            boolean running = true;
            while (running) {
                s = clavier.nextLine();
                if (s.equals("exit")) {
                    running = false;
                }
                else if (s.equals("info")) {
                    infoReferendum(writer, reader);
                }
                else if (s.equals("resultat")) {
                    resultatReferendum(writer, reader, clavier);
                }
                else if (s.equals("envoyePK")) {
                    envoyeClePubliqueReferendum(writer, reader);
                }
            }
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }

    private void resultatReferendum(PrintWriter writer, BufferedReader reader, Scanner clavier) throws IOException {
        writer.println("RESULTAT_REFERENDUM");
        // choix referendum
        System.out.println("Choisir ID du referendum : ");
        String idReferendum = clavier.nextLine();
        writer.println(Integer.parseInt(idReferendum));
        while (!idReferendum.matches("[0-9]+") || Integer.parseInt(idReferendum) <= 0 || reader.readLine().equals("Erreur")) {
            System.out.println("Choix invalide");
            idReferendum = clavier.nextLine();
            writer.println(Integer.parseInt(idReferendum));
        }
        BigInteger c1 = new BigInteger(reader.readLine());
        BigInteger c2 = new BigInteger(reader.readLine());
        BigInteger[] resultatAgrege = {c1, c2};
        int nbVotants = Integer.parseInt(reader.readLine());
        writer.println(dechiffrer(resultatAgrege, nbVotants)); // ICI renvoi null
        System.out.println("Serveur réponse" + reader.readLine());
    }

    private void envoyeClePubliqueReferendum(PrintWriter writer, BufferedReader reader) throws IOException {
        System.out.println("Envoie de la clé publique");
        writer.println("CLE_PUBLIQUE_REFERENDUM");
        writer.println(pk[0]);  // p
        writer.println(pk[1]);  // g
        writer.println(pk[2]);  // h
        System.out.println("Serveur réponse" + reader.readLine());
    }

    public void infoReferendum(PrintWriter writer, BufferedReader reader) throws IOException {
        writer.println("GET_SERVER_INFO");
        String response;
        while (!(response = reader.readLine()).isEmpty()) {
            System.out.println("Server response: " + response);
        }
    }

}
