package fr.iut.referendum;

import java.math.BigInteger;
import java.security.SecureRandom;

public abstract class Crypto {
    private static final SecureRandom random = new SecureRandom();

    public static BigInteger[] encrypt(BigInteger m, BigInteger g, BigInteger p, BigInteger h) {
        BigInteger k = new BigInteger(p.subtract(BigInteger.ONE).bitLength(), random).mod(p.subtract(BigInteger.ONE)); // k < p-1
        BigInteger c1 = g.modPow(k, p); // c1 = g^k mod p
        BigInteger c2 = g.modPow(m, p).multiply(h.modPow(k, p)).mod(p); // c2 = g^m * publickey^k mod p
        return new BigInteger[]{c1, c2};
    }

    public static BigInteger[] genkey(int nbBits) {
        int tauxPremier = 40; // Taux de certitude de primalité
        BigInteger p;
        BigInteger q;
        do {
            q = new BigInteger(nbBits, tauxPremier, random); // q premier
            p = q.multiply(BigInteger.valueOf(2)).add(BigInteger.ONE); // p = 2q + 1
        } while (!p.isProbablePrime(tauxPremier)); // p premier
        BigInteger g;
        do {
            g = new BigInteger(p.subtract(BigInteger.ONE).bitLength(), random).mod(p.subtract(BigInteger.ONE)); // g < p-1
        } while (g.modPow(BigInteger.TWO, p).compareTo(BigInteger.ONE) == 0); // g^2 mod p == 0
        if (g.modPow(q, p).compareTo(BigInteger.ONE) != 0) { // g^q mod p != 0
            g = g.modPow(BigInteger.TWO, p); // g = g^2 mod p
        }
        if (nbBits < 512) {
            throw new IllegalArgumentException("La taille de la clé doit être supérieure à 512 bits");
        }
        BigInteger x = new BigInteger(p.subtract(BigInteger.ONE).bitLength(), random).mod(p.subtract(BigInteger.ONE));
        BigInteger h = g.modPow(x, p);
        return new BigInteger[]{p, q, g, h, x};
    }
}