package fr.iut.referendum;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Admin {
    private String login;
    private String password;

    public Admin(String login, String password) {
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
            System.out.println("Pour creer un referundum, tapez new");
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
                    writer.println("GET_SERVER_INFO");
                    String response;
                    while (!(response = reader.readLine()).isEmpty()) {
                        System.out.println("Server response: " + response);
                    }
                }
                else if (s.equals("new")) {
                    writer.println("NEW_REFERENDUM");

                    System.out.println("Nom du referendum : ");
                    String nom = clavier.nextLine();
                    writer.println(nom);
                    /*
                    System.out.println("Nombre de choix : ");
                    String nbChoixString = clavier.nextLine();
                    while (!nbChoixString.matches("[0-9]+")) {
                        System.out.println("Nombre de choix invalide");
                        nbChoixString = clavier.nextLine();
                    }
                    int nbChoix = Integer.parseInt(nbChoixString); ;
                    writer.println(nbChoix);

                    for (int i = 1; i < nbChoix+1; i++) {
                        System.out.println("Choix " + i + " : ");
                        String choix = clavier.nextLine();
                        writer.println(choix);
                    }
                    */
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
