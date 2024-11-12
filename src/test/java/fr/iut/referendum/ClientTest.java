package fr.iut.referendum;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.math.BigInteger;
import java.util.Scanner;

public class ClientTest {

    // Test de la création d'un client
    @Test
    public void testClientCreation() {
        // Création d'un client
        Client client = new Client("user1", "password1");

        // Vérification des informations du client
        assertEquals("user1", client.getLogin(), "Le login du client doit être correct");
        assertEquals("password1", client.getPassword(), "Le mot de passe du client doit être correct");
        assertTrue(client.id > 0, "L'ID du client doit être positif");
    }

    // Test de la méthode infoReferendum
    @Test
    public void testInfoReferendum() throws IOException {
        // Création d'un client
        Client client = new Client("user1", "password1");

        // Création des flux simulés pour la communication avec le serveur
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(outputStream, true);

        String input = "Referendum 1: Oui ou Non\nReferendum 2: Oui ou Non\n\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        // Appel de la méthode infoReferendum
        client.infoReferendum(writer, reader);

        // Vérification que les données de referendum ont bien été affichées
        String output = outputStream.toString();
        assertTrue(output.contains("GET_SERVER_INFO"), "La requête 'GET_SERVER_INFO' doit être envoyée");
    }
}
