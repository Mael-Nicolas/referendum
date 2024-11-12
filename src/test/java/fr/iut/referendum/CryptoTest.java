package fr.iut.referendum;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.*;

public class CryptoTest {

    // Test de la génération des clés
    @Test
    public void testGenKey() {
        // Génération de la clé publique et privée
        BigInteger[] keys = Crypto.genkey();
        BigInteger p = keys[0];
        BigInteger g = keys[1];
        BigInteger h = keys[2];
        BigInteger sk = keys[3];

        // Vérification de la validité de la clé publique et privée
        assertNotNull(p, "La valeur de p ne doit pas être nulle");
        assertNotNull(g, "La valeur de g ne doit pas être nulle");
        assertNotNull(h, "La valeur de h ne doit pas être nulle");
        assertNotNull(sk, "La clé secrète ne doit pas être nulle");

        // Vérification que p et g satisfont les conditions de sécurité
        assertTrue(p.isProbablePrime(40), "p doit être un nombre premier");
        assertTrue(g.modPow(BigInteger.TWO, p).compareTo(BigInteger.ONE) != 0, "g^2 mod p ne doit pas être égal à 1");
        assertTrue(g.modPow(p.subtract(BigInteger.ONE), p).compareTo(BigInteger.ONE) == 0, "g^(p-1) mod p doit être égal à 1");
    }

    // Test de la méthode encrypt
    @Test
    public void testEncrypt() {
        // Génération d'une clé publique
        BigInteger[] keys = Crypto.genkey();
        BigInteger[] pk = {keys[0], keys[1], keys[2]}; // p, g, h

        // Message à crypter
        BigInteger message = BigInteger.valueOf(42);

        // Appel de la méthode encrypt
        BigInteger[] encrypted = Crypto.encrypt(message, pk);

        // Vérification que le message est correctement crypté
        assertNotNull(encrypted, "Le message crypté ne doit pas être nul");
        assertEquals(2, encrypted.length, "Le tableau de sortie doit contenir deux éléments");
        assertTrue(encrypted[0].compareTo(BigInteger.ZERO) > 0, "Le premier élément du cryptage doit être positif");
        assertTrue(encrypted[1].compareTo(BigInteger.ZERO) > 0, "Le deuxième élément du cryptage doit être positif");
    }

    // Test de l'agrégation des votes
    @Test
    public void testAgrege() {
        // Génération d'une clé publique
        BigInteger[] keys = Crypto.genkey();
        BigInteger[] pk = {keys[0], keys[1], keys[2]}; // p, g, h

        // Messages à agréger
        BigInteger[] encryptedMessage1 = Crypto.encrypt(BigInteger.valueOf(42), pk);
        BigInteger[] encryptedMessage2 = Crypto.encrypt(BigInteger.valueOf(43), pk);

        // Agrégation des messages
        BigInteger[] aggregated = Crypto.agrege(encryptedMessage1, encryptedMessage2, pk);

        // Vérification que l'agrégation donne bien un résultat
        assertNotNull(aggregated, "L'agrégation des messages ne doit pas être nulle");
        assertEquals(2, aggregated.length, "Le tableau de sortie de l'agrégation doit contenir deux éléments");
        assertTrue(aggregated[0].compareTo(BigInteger.ZERO) > 0, "Le premier élément de l'agrégation doit être positif");
        assertTrue(aggregated[1].compareTo(BigInteger.ZERO) > 0, "Le deuxième élément de l'agrégation doit être positif");
    }

    // Test du décryptage
    @Test
    public void testDecrypt() {
        // Génération d'une clé publique et d'une clé secrète
        BigInteger[] keys = Crypto.genkey();
        BigInteger[] pk = {keys[0], keys[1], keys[2]}; // p, g, h
        BigInteger sk = keys[3]; // clé secrète

        // Message à crypter
        BigInteger message = BigInteger.valueOf(8);

        // Cryptage du message
        BigInteger[] encrypted = Crypto.encrypt(message, pk);

        // Décryptage du message
        BigInteger decryptedMessage = Crypto.decrypt(encrypted, pk, sk, 10);

        // Vérification que le message décrypté est correct
        assertNotNull(decryptedMessage, "Le message décrypté ne doit pas être nul");
        assertEquals(message, decryptedMessage, "Le message décrypté doit être égal au message original");
    }

    @Test
    public void testAgregeDecrypt() {
        // Génération d'une clé publique et d'une clé secrète
        BigInteger[] keys = Crypto.genkey();
        BigInteger[] pk = {keys[0], keys[1], keys[2]}; // p, g, h
        BigInteger sk = keys[3]; // clé secrète

        // Message à crypter
        BigInteger message = BigInteger.valueOf(8);
        BigInteger message2 = BigInteger.valueOf(1);

        // Cryptage du message
        BigInteger[] encrypted = Crypto.encrypt(message, pk);
        BigInteger[] encrypted2 = Crypto.encrypt(message2, pk);

        BigInteger[] agrege = Crypto.agrege(encrypted, encrypted2, pk);

        // Décryptage du message
        BigInteger decryptedMessage = Crypto.decrypt(agrege, pk, sk, 9);

        // Vérification que le message décrypté est correct
        assertNotNull(decryptedMessage, "Le message décrypté ne doit pas être nul");
        assertEquals(9, decryptedMessage, "Le message décrypté doit être égal au message original");
    }

    // Test de décryptage avec un nombre incorrect de votants
    @Test
    public void testDecryptWithInvalidVoters() {
        // Génération d'une clé publique et d'une clé secrète
        BigInteger[] keys = Crypto.genkey();
        BigInteger[] pk = {keys[0], keys[1], keys[2]}; // p, g, h
        BigInteger sk = keys[3]; // clé secrète

        // Message à crypter
        BigInteger message = BigInteger.valueOf(42);

        // Cryptage du message
        BigInteger[] encrypted = Crypto.encrypt(message, pk);

        // Test du décryptage avec un nombre incorrect de votants
        BigInteger decryptedMessage = Crypto.decrypt(encrypted, pk, sk, 10); // Nombre incorrect de votants

        // Vérification que le message n'a pas pu être décrypté correctement
        assertNull(decryptedMessage, "Le décryptage ne doit pas réussir avec un nombre incorrect de votants");
    }

        @Test
        public void testDecrypt2() {
            // Génération des clés
            BigInteger[] keys = Crypto.genkey();
            BigInteger[] pk = {keys[0], keys[1], keys[2]}; // pk[0] -> p, pk[1] -> g, pk[2] -> h
            BigInteger sk = keys[3]; // sk -> clé privée

            // Message à chiffrer
            BigInteger m = BigInteger.valueOf(5);

            // Chiffrement
            BigInteger[] cipher = Crypto.encrypt(m, pk);

            // Déchiffrement
            BigInteger decrypted = Crypto.decrypt(cipher, pk, sk, 10);

            // Affichage et vérification du résultat
            System.out.println("Décrypté: " + decrypted);
            assertNotNull(decrypted, "Le message décrypté ne doit pas être nul");
            assertEquals(m, decrypted, "Le message décrypté ne correspond pas au message original");
        }

    @Test
    public void testDecrypt3() {
        // Génération des clés
        BigInteger[] keys = Crypto.genkey();
        BigInteger[] pk = { keys[0], keys[1], keys[2] }; // pk[0] -> p, pk[1] -> g, pk[2] -> h
        BigInteger sk = keys[3]; // sk -> clé privée

        // Message à chiffrer
        BigInteger m = BigInteger.valueOf(5);

        // Chiffrement
        BigInteger[] cipher = Crypto.encrypt(m, pk);

        // Affichage des valeurs chiffrées
        System.out.println("c1 (chiffré): " + cipher[0]);
        System.out.println("c2 (chiffré): " + cipher[1]);

        // Déchiffrement
        BigInteger decrypted = Crypto.decrypt(cipher, pk, sk, 10);

        // Log des résultats
        System.out.println("Décrypté: " + decrypted);

        // Assertions pour vérifier que le décryptage fonctionne
        assertNotNull(decrypted, "Le message décrypté ne doit pas être nul");
        assertEquals(m, decrypted, "Le message décrypté ne correspond pas au message original");
    }

    @Test
    public void testDecryptWithInvalidKey() {
        // Génération des clés
        BigInteger[] keys = Crypto.genkey();
        BigInteger[] pk = { keys[0], keys[1], keys[2] }; // pk[0] -> p, pk[1] -> g, pk[2] -> h
        BigInteger sk = keys[3]; // sk -> clé privée

        // Message à chiffrer
        BigInteger m = BigInteger.valueOf(5);

        // Chiffrement
        BigInteger[] cipher = Crypto.encrypt(m, pk);

        // Crée une clé privée invalide
        BigInteger invalidSk = new BigInteger(pk[0].subtract(BigInteger.ONE).bitLength(), new SecureRandom()).mod(pk[0].subtract(BigInteger.ONE));

        // Déchiffrement avec une clé privée incorrecte
        BigInteger decrypted = Crypto.decrypt(cipher, pk, invalidSk, 10);

        // Vérification que le décryptage échoue
        assertNull(decrypted, "Le message décrypté avec une clé invalide devrait être nul");
    }
}
