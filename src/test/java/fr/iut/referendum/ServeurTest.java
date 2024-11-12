package fr.iut.referendum;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Calendar;

public class ServeurTest {

    @Test
    public void testAddAndGetReferendum() {
        // Création d'une liste de référendums
        Referendum r1 = new Referendum("Killian président ?", new Date(2025-1900, Calendar.JANUARY, 1, 0, 0));
        Referendum r2 = new Referendum("Vincent revienne a Montpellier ?", new Date(2028-1900, Calendar.FEBRUARY, 29, 20, 0));
        List<Referendum> referendums = new ArrayList<>();
        Serveur serveur = new Serveur(referendums);

        // Vérifier que la liste est vide au départ
        assertTrue(serveur.getReferendums().isEmpty(), "La liste des référendums doit être vide au départ");

        // Ajouter un référendum et tester la récupération
        serveur.addReferendum(r1);
        assertEquals(r1, serveur.getReferendum(r1.getId()), "Le référendum ajouté doit être récupéré");
    }


    @Test
    public void testToString() {
        // Création de quelques référendums
        Referendum r1 = new Referendum("Killian président ?", new Date(2025-1900, Calendar.JANUARY, 1, 0, 0));
        Referendum r2 = new Referendum("Vincent revienne a Montpellier ?", new Date(2028-1900, Calendar.FEBRUARY, 29, 20, 0));
        List<Referendum> referendums = new ArrayList<>();
        referendums.add(r1);
        referendums.add(r2);
        Serveur serveur = new Serveur(referendums);

        // Tester la méthode toString
        String result = serveur.toString();
        assertTrue(result.contains("Referendum ouvert") || result.contains("Referendum fermé"), "L'état du référendum (ouvert/fermé) doit être indiqué dans la sortie");
        assertTrue(result.contains("Killian président ?"), "Le nom du référendum doit apparaître dans la sortie");
    }

    @Test
    public void testRemoveReferendum() {
        // Création de référendums et serveur
        Referendum r1 = new Referendum("Killian président ?", new Date(2025-1900, Calendar.JANUARY, 1, 0, 0));
        Referendum r2 = new Referendum("Vincent revienne a Montpellier ?", new Date(2028-1900, Calendar.FEBRUARY, 29, 20, 0));
        List<Referendum> referendums = new ArrayList<>();
        Serveur serveur = new Serveur(referendums);

        // Ajouter les référendums au serveur
        serveur.addReferendum(r1);
        serveur.addReferendum(r2);

        // Vérifier que les référendums sont bien ajoutés
        assertEquals(2, serveur.getReferendums().size(), "La liste des référendums doit contenir 2 éléments");

        // Supprimer un référendum et tester
        serveur.removeReferendum(r1);
        assertEquals(1, serveur.getReferendums().size(), "La liste des référendums doit contenir 1 élément après suppression");
        assertNull(serveur.getReferendum(r1.getId()), "Le référendum supprimé ne doit plus être présent");
    }
}
