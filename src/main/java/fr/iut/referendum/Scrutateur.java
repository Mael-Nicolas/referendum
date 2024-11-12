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
    private BigInteger resChiffre;

    public Scrutateur() {
        BigInteger[] tab = Crypto.genkey();
        pk = new BigInteger[]{tab[0],tab[1],tab[2]};
        sk = tab[3];
    }

    public BigInteger[] getPk() {
        return pk;
    }

    public BigInteger recupererResultat(BigInteger resChiffre) {
        this.resChiffre = resChiffre;
    }

    public BigInteger dechiffrer(BigInteger[] agrege, int nbVotants) {
        BigInteger resultat = Crypto.decrypt(agrege, pk, sk, nbVotants);
        return resultat;
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
        BigInteger resultat = new BigInteger(reader.readLine());

        // code

        //writer.println(resultatDechiffre);
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
