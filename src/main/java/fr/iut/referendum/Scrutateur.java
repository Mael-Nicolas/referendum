package fr.iut.referendum;

import java.math.BigInteger;
import java.security.SecureRandom;

public class Scrutateur {

    private final BigInteger[] pk;
    private final BigInteger sk;
    private BigInteger resChiffre;

    public Scrutateur() {
        pk, sk = Crypto.genkey();
    }

    public BigInteger[] getPk() {
        return pk;
    }

    public BigInteger setResChiffre(BigInteger resChiffre) {
        this.resChiffre = resChiffre;
    }

    public BigInteger dechiffrer() {
        Crypto.decrypt();
        return resultat;
    }
}
