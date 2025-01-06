package fr.iut.referendum;

import javafx.util.converter.LocalDateTimeStringConverter;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ServerThread extends Thread {
    private Socket socket;
    private Serveur serveur;
    private ConnexionBD connexionBD;

    public ServerThread(Socket socket, Serveur serveur) {
        this.socket = socket;
        this.connexionBD = new ConnexionBD();
        this.serveur = serveur;
    }

    public void run() {
        try (InputStream input = socket.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(input));
             OutputStream output = socket.getOutputStream();
             PrintWriter writer = new PrintWriter(output, true)) {

            String text;
            while ((text = reader.readLine()) != null) {
                try {
                    Command(text, reader, writer);
                } catch (Exception e) {
                    if (e.getMessage().equals("Connection reset")) {
                        System.out.println("Le client " + socket.getInetAddress() + " s'est déconnecté.");
                        break;
                    }
                    System.err.println("Erreur lors de l'exécution d'une commande : " + e.getMessage() /* + "\n" + Arrays.toString(e.getStackTrace()) */);
                }
            }

        } catch (IOException ex) {
            System.out.println(socket.getInetAddress() + " s'est déconnecté.");
        } finally {
            try {
                if (!socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                System.err.println("Erreur lors de la fermeture du socket : " + e.getMessage() /* + "\n" + Arrays.toString(e.getStackTrace()) */);
            }
        }
    }

    private void Command(String command, BufferedReader reader, PrintWriter writer) throws IOException {
        switch (command) {
            case "GET_SERVER_INFO":
                Get_Server_Info(writer);
                break;
            case "NEW_REFERENDUM":
                New_Referendum(reader, writer);
                break;
            case "VOTER_REFERENDUM":
                Voter_Referendum(writer, reader);
                break;
            case "RESULTAT_REFERENDUM":
                Resultat_Referendum(writer, reader);
                break;
            case "CLE_PUBLIQUE_REFERENDUM":
                cle_publique_referendum(writer, reader);
                break;
            case "RESULTAT_CLIENT_REFERENDUM":
                resultat_client_referendum(writer, reader);
                break;
            case "SUPPR_REFERENDUM":
                supr_referendum(writer, reader);
                break;
            case "EXIT":
                System.out.println("Le client " + socket.getInetAddress() + " s'est déconnecté.");
                writer.println("1");
                socket.close();
                break;
            default:
                writer.println("Commande inconnue");
                break;
        }
    }

    private void supr_referendum(PrintWriter writer, BufferedReader reader) throws IOException {
        int idReferendum = Integer.parseInt(reader.readLine());
        if(true /*connexionBD.supprimerReferendum(idReferendum)*/){
            serveur.removeReferendum(idReferendum);
            writer.println("Referendum supprimé");
        } else {
            writer.println("Erreur");
        }
    }

    private void resultat_client_referendum(PrintWriter writer, BufferedReader reader) throws IOException {
        int idReferendum = Integer.parseInt(reader.readLine());
        Referendum referendum = serveur.getReferendum(idReferendum);

        while (referendum == null) {
            writer.println("Erreur");
            idReferendum = Integer.parseInt(reader.readLine());
            referendum = serveur.getReferendum(idReferendum);
        }
        writer.println("Ok");
        String resultat = referendum.getResultat();
        if (resultat == null) {
            writer.println("Resultat non disponible");
            return;
        }
        writer.println(resultat);
    }

    private void cle_publique_referendum(PrintWriter writer, BufferedReader reader) throws IOException {
        BigInteger p = new BigInteger(reader.readLine());
        BigInteger q = new BigInteger(reader.readLine());
        BigInteger h = new BigInteger(reader.readLine());
        BigInteger[] pk = {p, q, h};
        // A changer pour avoir la clé publique du referendum sur la base de donnée
        List<Referendum> referendums = serveur.getReferendums();
        for (Referendum referendum : referendums) {
            referendum.setPk(pk);
        }
        writer.println("Clé publique enregistrée");
    }

    private void Resultat_Referendum(PrintWriter writer, BufferedReader reader) throws IOException {

        int idReferendum = Integer.parseInt(reader.readLine());
        Referendum referendum = serveur.getReferendum(idReferendum);

        if (referendum == null || referendum.isOpen()) {
            writer.println("Erreur");
            return;
        }
        writer.println("Ok"); // Pas erreur

        // Test si le resultat est déjà calculé ou null
        if (referendum.getResultat() != null) {
            writer.println("Error01");
            writer.println("Resultat déjà calculé");
            writer.println(referendum.getResultat());
            return;
        }

        if (referendum.getNbVotants() == 0) {
            writer.println("Error01");
            referendum.setResultat("Egalité");
            writer.println("Nombre de votants égal à 0");
            writer.println("Résultat : " + referendum.getResultat());
            return;
        }

        writer.println("Ok"); // Pas erreur

        // envoie du resultat agregé
        BigInteger[] VotesAgreget = referendum.getVotesAgrege();
        writer.println(VotesAgreget[0]);
        writer.println(VotesAgreget[1]);
        writer.println(referendum.getNbVotants());
        // reception du resultat (oui ou non)
        String resultatReferendum = reader.readLine();
        if (resultatReferendum.equals("Erreur")) {
            return;
        }
        referendum.setResultat(resultatReferendum);
        System.out.println("Resultat du referendum " + referendum.getId() + " : " + resultatReferendum);
        writer.println("Resultat du referendum");
    }

    private void Get_Server_Info(PrintWriter writer) {
        List<Referendum> referendums = serveur.getReferendums();
        for (Referendum referendum : referendums) {
            writer.println(referendum.toString());
        }
        writer.println("fin");
    }

    private void New_Referendum(BufferedReader reader, PrintWriter writer) throws IOException {
        String nom = reader.readLine();
        LocalDateTime date = creeDate(reader);
        Referendum referendum;

        if (connexionBD.creerReferendum(nom, date) && (referendum = connexionBD.getDernierReferendum()) != null) {
            serveur.addReferendum(referendum);
            System.out.println("Referendum créé : " + referendum);
            writer.println("Referendum créé");
        } else
            writer.println("Erreur");
    }

    private LocalDateTime creeDate(BufferedReader reader) throws IOException {
        int annee = Integer.parseInt(reader.readLine());
        int mois = Integer.parseInt(reader.readLine());
        int jour = Integer.parseInt(reader.readLine());
        int heure = Integer.parseInt(reader.readLine());
        int minute = Integer.parseInt(reader.readLine());
        return LocalDateTime.of(annee, mois, jour, heure, minute);
    }

    private void Voter_Referendum(PrintWriter writer, BufferedReader reader) throws IOException {
        int idReferendum = Integer.parseInt(reader.readLine());
        Referendum referendum = serveur.getReferendum(idReferendum);

        // vérif si le referendum existe et si il est fini
        if (referendum == null || !referendum.isOpen()) {
            writer.println("Erreur");
            return;
        }

        // Envoi Clé publique du referendum
        BigInteger[] clePublique = referendum.getClePublique();
        if (clePublique == null) {
            writer.println("Erreur");
            return;
        }

        writer.println(clePublique[0]);
        writer.println(clePublique[1]);
        writer.println(clePublique[2]);
        // Enregistrement du vote
        BigInteger c1 = new BigInteger(reader.readLine());
        BigInteger c2 = new BigInteger(reader.readLine());
        BigInteger[] c = new BigInteger[]{c1, c2}; // choix crypté
        clientAVote(referendum, c);
        writer.println("Vote enregistré");
    }

    public void clientAVote(Referendum referendum, BigInteger[] c) {
        referendum.ajouterVotant();
        referendum.agregeVote(c);
    }
}