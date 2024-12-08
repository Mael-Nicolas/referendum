package fr.iut.referendum;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    public void clientAVote(Referendum referendum, BigInteger[] c) {
        referendum.ajouterVotant();
        referendum.agregeVote(c);
    }

    @Override
    public String toString() {
        String result = "---------------------------------------------------------------------------------------------\n";
        for (Referendum referendum : referendums) {
            result += referendum + "\n" + "---------------------------------------------------------------------------------------------\n";
        }
        return result;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, KeyStoreException, FileNotFoundException, UnrecoverableKeyException, KeyManagementException {
        try {
            // Certificat SSL
            System.setProperty("javax.net.ssl.keyStore", "keystore.jks");
            System.setProperty("javax.net.ssl.keyStorePassword", "Admin!123");

            // Initialisation de SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            KeyStore keyStore = KeyStore.getInstance("JKS");

            try (FileInputStream keyStoreFile = new FileInputStream("keystore.jks")) {
                keyStore.load(keyStoreFile, "Admin!123".toCharArray());
            } catch (CertificateException e) {
                throw new RuntimeException(e);
            }

            keyManagerFactory.init(keyStore, "Admin!123".toCharArray());
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            // Utilisation d'un SSLServerSocket
            SSLServerSocketFactory serverSocketFactory = sslContext.getServerSocketFactory();
            SSLServerSocket serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(3390);

            // Affichage Info serveur
            System.out.println("Serveur sécurisé ouvert sur le port " + serverSocket.getLocalPort());
            InetAddress adrLocale = InetAddress.getLocalHost();
            System.out.println("Adresse locale = " + adrLocale.getHostAddress());
            System.out.println("Nom de la machine locale = " + adrLocale.getHostName());

            // Création de référendums de test
            Referendum r1 = new Referendum("Killian président ?", new Date(2025 - 1900, Calendar.JANUARY, 1, 0, 0));
            Referendum r2 = new Referendum("Vincent revient à Montpellier ?", new Date(2024 - 1900, Calendar.DECEMBER, 9, 13, 2));
            Referendum r3 = new Referendum("Ouverture BL3 ?", new Date(2024 - 1900, Calendar.DECEMBER, 9, 16, 10));
            Serveur serveur = new Serveur(new ArrayList<>(List.of(r1, r2, r3)));

            // Boucle de gestion des connexions clients
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Connexion sécurisée établie avec " + socket.getInetAddress());
                new ServerThread(socket, serveur).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}