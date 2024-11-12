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
        BigInteger[] key = Crypto.genkey();

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
        BigInteger[] key = Crypto.genkey();
        BigInteger p = key[0];
        BigInteger g = key[2];
        BigInteger h = key[3];

        // Message à encrypter (par exemple un entier m)
        BigInteger m = BigInteger.valueOf(12345);

        // Encryption du message
        BigInteger[] encrypted = Crypto.encrypt(m, key);

        BigInteger c1 = encrypted[0];
        BigInteger c2 = encrypted[1];

        // Vérification que c1 et c2 sont bien dans le groupe Zp
        assertTrue(c1.compareTo(BigInteger.ZERO) > 0 && c1.compareTo(p) < 0, "c1 doit être dans Zp");
        assertTrue(c2.compareTo(BigInteger.ZERO) > 0 && c2.compareTo(p) < 0, "c2 doit être dans Zp");
    }

    @Test
    public void testEncryptConsistency() throws ExecutionException, InterruptedException {
        // Génération de clés
        BigInteger[] key = Crypto.genkey();
        BigInteger p = key[0];
        BigInteger g = key[2];
        BigInteger h = key[3];

        // Message à encrypter
        BigInteger m = BigInteger.valueOf(12345);

        // Encryptage du message
        BigInteger[] encrypted1 = Crypto.encrypt(m, key);
        BigInteger[] encrypted2 = Crypto.encrypt(m, key);

        // Vérification que deux encryptions du même message produisent des résultats différents (propriété des schémas probabilistes)
        assertNotEquals(encrypted1[0], encrypted2[0], "c1 devrait être différent pour chaque encryption");
        assertNotEquals(encrypted1[1], encrypted2[1], "c2 devrait être différent pour chaque encryption");
    }

    @Test
    public void testKeyGenerationValidity() {
        // Test standard pour générer une clé de 1024 bits
        BigInteger[] key = Crypto.genkey();

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
    public void testKeyGenerationMaxBits() {
        BigInteger[] key = Crypto.genkey();
        assertNotNull(key, "La génération de clé pour 3072 bits doit fonctionner");
    }

    @Test
    public void testKeyGenerationConsistency() {
        // Vérifie que deux générations de clés avec les mêmes paramètres ne produisent pas les mêmes clés
        BigInteger[] key1 = Crypto.genkey();
        BigInteger[] key2 = Crypto.genkey();

        assertNotEquals(key1[0], key2[0], "Les clés générées (p) ne devraient pas être identiques");
        assertNotEquals(key1[4], key2[4], "Les clés privées générées (x) ne devraient pas être identiques");
    }

    @Test
    public void testKeyElementsRange() {
        // Teste que les éléments de la clé générée sont dans les plages attendues
        BigInteger[] key = Crypto.genkey();

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
        BigInteger[] key = Crypto.genkey();
        BigInteger p = key[0];
        BigInteger g = key[2];
        BigInteger h = key[3];

        // Message à encrypter
        BigInteger m = BigInteger.valueOf(42);

        // Chiffrement
        BigInteger[] encrypted = Crypto.encrypt(m, key);

        BigInteger c1 = encrypted[0];
        BigInteger c2 = encrypted[1];

        // Vérification que c1 et c2 sont dans le groupe Zp
        assertTrue(c1.compareTo(BigInteger.ZERO) > 0 && c1.compareTo(p) < 0, "c1 doit être dans Zp");
        assertTrue(c2.compareTo(BigInteger.ZERO) > 0 && c2.compareTo(p) < 0, "c2 doit être dans Zp");
    }

    @Test
    public void testEncryptWithLargeMessage() {
        // Génération de clés
        BigInteger[] key = Crypto.genkey();
        BigInteger p = key[0];
        BigInteger g = key[2];
        BigInteger h = key[3];

        // Message à encrypter plus grand (mais toujours dans Zp)
        BigInteger m = BigInteger.valueOf(Long.MAX_VALUE);

        // Chiffrement
        BigInteger[] encrypted = Crypto.encrypt(m, key);

        BigInteger c1 = encrypted[0];
        BigInteger c2 = encrypted[1];

        // Vérification que c1 et c2 sont dans le groupe Zp
        assertTrue(c1.compareTo(BigInteger.ZERO) > 0 && c1.compareTo(p) < 0, "c1 doit être dans Zp");
        assertTrue(c2.compareTo(BigInteger.ZERO) > 0 && c2.compareTo(p) < 0, "c2 doit être dans Zp");
    }

    @Test
    public void testEncryptWithZeroMessage() {
        // Génération de clés
        BigInteger[] key = Crypto.genkey();
        BigInteger p = key[0];
        BigInteger g = key[2];
        BigInteger h = key[3];

        // Message à encrypter (m = 0)
        BigInteger m = BigInteger.ZERO;

        // Chiffrement
        BigInteger[] encrypted = Crypto.encrypt(m, key);

        BigInteger c1 = encrypted[0];
        BigInteger c2 = encrypted[1];

        // Vérification que c1 et c2 sont dans Zp
        assertTrue(c1.compareTo(BigInteger.ZERO) > 0 && c1.compareTo(p) < 0, "c1 doit être dans Zp");
        assertTrue(c2.compareTo(BigInteger.ZERO) > 0 && c2.compareTo(p) < 0, "c2 doit être dans Zp");
    }

    @Test
    void encrypt() {
    }

    @Test
    void genkey() {
    }

    @Test
    void agrege() {
    }

    @Test
    void decrypt() {
    }
}
