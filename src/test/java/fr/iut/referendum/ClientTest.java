package fr.iut.referendum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    private Client client;

    @BeforeEach
    void setUp() {
        client = new Client("client1", "password1");
    }

    @Test
    void testInfoReferendum() throws IOException {
        // Simule la sortie du client et la réponse du serveur
        ByteArrayOutputStream simulatedOutput = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(simulatedOutput, true);
        String serverResponse = "Referendum information\n\n";  // Réponse simulée du serveur
        BufferedReader reader = new BufferedReader(new StringReader(serverResponse));

        // Exécute la méthode
        client.infoReferendum(writer, reader);

        // Vérifie le contenu de la sortie
        String output = simulatedOutput.toString();
        assertTrue(output.contains("GET_SERVER_INFO"), "La commande GET_SERVER_INFO doit être envoyée");
        assertTrue(output.contains("Server response: Referendum information"), "La réponse du serveur doit être affichée");
    }

    @Test
    void testVoterReferendum() throws IOException {
        // Simule l'entrée de l'utilisateur et la sortie du client
        ByteArrayOutputStream simulatedOutput = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(simulatedOutput, true);

        // Prépare la réponse du serveur pour les informations de clé publique et validation
        String serverResponses = "23\n5\n15\n" + // Valeurs de clé publique p, g, h
                "Vote reçu\n"; // Réponse finale du serveur
        BufferedReader reader = new BufferedReader(new StringReader(serverResponses));

        // Entrée utilisateur simulée : ID du référendum et choix de vote "Oui"
        String simulatedInput = "1\nOui\n";
        Scanner scanner = new Scanner(simulatedInput);

        // Exécute la méthode de vote
        client.voterReferendum(writer, reader, scanner);

        // Vérifie que les informations envoyées correspondent aux attentes
        String output = simulatedOutput.toString();
        assertTrue(output.contains("VOTER_REFERENDUM"), "La commande VOTER_REFERENDUM doit être envoyée");
        assertTrue(output.contains("Vote reçu"), "La confirmation du serveur doit être affichée");
    }

    @Test
    void testIdCounter() {
        // Crée un second client pour vérifier l'incrémentation de l'ID
        Client client2 = new Client("client2", "password2");
        assertEquals(client.id + 1, client2.id, "L'ID du second client doit être l'incrément de celui du premier client");
    }
}
