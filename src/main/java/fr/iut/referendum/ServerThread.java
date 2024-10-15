package fr.iut.referendum;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ServerThread extends Thread {
    private Socket socket;
    private Serveur serveur;

    public ServerThread(Socket socket, Serveur serveur) {
        this.socket = socket;
        this.serveur = serveur;
    }

    public void run() {
        try (InputStream input = socket.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(input));
             OutputStream output = socket.getOutputStream();
             PrintWriter writer = new PrintWriter(output, true)) {

            String text;
            while ((text = reader.readLine()) != null) {
                if ("GET_SERVER_INFO".equals(text)) {
                    writer.println(serveur.toString());
                }
                else if ("NEW_REFERENDUM".equals(text)) {
                    String nom = reader.readLine();
                    int nbChoix = Integer.parseInt(reader.readLine());
                    ArrayList<String> choix = new ArrayList<>();
                    for (int i = 1; i < nbChoix+1; i++) {
                        String choixi = reader.readLine();
                        choix.add(choixi);
                    }
                    serveur.addReferendum(new Referendum(nom, choix));
                    writer.println("Referendum créé");
                }
                else if ("VOTER_REFERENDUM".equals(text)) {
                    writer.println(serveur.getReferendums().size());
                    int idReferendum = Integer.parseInt(reader.readLine());
                    Referendum referendum = serveur.getReferendum(idReferendum);

                    String choixVote = reader.readLine();

                    while (!referendum.getChoix().contains(choixVote)) {
                        writer.println("Erreur");
                        choixVote = reader.readLine();
                    }
                    writer.println("Ok"); // Doit renvoyer "Ok" pour continuer sinon il renvoie "Erreur"
                    String loginClient = reader.readLine();
                    serveur.clientAVote(idReferendum, loginClient, choixVote);
                    writer.println("Vote enregistré");
                    System.out.println(referendum.getIdClientvote());
                }
                else {
                    writer.println();
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}