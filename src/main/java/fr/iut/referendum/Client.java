package fr.iut.referendum;

import java.math.BigInteger;
import java.security.SecureRandom;

public class Client {
    public static BigInteger[] encrypt(BigInteger m, BigInteger g, BigInteger p, BigInteger q, BigInteger publicKey) {
        SecureRandom random = new SecureRandom();
        BigInteger k = new BigInteger(q.bitLength(), random).mod(q); // k aléatoire
        BigInteger c1 = g.modPow(k, p); // c1 = g^k mod p
        BigInteger c2 = m.multiply(publicKey.modPow(k, p)).mod(p); // c2 = m * publicKey^k mod p
        return new BigInteger[]{c1, c2};
    }
}
