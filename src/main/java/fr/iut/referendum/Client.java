package fr.iut.referendum;

import java.io.*;
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

    public static void info(PrintWriter writer, BufferedReader reader) throws IOException {
        writer.println("GET_SERVER_INFO");
        String response;
        while (!(response = reader.readLine()).isEmpty()) {
            System.out.println("Server response: " + response);
        }
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
                    info(writer, reader);
                }
                else if (s.equals("voter")) {
                    info(writer, reader);
                    writer.println("VOTER_REFERENDUM");

                    int nbReferendum = Integer.parseInt(reader.readLine());

                    System.out.println("Choisir ID du referendum : ");
                    String idReferendum = clavier.nextLine();
                    while (!idReferendum.matches("[0-9]+") || Integer.parseInt(idReferendum) > nbReferendum || Integer.parseInt(idReferendum) < 1) {
                        System.out.println("Choix invalide");
                        idReferendum = clavier.nextLine();
                    }
                    int idReferendumInt = Integer.parseInt(idReferendum);

                    writer.println(idReferendumInt);


                    System.out.println("Saisir vote : ");
                    String choix = clavier.nextLine();
                    writer.println(choix);

                    String response = reader.readLine();
                    while (response.equals("Erreur")) {
                        System.out.println("Id vote incorrect");
                        System.out.println("Saisir vote : ");
                        choix = clavier.nextLine();
                        writer.println(choix);
                        response = reader.readLine();
                    }

                    writer.println(this.login);

                    System.out.println("Server response: " + reader.readLine());
                }
            }
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}