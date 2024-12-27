package fr.iut.referendum;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    public int id;
    private String login;
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    private static int idCounter = 1;

    public Client(String login, String password) {
        this.id = idCounter++;
        this.login = login;
        this.password = password;
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

    private static void resultatReferendum(PrintWriter writer, Scanner clavier, BufferedReader reader) throws IOException {
        writer.println("RESULTAT_CLIENT_REFERENDUM");
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
        System.out.println("Server response: " + reader.readLine());
    }

    public void infoReferendum(PrintWriter writer, BufferedReader reader) throws IOException {
        writer.println("GET_SERVER_INFO");
        String response;
        while (!(response = reader.readLine()).isEmpty()) {
            System.out.println("Server response: " + response);
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
}

