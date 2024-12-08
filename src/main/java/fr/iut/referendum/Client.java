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

//    public void run(String hostname, int port) {
//        try {
//            // Configuration SSL
//            System.setProperty("javax.net.ssl.trustStore", "keystore.jks");
//            System.setProperty("javax.net.ssl.trustStorePassword", "Admin!123");
//
//            // Création d'une socket sécurisée
//            SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
//            try (SSLSocket socket = (SSLSocket) socketFactory.createSocket(hostname, port);
//                 OutputStream output = socket.getOutputStream();
//                 PrintWriter writer = new PrintWriter(output, true);
//                 InputStream input = socket.getInputStream();
//                 BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
//
//                System.out.println("Connexion sécurisée au serveur établie.");
//                System.out.println("Pour obtenir les informations des refrendum, tapez info");
//                System.out.println("Pour voter pour un referendum, tapez voter");
//                System.out.println("Pour obtenir le résultat d'un referendum, tapez resultat");
//                System.out.println("Pour quitter, tapez exit");
//
//                Scanner clavier = new Scanner(System.in);
//                boolean running = true;
//
//                while (running) {
//                    String commande = clavier.nextLine();
//                    if (commande.equals("exit")) {
//                        running = exit(writer, reader);
//                    } else if (commande.equals("info")) {
//                        infoReferendum(writer, reader);
//                    } else if (commande.equals("voter")) {
//                        voterReferendum(writer, reader, clavier);
//                    } else if (commande.equals("resultat")) {
//                        resultatReferendum(writer, clavier, reader);
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

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

            if (reader.readLine().equals("Erreur")) {
                return false;
            }

            // réception clé publique
            BigInteger p = new BigInteger(reader.readLine());
            BigInteger g = new BigInteger(reader.readLine());
            BigInteger h = new BigInteger(reader.readLine());
            BigInteger[] pk = new BigInteger[]{p,g,h};

            // choix vote
            BigInteger choixint = choix ? BigInteger.ONE : BigInteger.ZERO;
            // cryptage
            BigInteger[] choixCrypter = Crypto.encrypt(choixint, pk);

            writer.println(choixCrypter[0]);
            writer.println(choixCrypter[1]);

            return true;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    public void voterReferendum(PrintWriter writer, BufferedReader reader, Scanner clavier) throws IOException {
//        infoReferendum(writer, reader);
//
//        writer.println("VOTER_REFERENDUM");
//
//        // choix referendum
//        System.out.println("Choisir ID du referendum : ");
//        String idReferendum = clavier.nextLine();
//        while (!idReferendum.matches("[0-9]+") || idReferendum.isEmpty() || Integer.parseInt(idReferendum) <= 0) {
//            System.out.println("Choix invalide");
//            idReferendum = clavier.nextLine();
//        }
//        writer.println(Integer.parseInt(idReferendum));
//        while (reader.readLine().equals("Erreur")) {
//            System.out.println("Choix invalide");
//            idReferendum = clavier.nextLine();
//            while (!idReferendum.matches("[0-9]+") || idReferendum.isEmpty() || Integer.parseInt(idReferendum) <= 0) {
//                System.out.println("Choix invalide");
//                idReferendum = clavier.nextLine();
//            }
//            writer.println(Integer.parseInt(idReferendum));
//        }
//
//        // réception clé publique
//        BigInteger p = new BigInteger(reader.readLine());
//        BigInteger g = new BigInteger(reader.readLine());
//        BigInteger h = new BigInteger(reader.readLine());
//        BigInteger[] pk = new BigInteger[]{p,g,h};
//
//        // choix vote
//        System.out.println("Saisir vote (Oui ou Non) : ");
//        String choix = clavier.nextLine();
//        while (!choix.equals("Oui") && !choix.equals("Non")) {
//            System.out.println("Choix invalide");
//            choix = clavier.nextLine();
//        }
//        BigInteger choixint;
//        if (choix.equals("Oui")) {
//            choixint = BigInteger.ONE;
//        } else {
//            choixint = BigInteger.ZERO;
//        }
//        // cryptage
//        BigInteger[] choixCrypter = Crypto.encrypt(choixint, pk);
//
//        writer.println(choixCrypter[0]);
//        writer.println(choixCrypter[1]);
//
//        System.out.println("Server response: " + reader.readLine());
//    }
}

