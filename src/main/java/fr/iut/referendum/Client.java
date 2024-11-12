package fr.iut.referendum;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    private int id;
    private String login;
    private String password;

    private static int idCounter = 1;

    public Client(String login, String password) {
        this.id = idCounter++;
        this.login = login;
        this.password = password;
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
            System.out.println("Pour voter pour un referundum, tapez voter");
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
                else if (s.equals("voter")) {
                    voterReferendum(writer, reader, clavier);
                }
            }
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }

    public void infoReferendum(PrintWriter writer, BufferedReader reader) throws IOException {
        writer.println("GET_SERVER_INFO");
        String response;
        while (!(response = reader.readLine()).isEmpty()) {
            System.out.println("Server response: " + response);
        }
    }

    private void voterReferendum(PrintWriter writer, BufferedReader reader, Scanner clavier) throws IOException {
        infoReferendum(writer, reader);

        writer.println("VOTER_REFERENDUM");

        // choix referendum
        System.out.println("Choisir ID du referendum : ");
        String idReferendum = clavier.nextLine();
        writer.println(Integer.parseInt(idReferendum));
        while (!idReferendum.matches("[0-9]+") || Integer.parseInt(idReferendum) <= 0 || reader.readLine().equals("Erreur")) {
            System.out.println("Choix invalide");
            idReferendum = clavier.nextLine();
            writer.println(Integer.parseInt(idReferendum));
        }

        // réception clé publique
        BigInteger p = new BigInteger(reader.readLine());
        BigInteger g = new BigInteger(reader.readLine());
        BigInteger h = new BigInteger(reader.readLine());
        BigInteger[] pk = new BigInteger[]{p,g,h};

        // choix vote
        System.out.println("Saisir vote (Oui ou Non) : ");
        String choix = clavier.nextLine();
        while (!choix.equals("Oui") && !choix.equals("Non")) {
            System.out.println("Choix invalide");
            choix = clavier.nextLine();
        }
        int choixint;
        if (choix.equals("Oui")) {
            choixint = 1;
        } else {
            choixint = 0;
        }
        // cryptage
        //BigInteger[] choixCrypter = Crypto.encrypt();

        //writer.println(choixCrypter);
        writer.println(this.login);

        System.out.println("Server response: " + reader.readLine());
    }
}

