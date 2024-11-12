package fr.iut.referendum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ReferendumTest {

    private Referendum referendum;
    private Date dateFin;

    @BeforeEach
    void setUp() {
        // Création d'une date de fin pour le test
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            dateFin = sdf.parse("30/11/2024 12:00");
        } catch (Exception e) {
            e.printStackTrace();
        }

        referendum = new Referendum("Référendum sur la Constitution", dateFin);
    }

    @Test
    void testCreationReferendum() {
        assertNotNull(referendum, "Le référendum ne doit pas être null");
        assertEquals("Référendum sur la Constitution", referendum.getNom(), "Le nom du référendum doit correspondre");
        assertEquals(2, referendum.getChoix().size(), "Le référendum doit avoir 2 choix");
        assertTrue(referendum.getChoix().contains("Oui"), "Le choix 'Oui' doit être présent");
        assertTrue(referendum.getChoix().contains("Non"), "Le choix 'Non' doit être présent");
    }

    @Test
    void testTempRestant() {
        // Nous devons tester que le temps restant est calculé correctement.
        // Ici, on suppose que la date actuelle est avant la date de fin pour que le calcul soit valide
        String tempRestant = referendum.tempRestant();
        assertNotNull(tempRestant, "Le temps restant ne doit pas être null");
        assertTrue(tempRestant.contains("jour") || tempRestant.contains("jour(s)"), "Le temps restant doit mentionner des jours");
        assertTrue(tempRestant.contains("heure") || tempRestant.contains("heure(s)"), "Le temps restant doit mentionner des heures");
        assertTrue(tempRestant.contains("minute") || tempRestant.contains("minute(s)"), "Le temps restant doit mentionner des minutes");
    }

    @Test
    void testDateFinAffichage() {
        String dateFinAffichage = referendum.dateFinAffichage();
        assertNotNull(dateFinAffichage, "La date de fin ne doit pas être null");
        assertTrue(dateFinAffichage.contains("30/11/2024 12:00"), "La date de fin doit être affichée dans le bon format");
    }

    @Test
    void testFini() {
        // Nous allons tester si la méthode 'fini' fonctionne correctement.
        Date datePassée;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            datePassée = sdf.parse("01/01/2023 12:00"); // une date passée
        } catch (Exception e) {
            e.printStackTrace();
            datePassée = new Date();
        }

        referendum = new Referendum("Référendum passé", datePassée);

        // Date passée, donc 'fini' doit être true
        assertTrue(referendum.fini(), "Le référendum doit être terminé");
    }

    @Test
    void testAjoutVotes() {
        referendum.getIdClientvote().put("client1", "Oui");
        referendum.getIdClientvote().put("client2", "Non");

        // Vérifie que le nombre de votes est bien mis à jour
        assertEquals(2, referendum.getNbVotes(), "Le nombre de votes doit être 2");

        // Vérifie que les votes sont bien stockés dans le Map
        Map<String, String> votes = referendum.getIdClientvote();
        assertTrue(votes.containsKey("client1"), "Le vote de client1 doit être présent");
        assertTrue(votes.containsKey("client2"), "Le vote de client2 doit être présent");
    }

    @Test
    void testMaxDaysInMonth() {
        // Test pour le mois de février lors d'une année bissextile (2024)
        int maxDays = referendum.getMaxDaysInMonth(2024, 2);
        assertEquals(29, maxDays, "Février 2024 doit avoir 29 jours");

        // Test pour le mois de février lors d'une année non bissextile (2023)
        maxDays = referendum.getMaxDaysInMonth(2023, 2);
        assertEquals(28, maxDays, "Février 2023 doit avoir 28 jours");

        // Test pour un mois de 31 jours (exemple, janvier)
        maxDays = referendum.getMaxDaysInMonth(2024, 1);
        assertEquals(31, maxDays, "Janvier 2024 doit avoir 31 jours");
    }
}
