package fr.iut.referendum;

import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.net.*;

public class Serveur {
    private List<Referendum> referendums;

    public Serveur(List<Referendum> referendums) {
        this.referendums = referendums;
    }

    public List<Referendum> getReferendums() {
        return referendums;
    }

    public void setReferendums(List<Referendum> referendums) {
        this.referendums = referendums;
    }

    public void addReferendum(Referendum referendum) {
        this.referendums.add(referendum);
    }

    public void removeReferendum(Referendum referendum) {
        this.referendums.remove(referendum);
    }

    public Referendum getReferendum(int id) {
        for (Referendum referendum : referendums) {
            if (referendum.getId() == id) {
                return referendum;
            }
        }
        return null;
    }

    public void clientAVote(int idReferendum, String loginClient, String choix) {
        Referendum referendum = getReferendum(idReferendum);
        referendum.getIdClientvote().put(loginClient, choix);
    }

    @Override
    public String toString() {
        String result = "";
        for (Referendum referendum : referendums) {
            result += "[" + referendum.getId() +"] " + referendum.getNom() + " : " + referendum.getChoix() + "\n";
        }
        return result;
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(3390)) {
            System.out.println("Server open on port " + serverSocket.getLocalPort());

            InetAddress adrLocale = InetAddress.getLocalHost();
            System.out.println("Adresse locale = "+adrLocale.getHostAddress());
            System.out.println("Nom de la machine locale = "+adrLocale.getHostName());


            Referendum r1 = new Referendum("Killian président ?", new ArrayList<>(List.of("Oui", "Non")));
            Referendum r2 = new Referendum("Vincent revienne a Montpellier ?", new ArrayList<>(List.of("Oui", "Non", "Blanc")));
            Referendum r3 = new Referendum("Ouverture BL3 ?", new ArrayList<>(List.of("Oui", "Non", "Blanc", "Nul")));
            Serveur serveur = new Serveur(new ArrayList<>(List.of(r1, r2, r3)));
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Connexion établie avec " + socket.getInetAddress());
                new ServerThread(socket, serveur).start();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}