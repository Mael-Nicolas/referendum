package fr.iut.referendum;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.List;
import java.io.*;
import java.net.*;

public class Serveur {
    private ConnexionBD connexionBD;

    private List<Referendum> referendums;

    public Serveur(List<Referendum> referendums) {
        this.referendums = referendums;
        connexionBD = new ConnexionBD();
    }

    public List<Referendum> getReferendums() {
        return referendums;
    }

    public void addReferendum(Referendum referendum) {
        this.referendums.add(referendum);
    }

    public Referendum getReferendum(int id) {
        for (Referendum referendum : referendums) {
            if (referendum.getId() == id) {
                return referendum;
            }
        }
        return null;
    }

    public static void main(String[] args) {
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
            try (SSLServerSocket serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(3390)) {

                // Affichage Info serveur
                System.out.println("Serveur sécurisé ouvert sur le port " + serverSocket.getLocalPort());
                InetAddress adrLocale = InetAddress.getLocalHost();
                System.out.println("Adresse locale = " + adrLocale.getHostAddress());
                System.out.println("Nom de la machine locale = " + adrLocale.getHostName());

                // Affectation des referendums de la BD
                List<Referendum> referendums = new ConnexionBD().getReferendums();
                Serveur serveur = new Serveur(referendums);

                // Boucle de gestion des connexions clients
                while (true) {
                    Socket socket = serverSocket.accept();
                    System.out.println("Connexion sécurisée établie avec " + socket.getInetAddress());
                    new ServerThread(socket, serveur).start();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création du serveur", e);
        }
    }

    public void removeReferendum(int idReferendum) {
        Referendum referendum = getReferendum(idReferendum);
        if (referendum != null) {
            referendums.remove(referendum);
        }
    }
}