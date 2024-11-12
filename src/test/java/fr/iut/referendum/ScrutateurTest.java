package fr.iut.referendum;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.math.BigInteger;

public class ScrutateurTest {

    @Test
    public void testDechiffrer() {
        // Créer un scrutateur
        Scrutateur scrutateur = new Scrutateur();

        // Exemple de données pour les résultats agrégés
        BigInteger[] agr = new BigInteger[2];
        agr[0] = new BigInteger("10"); // Exemple de premier agrégé (c1)
        agr[1] = new BigInteger("15"); // Exemple de deuxième agrégé (c2)
        int nbVotants = 20; // Exemple de nombre de votants

        // Appeler la méthode de déchiffrement
        String resultat = scrutateur.dechiffrer(agr, nbVotants);

        // Afficher et vérifier le résultat
        System.out.println("Résultat déchiffré : " + resultat);
        assertEquals("Oui", resultat); // Résultat attendu : Oui (25 > 10)
    }

    @Test
    public void testDechiffrerNon() {
        // Créer un scrutateur
        Scrutateur scrutateur = new Scrutateur();

        // Exemple de données pour les résultats agrégés
        BigInteger[] agr = new BigInteger[2];
        agr[0] = new BigInteger("5"); // Exemple de premier agrégé (c1)
        agr[1] = new BigInteger("4"); // Exemple de deuxième agrégé (c2)
        int nbVotants = 20; // Exemple de nombre de votants

        // Appeler la méthode de déchiffrement
        String resultat = scrutateur.dechiffrer(agr, nbVotants);

        // Afficher et vérifier le résultat
        System.out.println("Résultat déchiffré : " + resultat);
        assertEquals("Non", resultat); // Résultat attendu : Non (9 < 10)
    }

    @Test
    public void testEnvoyeClePubliqueReferendum() {
        // Créer un scrutateur
        Scrutateur scrutateur = new Scrutateur();

        // Vérifier que la clé publique a été générée correctement
        assertNotNull(scrutateur.pk[0]); // p
        assertNotNull(scrutateur.pk[1]); // g
        assertNotNull(scrutateur.pk[2]); // h

        System.out.println("Clé publique (p, g, h) générée.");
        System.out.println("p: " + scrutateur.pk[0]);
        System.out.println("g: " + scrutateur.pk[1]);
        System.out.println("h: " + scrutateur.pk[2]);

        // Les tests s'assurent ici que les clés existent et sont générées.
        assertTrue(scrutateur.pk[0].bitLength() > 0);
        assertTrue(scrutateur.pk[1].bitLength() > 0);
        assertTrue(scrutateur.pk[2].bitLength() > 0);
    }

    @Test
    public void testResultatReferendum() {
        // Créer un scrutateur
        Scrutateur scrutateur = new Scrutateur();

        // Simuler des valeurs pour le résultat
        BigInteger c1 = new BigInteger("10");
        BigInteger c2 = new BigInteger("15");
        BigInteger[] resultatAgrege = {c1, c2};
        int nbVotants = 20;

        // Déchiffrer le résultat avec la méthode de scrutateur
        String resultat = scrutateur.dechiffrer(resultatAgrege, nbVotants);

        // Vérifier que le résultat est correct
        assertEquals("Oui", resultat);
    }
}
