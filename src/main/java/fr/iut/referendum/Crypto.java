package fr.iut.referendum;

import java.math.BigInteger;
import java.security.SecureRandom;

public abstract class Crypto {
    private static final SecureRandom random = new SecureRandom();

    public static BigInteger[] encrypt(BigInteger m, BigInteger[] pk) {
        BigInteger p = pk[0];
        BigInteger g = pk[1];
        BigInteger h = pk[2];
        BigInteger k = new BigInteger(p.subtract(BigInteger.ONE).bitLength(), random).mod(p.subtract(BigInteger.ONE)); // k < p-1
        BigInteger c1 = g.modPow(k, p); // c1 = g^k mod p
        BigInteger c2 = g.modPow(m, p).multiply(h.modPow(k, p)).mod(p); // c2 = g^m * publickey^k mod p
        return new BigInteger[]{c1, c2};
    }

    public static BigInteger[] genkey() {
        int tauxPremier = 40; // Taux de certitude de primalité
        BigInteger p;
        BigInteger q;
        do {
            q = new BigInteger(512, tauxPremier, random); // q premier
            p = q.multiply(BigInteger.valueOf(2)).add(BigInteger.ONE); // p = 2q + 1
        } while (!p.isProbablePrime(tauxPremier)); // p premier
        BigInteger g;
        do {
            g = new BigInteger(p.subtract(BigInteger.ONE).bitLength(), random).mod(p.subtract(BigInteger.ONE)); // g < p-1
        } while (g.modPow(BigInteger.TWO, p).compareTo(BigInteger.ONE) == 0); // g^2 mod p == 0
        if (g.modPow(q, p).compareTo(BigInteger.ONE) != 0) { // g^q mod p != 0
            g = g.modPow(BigInteger.TWO, p); // g = g^2 mod p
        }
        BigInteger sk = new BigInteger(p.subtract(BigInteger.ONE).bitLength(), random).mod(p.subtract(BigInteger.ONE));
        BigInteger h = g.modPow(sk, p);
        return new BigInteger[]{p, g, h, sk};
    }

    public static BigInteger[] agrege(BigInteger[] c1, BigInteger[] c2, BigInteger[] pk) {
        BigInteger p = pk[0];
        BigInteger u = c1[0].multiply(c2[0]).mod(p);
        BigInteger v = c1[1].multiply(c2[1]).mod(p);
        return new BigInteger[]{u, v};
    }

    public static BigInteger decrypt(BigInteger[] c, BigInteger[] pk, BigInteger sk, int nbVotants) {
        BigInteger c1 = c[0];
        BigInteger c2 = c[1];
        BigInteger p = pk[0];
        BigInteger g = pk[1];

        // Ajouter un log pour vérifier les entrées
        System.out.println("c1: " + c1);
        System.out.println("c2: " + c2);
        System.out.println("sk: " + sk);
        System.out.println("p: " + p);
        System.out.println("g: " + g);

        BigInteger M = c2.multiply(c1.modPow(sk, p).modInverse(p)).mod(p);   // M = v × (u^x)^−1 mod p

        // Ajouter un log pour M avant la boucle
        System.out.println("M: " + M);

        BigInteger B = BigInteger.valueOf(nbVotants);
        for (BigInteger m = BigInteger.ZERO; m.compareTo(B) <= 0; m = m.add(BigInteger.ONE)) {
            if ((g.modPow(m, p)).equals(M)) {
                System.out.println("Message décrypté: " + m);
                return m;
            }
        }
        System.out.println("Déchiffrement échoué");
        return null;
    }

}