package fr.iut.referendum.Serveur;

import fr.iut.referendum.ConnexionBD;
import fr.iut.referendum.Crypto.Crypto;
import fr.iut.referendum.Crypto.ElGamalCrypto;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Referendum {
    private int id;
    private String nom;
    private int nbVotants;
    private LocalDateTime dateFin;
    private BigInteger[] votesAgrege;
    private BigInteger[] pk;
    private String resultat;
    private final ConnexionBD connexionBD;
    Crypto crypto = new ElGamalCrypto();

    public Referendum(int id, String nom, LocalDateTime dateFin, BigInteger[] votesAgrege, String resultat, BigInteger[] pk) {
        this.id = id;
        this.nom = nom;
        this.dateFin = dateFin;
        this.nbVotants = 0;
        this.votesAgrege = votesAgrege;
        connexionBD = new ConnexionBD();
        this.resultat = resultat;
        this.pk = pk;
    }

    public String getNom() {
        return nom;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public boolean isOpen() {
        return !fini() && pk != null;
    }

    public void setResultat(String resultat) {
        this.resultat = resultat;
        connexionBD.changerResultatReferendum(id, resultat);
    }

    public String dateFinAffichage() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return dateFin.format(formatter);
    }

    public boolean fini() {
        return LocalDateTime.now().isAfter(dateFin);
    }

    public String tempRestant() {
        if (fini()) {
            return "Terminé";
        }
        LocalDateTime now = LocalDateTime.now();
        int years = dateFin.getYear() - now.getYear();
        int months = dateFin.getMonthValue() - now.getMonthValue();
        int days = dateFin.getDayOfMonth() - now.getDayOfMonth();
        int hours = dateFin.getHour() - now.getHour();
        int minutes = dateFin.getMinute() - now.getMinute();

        if (minutes < 0) {
            minutes += 60;
            hours--;
        }
        if (hours < 0) {
            hours += 24;
            days--;
        }
        if (days < 0) {
            months--;
            days += now.getMonth().length(now.toLocalDate().isLeapYear());
        }
        if (months < 0) {
            years--;
            months += 12;
        }

        StringBuilder sb = new StringBuilder();
        if (years > 0) {
            sb.append(years).append(" an(s) ");
        }
        if (months > 0) {
            sb.append(months).append(" mois ");
        }
        if (days > 0) {
            sb.append(days).append(" jour(s) ");
        }
        if (hours > 0) {
            sb.append(hours).append(" heure(s) ");
        }
        if (minutes > 0) {
            sb.append(minutes).append(" minute(s)");
        }

        return sb.toString().trim();
    }

    @Override
    public String toString() {
        return id + " - " + nom + " - " + dateFinAffichage() + " - " + (isOpen()? "Ouvert" : "Fermé") + " - " + tempRestant();
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
        if (votesAgrege[0].compareTo(BigInteger.ZERO) == 0) {
            votesAgrege = c;
        } else {
            votesAgrege = crypto.agrege(votesAgrege, c, pk);
        }
        connexionBD.changerAgregeReferendum(id, votesAgrege);
    }

    public BigInteger[] getVotesAgrege() {
        return votesAgrege;
    }

    public void setPk(BigInteger[] pk) {
        this.pk = pk;
        connexionBD.changerClePubliqueReferendum(id, pk);
    }

    public int getNbVotants() {
        return nbVotants;
    }

    public String getResultat() {
        return resultat;
    }
}