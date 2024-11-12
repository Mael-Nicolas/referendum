package fr.iut.referendum;

import java.math.BigInteger;
import java.util.*;

public class Referendum {
    private int id;
    private String nom;
    private int nbVotants;
    private Date dateFin;
    private BigInteger[] votesAgrege;
    private BigInteger[] pk;
    private String resultat = "";
    private boolean open = false;

    private static int idCounter = 1;
    private Object[] votes;

    public Referendum(String nom, Date dateFin) {
        this.id = idCounter++;
        this.nom = nom;
        this.dateFin = dateFin;
        this.nbVotants = 0;
        this.votesAgrege = null;
    }

    public String getNom() {
        return nom;
    }

    public int getId() {
        return id;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public void setResultat(String resultat) {
        this.resultat = resultat;
    }

    public String dateFinAffichage() {
        int annee = dateFin.getYear() + 1900;
        int mois = dateFin.getMonth() + 1;
        return dateFin.getDate() + "/" + mois + "/" + annee + " " + dateFin.getHours() + ":" + dateFin.getMinutes();
    }

    public boolean fini() {
        Date dateNow = new Date();
        return dateNow.after(dateFin);
    }

    public String tempRestant() {
        Date dateNow = new Date();
        if (fini()) {
            open = false;
            return "Terminé";
        }
        String result = "";
        int annee = dateFin.getYear() - dateNow.getYear();
        int mois = dateFin.getMonth() - dateNow.getMonth();
        int jour = dateFin.getDate() - dateNow.getDate();
        int heure = dateFin.getHours() - dateNow.getHours();
        int min = dateFin.getMinutes() - dateNow.getMinutes();
        if (min < 0) {
            min += 60;
            heure--;
        }
        if (heure < 0) {
            heure += 24;
            jour--;
        }
        if (jour < 0) {
            int nbJour = getMaxDaysInMonth(annee, mois-1);
            jour += nbJour;
            mois--;
        }
        if (mois < 0) {
            mois += 12;
            annee--;
        }
        if (annee != 0) {
            result += annee + " an(s) ";
        }
        if (mois != 0) {
            result += mois + " mois ";
        }
        if (jour != 0) {
            result += jour + " jour(s) ";
        }
        result += heure + " heure(s) ";
        result += min + " minute(s) ";
        return result;
    }

    @Override
    public String toString() {
        return "Referendum [" + id + "] " + nom + " : Oui ou Non" + "\n" +
                " - Date de fin : " + dateFinAffichage() + "\n" +
                " - Temps restant : " + tempRestant();
    }

    public int getMaxDaysInMonth(int year, int month) {
        switch (month) {
            case 4: case 6: case 9: case 11:
                return 30;
            case 2:
                if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
                    return 29; // Année bisextile
                } else {
                    return 28;
                }
            default:
                return 31;
        }
    }

    public int getNbVotes() {
        return nbVotants;
    }

    public void ajouterVotant() {
        nbVotants++;
    }

    public BigInteger[] getClePublique() {
        return pk;
    }

    public void agregeVote(BigInteger[] c) {
        if (votesAgrege == null) {
            votesAgrege = c;
        }
        votesAgrege = Crypto.agrege(votesAgrege, c, pk);
    }

    public BigInteger[] getVotesAgrege() {
        return votesAgrege;
    }

    public void setPk(BigInteger[] pk) {
        this.pk = pk;
    }

    public int getNbVotants() {
        return nbVotants;
    }

    public String getResultat() {
        return resultat;
    }


    public Object[] getVotes() {
        return votes;
    }

    public void setVotes(Object[] votes) {
        this.votes = votes;
    }
}