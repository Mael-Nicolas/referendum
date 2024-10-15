package fr.iut.referendum;

import com.sun.source.tree.BreakTree;

import java.math.BigInteger;
import java.security.SecureRandom;

public abstract class Crypto {
    private static final SecureRandom random = new SecureRandom();

    public static BigInteger[] encrypt(BigInteger m, BigInteger g, BigInteger p, BigInteger publicKey) {
        BigInteger k = new BigInteger(p.subtract(BigInteger.ONE).bitLength(), random).mod(p.subtract(BigInteger.ONE)); // k < p-1
        BigInteger c1 = g.modPow(k, p); // c1 = g^k mod p
        BigInteger c2 = g.modPow(m, p).multiply(publicKey.modPow(k, p)).mod(p); // c2 = g^m * publickey^k mod p
        return new BigInteger[]{c1, c2};
    }

    public static BigInteger[] genkey(int nbBits) {
        int tauxPremier = 40;
        BigInteger p;
        BigInteger q;
        do {
            q = new BigInteger(nbBits, tauxPremier, random);
            p = q.multiply(BigInteger.valueOf(2)).add(BigInteger.ONE);
        } while (!p.isProbablePrime(tauxPremier));
        BigInteger g;
        do {
            g = new BigInteger(p.subtract(BigInteger.ONE).bitLength(), random).mod(p.subtract(BigInteger.ONE));
        } while (g.modPow(BigInteger.TWO, p).compareTo(BigInteger.ONE) == 0);
        if (g.modPow(q, p).compareTo(BigInteger.ONE) != 0) {
            g = g.modPow(BigInteger.TWO, p);
        }
        BigInteger x = new BigInteger(p.subtract(BigInteger.ONE).bitLength(), random).mod(p.subtract(BigInteger.ONE));
        BigInteger h = g.modPow(x, p);
        return new BigInteger[]{p, q, g, h, x};
    }
}