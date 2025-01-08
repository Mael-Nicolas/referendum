package fr.iut.referendum.Serveur;

import fr.iut.referendum.libs.EnvLoader;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.security.*;
import java.security.cert.CertificateException;
import java.io.*;
import java.net.*;

public class Serveur {

    private static EnvLoader instanceEnv = EnvLoader.getInstance();

    public static void main(String[] args) {
        try {
            // Certificat SSL
            System.setProperty("javax.net.ssl.keyStore", "keystore.jks");
            System.setProperty("javax.net.ssl.keyStorePassword", instanceEnv.getEnv("socketmdp"));

            // Initialisation de SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            KeyStore keyStore = KeyStore.getInstance("JKS");

            try (FileInputStream keyStoreFile = new FileInputStream("keystore.jks")) {
                keyStore.load(keyStoreFile, instanceEnv.getEnv("socketmdp").toCharArray());
            } catch (CertificateException e) {
                throw new RuntimeException(e);
            }

            keyManagerFactory.init(keyStore, instanceEnv.getEnv("socketmdp").toCharArray());
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            // Utilisation d'un SSLServerSocket
            SSLServerSocketFactory serverSocketFactory = sslContext.getServerSocketFactory();
            try (SSLServerSocket serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(Integer.parseInt(instanceEnv.getEnv("port")))) {

                // Affichage Info serveur
                System.out.println("Serveur sécurisé ouvert sur le port " + serverSocket.getLocalPort());
                InetAddress adrLocale = InetAddress.getLocalHost();
                System.out.println("Adresse locale = " + adrLocale.getHostAddress());
                System.out.println("Nom de la machine locale = " + adrLocale.getHostName());

                Serveur serveur = new Serveur();

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
}