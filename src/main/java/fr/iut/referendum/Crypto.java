package fr.iut.referendum;

import java.math.BigInteger;

public interface Crypto {
    BigInteger[] encrypt(BigInteger m, BigInteger[] pk);

    BigInteger[] genkey();

    BigInteger[] agrege(BigInteger[] c1, BigInteger[] c2, BigInteger[] pk);

    BigInteger decrypt(BigInteger[] c, BigInteger[] pk, BigInteger sk, int nbVotants);
}
