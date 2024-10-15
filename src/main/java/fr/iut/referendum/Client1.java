package fr.iut.referendum;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client1 {
    int id;
    String login;
    String password;

    static int idCounter = 1;

    public Client1(String login, String password) {
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

    public static void main(String[] args) {
        String hostname = "localhost"; // Remplacez par l'adresse IP du serveur
        int port = 6666;

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

                    System.out.println("Choisir ID du referendum : ");
                    String idReferendum = clavier.nextLine();
                    writer.println(idReferendum);

                    System.out.println("Saisir vote : ");
                    String choix = clavier.nextLine();
                    writer.println(choix);

                    String response = reader.readLine();
                    while (response.equals("Erreur")) {
                        System.out.println("Id referendum ou vote incorrect");
                        System.out.println("Choisir ID du referendum : ");
                        idReferendum = clavier.nextLine();
                        writer.println(idReferendum);

                        System.out.println("Saisir vote : ");
                        choix = clavier.nextLine();
                        writer.println(choix);

                        response = reader.readLine();
                    }

                    writer.println("1");// à modifier pour prendre en compte l'id du client

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