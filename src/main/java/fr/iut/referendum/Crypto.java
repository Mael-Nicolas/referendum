package fr.iut.referendum;

import java.math.BigInteger;
import java.security.SecureRandom;

public abstract class Crypto {
    private static final SecureRandom random = new SecureRandom();

    public static BigInteger[] encrypt(BigInteger m, BigInteger g, BigInteger p, BigInteger pk) {
        BigInteger k = new BigInteger(p.subtract(BigInteger.ONE).bitLength(), random).mod(p.subtract(BigInteger.ONE)); // k < p-1
        BigInteger c1 = g.modPow(k, p); // c1 = g^k mod p
        BigInteger c2 = g.modPow(m, p).multiply(pk.modPow(k, p)).mod(p); // c2 = g^m * publickey^k mod p
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
        BigInteger sk = new BigInteger(p.subtract(BigInteger.ONE).bitLength(), random).mod(p.subtract(BigInteger.ONE));
        BigInteger pk = g.modPow(sk, p);
        return new BigInteger[]{p, q, g, pk, sk};
    }

    public static BigInteger[] agrege(BigInteger[] c1, BigInteger[] c2, BigInteger pk) {
        BigInteger u = c1[0].multiply(c2[0]).mod(pk);
        BigInteger v = c1[1].multiply(c2[1]).mod(pk);
        return new BigInteger[]{u, v};
    }

    public static BigInteger decrypt(BigInteger[] c, BigInteger g, BigInteger p, BigInteger pk) {
        BigInteger c1 = c[0];
        BigInteger c2 = c[1];

        BigInteger M = c2.multiply(c1.modPow(pk, p).modInverse(p)).mod(p);   // M = v × (u^x)^−1 mod p
        BigInteger B = BigInteger.TWO.pow(3000); // si le message peut être codé sur 3000 bits
        for (BigInteger m = BigInteger.ZERO; m.compareTo(B) < 0; m = m.add(BigInteger.ONE)) {
            if ((g.modPow(m, p)).equals(M)) {
                return m;
            }
        }

        return null;
    }

}