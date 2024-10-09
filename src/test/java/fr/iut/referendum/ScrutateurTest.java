package fr.iut.referendum;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigInteger;

class ScrutateurTest {
    @org.junit.jupiter.api.Test
    void testEncrypt() {
        BigInteger m = new BigInteger("123456789");
        BigInteger g = new BigInteger("2");
        BigInteger p = new BigInteger("101");
        BigInteger q = new BigInteger("5");
        BigInteger publicKey = new BigInteger("10");
        BigInteger[] result = Scrutateur.encrypt(m, g, p, q, publicKey);
        assertEquals(new BigInteger("32"), result[0]);
        assertEquals(new BigInteger("50"), result[1]);
    }
}