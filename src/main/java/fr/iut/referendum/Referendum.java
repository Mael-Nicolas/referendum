package fr.iut.referendum;

import java.util.*;

public class Referendum {
    private int id;
    private String nom;
    private ArrayList<String> choix;
    private Map<String,String> loginClientvote = new HashMap<String,String>();
    private Date dateFin;

    private static int idCounter = 1;

    public Referendum(String nom, Date dateFin) {
        this.id = idCounter++;
        this.nom = nom;
        this.choix = new ArrayList<>();
        this.choix.add("Oui");
        this.choix.add("Non");
        this.dateFin = dateFin;
    }

    public String getNom() {
        return nom;
    }

    public ArrayList<String> getChoix() {
        return choix;
    }

    public Map<String, String> getIdClientvote() {
        return loginClientvote;
    }

    public int getId() {
        return id;
    }

    public Date getDateFin() {
        return dateFin;
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
        return "Referendum [" + id + "] " + nom + " : " + choix + "\n" +
                " - Date de fin : " + dateFinAffichage() + "\n" +
                " - Temps restant : " + tempRestant();
    }

    private static int getMaxDaysInMonth(int year, int month) {
        switch (month) {
            case 4: case 6: case 9: case 11:
                return 30;
            case 2:
                if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
                    return 29; // Leap year
                } else {
                    return 28;
                }
            default:
                return 31;
        }
    }

    /*
    * Renvoie le resultat du referendum s'il est terminé
    * retourne 0 pour le choix 0
    * retourne 1 pour le choix 1
    * Sinon renvoie -1 (referendum non terminé)
     */
    public int getResultat(int nb) {
        if (tempRestant().equals("Terminé")) {
            if (loginClientvote.size()/2 > nb) {
                return 0;
            }
            else {
                return 1;
            }
        }
        return -1;
    }
}