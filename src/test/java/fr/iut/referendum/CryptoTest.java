package fr.iut.referendum;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigInteger;
import java.time.Duration;

public class CryptoTest {

    @Test
    public void testKeyGeneration() {
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
    }

//    @Test
//    public void testKeyGenerationTimeFor3072Bits() {
//        // Mesure du temps de génération des clés avec 3072 bits
//        assertTimeoutPreemptively(Duration.ofSeconds(900), () -> {
//            Crypto.genkey(3072);
//        }, "La génération de clé avec 3072 bits ne doit pas prendre plus de 15 minutes.");
//    }

    @Test
    public void testEncrypt() {
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
    public void testEncryptConsistency() {
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
}
