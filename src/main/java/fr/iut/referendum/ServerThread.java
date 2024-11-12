package fr.iut.referendum;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;

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
                    Get_Server_Info(writer);
                }
                else if ("NEW_REFERENDUM".equals(text)) {
                    New_Referendum(reader, writer);
                }
                else if ("VOTER_REFERENDUM".equals(text)) {
                    Voter_Referendum(writer, reader);
                }
                else {
                    writer.println();
                }
            }
        } catch (IOException ex) {
            ex.getMessage();
        }
    }

    private void Get_Server_Info(PrintWriter writer) {
        writer.println(serveur.toString());
    }

    private void New_Referendum(BufferedReader reader, PrintWriter writer) throws IOException {
        String nom = reader.readLine();
        Date date = creeDate(reader);

        Referendum referendum = new Referendum(nom, date);
        serveur.addReferendum(referendum);
        System.out.println("Referendum créé : " + referendum);
        writer.println("Referendum créé");
    }

    private Date creeDate(BufferedReader reader) throws IOException {
        int annee = Integer.parseInt(reader.readLine());
        int mois = Integer.parseInt(reader.readLine());
        int jour = Integer.parseInt(reader.readLine());
        int heure = Integer.parseInt(reader.readLine());
        Date date = new Date(annee - 1900, mois-1, jour, heure, 0);
        return date;
    }

    private void Voter_Referendum(PrintWriter writer, BufferedReader reader) throws IOException {
        int idReferendum = Integer.parseInt(reader.readLine());
        Referendum referendum = serveur.getReferendum(idReferendum);

        // vérif si le referendum existe et si il est fini
        while (referendum == null || referendum.fini()) {
            writer.println("Erreur");
            idReferendum = Integer.parseInt(reader.readLine());
            referendum = serveur.getReferendum(idReferendum);
        }
        writer.println("Ok");

        // Envoi Clé publique du referendum
        BigInteger[] clePublique = referendum.getClePublique();
        writer.println(clePublique[0]);
        writer.println(clePublique[1]);
        writer.println(clePublique[2]);
        // Enregistrement du vote
        BigInteger c1 = new BigInteger(reader.readLine());
        BigInteger c2 = new BigInteger(reader.readLine());
        BigInteger[] c = new BigInteger[]{c1,c2}; // choix crypté
        String loginClient = reader.readLine();
        //serveur.clientAVote(idReferendum, loginClient, c); // enregistrement du vote chiffré à faire
        writer.println("Vote enregistré");
        System.out.println(referendum.getIdClientvote());
    }
}