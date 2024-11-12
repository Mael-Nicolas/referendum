package fr.iut.referendum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;

class CryptoTest {
    private BigInteger[] publicKey;
    private BigInteger privateKey;
    private final int nbVotants = 2; // Limite fixée pour le déchiffrement entre 0 et nbVotants-1

    @BeforeEach
    void setup() {
        BigInteger[] keys = Crypto.genkey();
        publicKey = new BigInteger[]{keys[0], keys[1], keys[2]};
        privateKey = keys[3];
    }

    @Test
    void testKeyGeneration() {
        // Test de base pour vérifier la conformité des clés générées
        assertNotNull(publicKey, "La clé publique ne doit pas être nulle");
        assertNotNull(privateKey, "La clé privée ne doit pas être nulle");
        assertEquals(3, publicKey.length, "La clé publique doit contenir trois éléments (p, g, h)");

        // Vérifie que p est un nombre premier
        assertTrue(publicKey[0].isProbablePrime(40), "p doit être premier");

        // Vérifie que g est un générateur du groupe de p
        assertTrue(publicKey[1].compareTo(BigInteger.ZERO) > 0 && publicKey[1].compareTo(publicKey[0]) < 0, "g doit être dans le groupe modulaire défini par p");

        // Vérifie que la clé privée est inférieure à p-1
        assertTrue(privateKey.compareTo(publicKey[0].subtract(BigInteger.ONE)) < 0, "La clé privée doit être inférieure à p-1");
    }

    @Test
    void testEncryptDecrypt() {
        // Chiffrement et déchiffrement des messages 0 et 1
        BigInteger message0 = BigInteger.ZERO;
        BigInteger message1 = BigInteger.ONE;

        // Chiffrement et déchiffrement du message 0
        BigInteger[] encryptedMessage0 = Crypto.encrypt(message0, publicKey);
        BigInteger decryptedMessage0 = Crypto.decrypt(encryptedMessage0, publicKey, privateKey, nbVotants);
        assertEquals(message0, decryptedMessage0, "Le déchiffrement du message 0 doit donner le message d'origine");

        // Chiffrement et déchiffrement du message 1
        BigInteger[] encryptedMessage1 = Crypto.encrypt(message1, publicKey);
        BigInteger decryptedMessage1 = Crypto.decrypt(encryptedMessage1, publicKey, privateKey, nbVotants);
        assertEquals(message1, decryptedMessage1, "Le déchiffrement du message 1 doit donner le message d'origine");

        // Test de déchiffrement incorrect pour une valeur au-delà de la limite nbVotants
        BigInteger invalidMessage = BigInteger.valueOf(3);
        BigInteger[] encryptedInvalidMessage = Crypto.encrypt(invalidMessage, publicKey);
        BigInteger decryptedInvalidMessage = Crypto.decrypt(encryptedInvalidMessage, publicKey, privateKey, nbVotants);
        assertNull(decryptedInvalidMessage, "Le déchiffrement d'un message au-delà de la limite nbVotants doit retourner null");
    }

    @Test
    void testEncryptProducesDifferentCiphertexts() {
        // Test pour vérifier que le chiffrement produit des résultats différents pour le même message
        BigInteger message = BigInteger.ONE;

        BigInteger[] ciphertext1 = Crypto.encrypt(message, publicKey);
        BigInteger[] ciphertext2 = Crypto.encrypt(message, publicKey);

        assertNotEquals(ciphertext1[0], ciphertext2[0], "c1 des deux chiffrés ne doit pas être identique");
        assertNotEquals(ciphertext1[1], ciphertext2[1], "c2 des deux chiffrés ne doit pas être identique");

        // Vérifie que les chiffrés sont bien de longueur 2
        assertEquals(2, ciphertext1.length, "Le chiffré doit contenir exactement deux éléments (c1, c2)");
    }

    @Test
    void testAggregation() {
        // Test de base pour l'agrégation de deux messages chiffrés
        BigInteger message1 = BigInteger.ONE;
        BigInteger message0 = BigInteger.ZERO;

        BigInteger[] encryptedMessage1 = Crypto.encrypt(message1, publicKey);
        BigInteger[] encryptedMessage0 = Crypto.encrypt(message0, publicKey);

        // Agrège les deux chiffrés
        BigInteger[] aggregatedCipher = Crypto.agrege(encryptedMessage1, encryptedMessage0, publicKey);

        // Déchiffre le chiffré agrégé
        BigInteger decryptedAggregatedMessage = Crypto.decrypt(aggregatedCipher, publicKey, privateKey, nbVotants);

        // Vérifie que l'agrégation donne le bon résultat
        assertEquals(message1.add(message0), decryptedAggregatedMessage, "Le résultat de l'agrégation doit être égal à la somme des messages");

        // Agrégation de deux messages contenant chacun 1 (devrait retourner null si on dépasse la limite)
        BigInteger[] encryptedMessage1Again = Crypto.encrypt(message1, publicKey);
        BigInteger[] aggregatedCipherOverflow = Crypto.agrege(encryptedMessage1, encryptedMessage1Again, publicKey);

        BigInteger decryptedAggregatedOverflow = Crypto.decrypt(aggregatedCipherOverflow, publicKey, privateKey, nbVotants);
        assertNull(decryptedAggregatedOverflow, "Le déchiffrement d'une agrégation dépassant la limite nbVotants doit retourner null");

        // Agrégation avec un chiffré invalide pour tester le comportement
        BigInteger[] invalidCipher = {BigInteger.ZERO, BigInteger.ZERO};
        BigInteger[] aggregatedWithInvalid = Crypto.agrege(encryptedMessage1, invalidCipher, publicKey);
        BigInteger decryptedInvalidAggregation = Crypto.decrypt(aggregatedWithInvalid, publicKey, privateKey, nbVotants);
        assertNull(decryptedInvalidAggregation, "L'agrégation avec un message invalide doit échouer et retourner null");
    }

    @Test
    void testAggregationWithoutModularInverseFailure() {
        // Génération de la clé publique et privée
        BigInteger[] key = Crypto.genkey();
        BigInteger p = key[0];
        BigInteger sk = key[3];
        BigInteger[] pk = new BigInteger[]{key[0], key[1], key[2]}; // p, g, h (sans sk)

        // Message simple à chiffrer
        BigInteger m = BigInteger.ONE;

        // Chiffrement du message
        BigInteger[] encryptedMessage1 = Crypto.encrypt(m, pk);

        // Agrégation du message avec lui-même
        BigInteger[] aggregatedCipher = Crypto.agrege(encryptedMessage1, encryptedMessage1, pk);

        // Déchiffrement de l'agrégation
        BigInteger decryptedMessage = Crypto.decrypt(aggregatedCipher, pk, sk, 2); // Nombre de votants = 2

        // Vérification que le déchiffrement est correct (m + m = 2)
        assertNotNull(decryptedMessage, "Le message déchiffré ne doit pas être nul.");
        assertEquals(BigInteger.TWO, decryptedMessage, "Le message déchiffré doit être égal à 2.");
    }

    @Test
    void testSimpleEncryptionDecryption() {
        // Génération des clés
        BigInteger[] key = Crypto.genkey();
        BigInteger p = key[0];
        BigInteger sk = key[3];
        BigInteger[] pk = new BigInteger[]{key[0], key[1], key[2]}; // p, g, h (sans sk)

        // Message simple à chiffrer
        BigInteger m = BigInteger.ONE;

        // Chiffrement
        BigInteger[] encryptedMessage = Crypto.encrypt(m, pk);

        // Déchiffrement
        BigInteger decryptedMessage = Crypto.decrypt(encryptedMessage, pk, sk, 1); // Nombre de votants = 1

        // Vérification que le message déchiffré est correct
        assertNotNull(decryptedMessage, "Le message déchiffré ne doit pas être nul.");
        assertEquals(m, decryptedMessage, "Le message déchiffré doit être égal au message original.");
    }
}
