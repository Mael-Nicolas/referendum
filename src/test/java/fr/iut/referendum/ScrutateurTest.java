package fr.iut.referendum;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.math.BigInteger;

public class ScrutateurTest {

    @Test
    public void testEnvoyeClePubliqueReferendum() {
        // Créer un scrutateur
        Scrutateur scrutateur = new Scrutateur();

        // Vérifier que la clé publique a été générée correctement
        assertNotNull(scrutateur.getPk()[0]); // p
        assertNotNull(scrutateur.getPk()[1]); // g
        assertNotNull(scrutateur.getPk()[2]); // h

        System.out.println("Clé publique (p, g, h) générée.");
        System.out.println("p: " + scrutateur.getPk()[0]);
        System.out.println("g: " + scrutateur.getPk()[1]);
        System.out.println("h: " + scrutateur.getPk()[2]);

        // Les tests s'assurent ici que les clés existent et sont générées.
        assertTrue(scrutateur.getPk()[0].bitLength() > 0);
        assertTrue(scrutateur.getPk()[1].bitLength() > 0);
        assertTrue(scrutateur.getPk()[2].bitLength() > 0);
    }

}
