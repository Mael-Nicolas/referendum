package fr.iut.referendum;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

public class CryptoTest {

    @Test
    public void testKeyGeneration() throws ExecutionException, InterruptedException {
        // Génération de clé avec 1024 bits pour une exécution plus rapide
        BigInteger[] key = Crypto.genkey(1024);

        BigInteger p = key[0];
        BigInteger q = key[1];
        BigInteger g = key[2];
        BigInteger h = key[3];
        BigInteger x = key[4];

        // Vérification que p est un nombre premier probable avec une grande certitude
        assertTrue(p.isProbablePrime(40), "p devrait être un nombre premier");

        // Vérification que q est aussi un nombre premier
        assertTrue(q.isProbablePrime(40), "q devrait être un nombre premier");

        // Vérification que g^q mod p = 1 (propriété de générateur g dans le groupe Zp)
        assertEquals(BigInteger.ONE, g.modPow(q, p), "g^q mod p devrait être égal à 1");

        // Vérification que x est bien dans l'intervalle [1, p-1]
        assertTrue(x.compareTo(BigInteger.ONE) > 0 && x.compareTo(p.subtract(BigInteger.ONE)) < 0, "x doit être dans l'intervalle [1, p-1]");
    }

    @Test
    public void testEncrypt() throws ExecutionException, InterruptedException {
        // Génération de clés
        BigInteger[] key = Crypto.genkey(1024);
        BigInteger p = key[0];
        BigInteger g = key[2];
        BigInteger h = key[3];

        // Message à encrypter (par exemple un entier m)
        BigInteger m = BigInteger.valueOf(12345);

        // Encryption du message
        BigInteger[] encrypted = Crypto.encrypt(m, g, p, h);

        BigInteger c1 = encrypted[0];
        BigInteger c2 = encrypted[1];

        // Vérification que c1 et c2 sont bien dans le groupe Zp
        assertTrue(c1.compareTo(BigInteger.ZERO) > 0 && c1.compareTo(p) < 0, "c1 doit être dans Zp");
        assertTrue(c2.compareTo(BigInteger.ZERO) > 0 && c2.compareTo(p) < 0, "c2 doit être dans Zp");
    }

    @Test
    public void testEncryptConsistency() throws ExecutionException, InterruptedException {
        // Génération de clés
        BigInteger[] key = Crypto.genkey(1024);
        BigInteger p = key[0];
        BigInteger g = key[2];
        BigInteger h = key[3];

        // Message à encrypter
        BigInteger m = BigInteger.valueOf(12345);

        // Encryptage du message
        BigInteger[] encrypted1 = Crypto.encrypt(m, g, p, h);
        BigInteger[] encrypted2 = Crypto.encrypt(m, g, p, h);

        // Vérification que deux encryptions du même message produisent des résultats différents (propriété des schémas probabilistes)
        assertNotEquals(encrypted1[0], encrypted2[0], "c1 devrait être différent pour chaque encryption");
        assertNotEquals(encrypted1[1], encrypted2[1], "c2 devrait être différent pour chaque encryption");
    }

    @Disabled
    @Test
    public void testKeyGenerationTime() throws ExecutionException, InterruptedException {
        // Mesurer le temps de génération pour différentes tailles de clés
        measureKeyGenTime(1024);
        measureKeyGenTime(2048);
        measureKeyGenTime(3072);
    }

    @Test
    public void testInvalidKeySize() {
        // Essai de génération de clé avec une taille de clé trop petite (par exemple 256 bits)
        assertThrows(IllegalArgumentException.class, () -> {
            Crypto.genkey(256);
        }, "Une taille de clé trop petite devrait lancer une exception");
    }

    private void measureKeyGenTime(int nbBits) throws ExecutionException, InterruptedException {
        // Démarrer le chronomètre
        long startTime = System.nanoTime();

        // Générer la clé
        BigInteger[] key = Crypto.genkey(nbBits);

        // Arrêter le chronomètre
        long endTime = System.nanoTime();

        // Calculer la durée en millisecondes
        long durationInMillis = (endTime - startTime) / 1_000_000;

        // Afficher la durée
        System.out.println("Génération de clé pour " + nbBits + " bits a pris " + durationInMillis + " ms");

        // Vérification basique que la clé est bien générée
        assertNotNull(key, "La clé ne doit pas être nulle");
        assertEquals(5, key.length, "Le tableau de clés doit avoir une longueur de 5");
    }

    @Test
    public void testKeyGenerationValidity() {
        // Test standard pour générer une clé de 1024 bits
        BigInteger[] key = Crypto.genkey(1024);

        BigInteger p = key[0];
        BigInteger q = key[1];
        BigInteger g = key[2];
        BigInteger h = key[3];
        BigInteger x = key[4];

        // Vérification que p est un nombre premier
        assertTrue(p.isProbablePrime(40), "p devrait être un nombre premier");

        // Vérification que q est aussi un nombre premier
        assertTrue(q.isProbablePrime(40), "q devrait être un nombre premier");

        // Vérification que g^q mod p = 1
        assertEquals(BigInteger.ONE, g.modPow(q, p), "g^q mod p devrait être égal à 1");

        // Vérification que la clé privée x est dans [1, p-1]
        assertTrue(x.compareTo(BigInteger.ONE) > 0 && x.compareTo(p.subtract(BigInteger.ONE)) < 0, "x doit être dans l'intervalle [1, p-1]");

        // Vérification que la clé publique h est bien calculée
        assertEquals(h, g.modPow(x, p), "h doit être égal à g^x mod p");
    }

    @Test
    public void testKeyGenerationWithSmallBits() {
        // Test pour une taille de clé inférieure à 512 bits, cela doit lever une exception
        assertThrows(IllegalArgumentException.class, () -> Crypto.genkey(256), "La génération de clé avec moins de 512 bits doit lancer une exception");
    }

    @Test
    public void testKeyGenerationWith512Bits() {
        // Test pour la taille minimale acceptable (512 bits)
        BigInteger[] key = Crypto.genkey(512);

        BigInteger p = key[0];
        BigInteger q = key[1];

        // Vérification que p et q sont des nombres premiers
        assertTrue(p.isProbablePrime(40), "p devrait être un nombre premier pour 512 bits");
        assertTrue(q.isProbablePrime(40), "q devrait être un nombre premier pour 512 bits");
    }

    @Disabled
    @Test
    public void testKeyGenerationMaxBits() {
        BigInteger[] key = Crypto.genkey(3072);
        assertNotNull(key, "La génération de clé pour 3072 bits doit fonctionner");
    }

    @Test
    public void testKeyGenerationConsistency() {
        // Vérifie que deux générations de clés avec les mêmes paramètres ne produisent pas les mêmes clés
        BigInteger[] key1 = Crypto.genkey(1024);
        BigInteger[] key2 = Crypto.genkey(1024);

        assertNotEquals(key1[0], key2[0], "Les clés générées (p) ne devraient pas être identiques");
        assertNotEquals(key1[4], key2[4], "Les clés privées générées (x) ne devraient pas être identiques");
    }

    @Test
    public void testKeyElementsRange() {
        // Teste que les éléments de la clé générée sont dans les plages attendues
        BigInteger[] key = Crypto.genkey(1024);

        BigInteger p = key[0];
        BigInteger q = key[1];
        BigInteger g = key[2];
        BigInteger h = key[3];
        BigInteger x = key[4];

        // Vérification que q < p
        assertTrue(q.compareTo(p) < 0, "q doit être inférieur à p");

        // Vérification que g, h et x sont bien dans [1, p-1]
        assertTrue(g.compareTo(BigInteger.ONE) > 0 && g.compareTo(p.subtract(BigInteger.ONE)) < 0, "g doit être dans l'intervalle [1, p-1]");
        assertTrue(h.compareTo(BigInteger.ONE) > 0 && h.compareTo(p.subtract(BigInteger.ONE)) < 0, "h doit être dans l'intervalle [1, p-1]");
        assertTrue(x.compareTo(BigInteger.ONE) > 0 && x.compareTo(p.subtract(BigInteger.ONE)) < 0, "x doit être dans l'intervalle [1, p-1]");
    }

    // Tests pour la fonction encrypt
    @Test
    public void testEncryptBasic() {
        // Génération de clés
        BigInteger[] key = Crypto.genkey(1024);
        BigInteger p = key[0];
        BigInteger g = key[2];
        BigInteger h = key[3];

        // Message à encrypter
        BigInteger m = BigInteger.valueOf(42);

        // Chiffrement
        BigInteger[] encrypted = Crypto.encrypt(m, g, p, h);

        BigInteger c1 = encrypted[0];
        BigInteger c2 = encrypted[1];

        // Vérification que c1 et c2 sont dans le groupe Zp
        assertTrue(c1.compareTo(BigInteger.ZERO) > 0 && c1.compareTo(p) < 0, "c1 doit être dans Zp");
        assertTrue(c2.compareTo(BigInteger.ZERO) > 0 && c2.compareTo(p) < 0, "c2 doit être dans Zp");
    }

    @Test
    public void testEncryptWithLargeMessage() {
        // Génération de clés
        BigInteger[] key = Crypto.genkey(1024);
        BigInteger p = key[0];
        BigInteger g = key[2];
        BigInteger h = key[3];

        // Message à encrypter plus grand (mais toujours dans Zp)
        BigInteger m = BigInteger.valueOf(Long.MAX_VALUE);

        // Chiffrement
        BigInteger[] encrypted = Crypto.encrypt(m, g, p, h);

        BigInteger c1 = encrypted[0];
        BigInteger c2 = encrypted[1];

        // Vérification que c1 et c2 sont dans le groupe Zp
        assertTrue(c1.compareTo(BigInteger.ZERO) > 0 && c1.compareTo(p) < 0, "c1 doit être dans Zp");
        assertTrue(c2.compareTo(BigInteger.ZERO) > 0 && c2.compareTo(p) < 0, "c2 doit être dans Zp");
    }

    @Test
    public void testEncryptWithZeroMessage() {
        // Génération de clés
        BigInteger[] key = Crypto.genkey(1024);
        BigInteger p = key[0];
        BigInteger g = key[2];
        BigInteger h = key[3];

        // Message à encrypter (m = 0)
        BigInteger m = BigInteger.ZERO;

        // Chiffrement
        BigInteger[] encrypted = Crypto.encrypt(m, g, p, h);

        BigInteger c1 = encrypted[0];
        BigInteger c2 = encrypted[1];

        // Vérification que c1 et c2 sont dans Zp
        assertTrue(c1.compareTo(BigInteger.ZERO) > 0 && c1.compareTo(p) < 0, "c1 doit être dans Zp");
        assertTrue(c2.compareTo(BigInteger.ZERO) > 0 && c2.compareTo(p) < 0, "c2 doit être dans Zp");
    }
}
