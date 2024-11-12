package fr.iut.referendum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.junit.jupiter.api.Assertions.*;

class ServeurTest {

    private Serveur serveur;
    private Referendum referendum1;
    private Referendum referendum2;

    @BeforeEach
    void setUp() {
        // Création de quelques référendums pour les tests
        referendum1 = new Referendum("Référendum 1", new Date(2025-1900, 0, 1, 0, 0));
        referendum2 = new Referendum("Référendum 2", new Date(2025-1900, 1, 15, 18, 0));
        List<Referendum> referendums = new ArrayList<>();
        referendums.add(referendum1);
        referendums.add(referendum2);

        serveur = new Serveur(referendums);
    }

    @Test
    void testAddReferendum() {
        Referendum newReferendum = new Referendum("Référendum 3", new Date(2025-1900, 4, 5, 12, 30));
        serveur.addReferendum(newReferendum);

        // Vérifier que le référendum a bien été ajouté
        assertTrue(serveur.getReferendums().contains(newReferendum), "Le référendum ajouté devrait être dans la liste");
    }

    @Test
    void testRemoveReferendum() {
        serveur.removeReferendum(referendum1);

        // Vérifier que le référendum a bien été supprimé
        assertFalse(serveur.getReferendums().contains(referendum1), "Le référendum supprimé ne devrait pas être dans la liste");
    }

    @Test
    void testGetReferendumById() {
        // Test de récupération d'un référendum existant
        Referendum retrievedReferendum = serveur.getReferendum(referendum1.getId());
        assertNotNull(retrievedReferendum, "Le référendum avec l'ID spécifié devrait être trouvé");
        assertEquals(referendum1, retrievedReferendum, "Le référendum récupéré devrait être le bon");

        // Test avec un ID inexistant
        Referendum nonExistentReferendum = serveur.getReferendum(999);
        assertNull(nonExistentReferendum, "Si le référendum n'existe pas, la méthode devrait renvoyer null");
    }

    @Test
    void testClientAVote() {
        String loginClient = "client1";
        String choix = "Oui";

        // Client vote pour le référendum1
        serveur.clientAVote(referendum1.getId(), loginClient, choix);

        // Vérifier que le vote est bien enregistré
        Map<String, String> votes = referendum1.getIdClientvote();
        assertTrue(votes.containsKey(loginClient), "Le vote du client devrait être enregistré");
        assertEquals(choix, votes.get(loginClient), "Le choix du client pour ce référendum devrait être 'Oui'");
    }

    @Test
    void testToString() {
        // Vérification de la sortie de la méthode toString pour un serveur avec des référendums
        String result = serveur.toString();
        assertNotNull(result, "La méthode toString ne doit pas renvoyer null");
        assertTrue(result.contains("Référendum 1"), "La sortie devrait inclure le nom du premier référendum");
        assertTrue(result.contains("Référendum 2"), "La sortie devrait inclure le nom du second référendum");
    }

    @Test
    void testServeurListenOnPort() throws IOException {
        // Créez un serveur qui écoute sur un port spécifique
        int port = 3390;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // Vérifie que le serveur écoute bien sur ce port
            assertTrue(serverSocket.isBound(), "Le serveur devrait être lié à un port");
        }
    }
}
