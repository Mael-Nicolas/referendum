package fr.iut.referendum;

import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.net.*;

public class Serveur {
    List<Referendum> referendums;

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

    @Override
    public String toString() {
        String result = "";
        for (Referendum referendum : referendums) {
            result += referendum.getNom() + " : " + referendum.getChoix() + "\n";
        }
        return result;
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(6666)) {
            System.out.println("Server open on port 6666");
            Referendum r1 = new Referendum("Killian président ?", List.of("Oui", "Non"));
            Referendum r2 = new Referendum("Vincent revienne a Montpellier ?", List.of("Oui", "Non", "Blanc"));
            Referendum r3 = new Referendum("Ouverture BL3 ?", List.of("Oui", "Non", "Blanc", "Nul"));
            Serveur serveur = new Serveur(new ArrayList<>(List.of(r1, r2, r3)));
            while (true) {
                Socket socket = serverSocket.accept();
                new ServerThread(socket, serveur).start(); // écoute les messages du client, admin, scrutateur
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}