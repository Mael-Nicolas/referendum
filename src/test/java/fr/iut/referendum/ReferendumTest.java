package fr.iut.referendum;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Date;
import java.util.Calendar;

public class ReferendumTest {

    @Test
    public void testReferendumCreation() {
        // Création d'un référendum
        Date dateFin = new Date(2025 - 1900, Calendar.JANUARY, 1, 0, 0);
        Referendum referendum = new Referendum("Killian président ?", dateFin);

        // Vérification des propriétés du référendum
        assertEquals("Killian président ?", referendum.getNom(), "Le nom du référendum doit être correct");
        assertNotNull(referendum.getDateFin(), "La date de fin ne doit pas être nulle");
        assertFalse(referendum.isOpen(), "Le référendum ne doit pas être ouvert au départ");
        assertEquals(0, referendum.getNbVotants(), "Le nombre de votants doit être 0 au départ");
    }

    @Test
    public void testTempRestant() {
        // Création d'un référendum avec une date de fin proche
        Date dateFin = new Date(2025 - 1900, Calendar.JANUARY, 1, 12, 30);
        Referendum referendum = new Referendum("Killian président ?", dateFin);

        // Appel de la méthode tempRestant
        String tempRestant = referendum.tempRestant();
        assertNotNull(tempRestant, "Le temps restant ne doit pas être null");
        System.out.println(tempRestant); // Pour observer la sortie

        // On vérifie que le texte contient "jour(s)", "mois", ou "an(s)" pour indiquer le temps restant
        assertTrue(tempRestant.contains("jour(s)") || tempRestant.contains("mois") || tempRestant.contains("an(s)"),
                "La sortie doit contenir une unité de temps");
    }

    @Test
    public void testAjouterVotant() {
        // Création d'un référendum
        Date dateFin = new Date(2025 - 1900, Calendar.JANUARY, 1, 0, 0);
        Referendum referendum = new Referendum("Killian président ?", dateFin);

        // Vérification que le nombre de votants est 0 au départ
        assertEquals(0, referendum.getNbVotants(), "Le nombre de votants doit être 0 au départ");

        // Ajouter un votant et vérifier
        referendum.ajouterVotant();
        assertEquals(1, referendum.getNbVotants(), "Le nombre de votants doit être 1 après l'ajout");
    }

    @Test
    public void testToString() {
        // Création d'un référendum avec une date de fin
        Date dateFin = new Date(2025 - 1900, Calendar.JANUARY, 1, 12, 30);
        Referendum referendum = new Referendum("Killian président ?", dateFin);

        // Appel de la méthode toString
        String result = referendum.toString();

        // Vérifier que le nom et la date de fin sont présents dans la chaîne
        assertTrue(result.contains("Killian président ?"), "Le nom du référendum doit être dans la sortie");
        assertTrue(result.contains("Date de fin :"), "La date de fin doit être indiquée dans la sortie");
        assertTrue(result.contains("Temps restant :"), "Le temps restant doit être indiqué dans la sortie");
    }
}
