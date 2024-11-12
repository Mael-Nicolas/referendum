package fr.iut.referendum;

import java.math.BigInteger;
import java.security.SecureRandom;

public class Scrutateur {

    private final BigInteger[] pk;
    private final BigInteger sk;
    private BigInteger resChiffre;

    public Scrutateur() {
        BigInteger[] tab = Crypto.genkey();
        pk = new BigInteger[]{tab[0],tab[1],tab[2]};
        sk = tab[3];
    }

    public BigInteger[] getPk() {
        return pk;
    }

    public BigInteger setResChiffre(BigInteger resChiffre) {
        this.resChiffre = resChiffre;
    }

    public BigInteger dechiffrer(BigInteger[] agrege, int nbVotants) {
        BigInteger resultat = Crypto.decrypt(agrege, pk, sk, nbVotants);
        return resultat;
    }
}
