package fr.iut.referendum;

import java.util.List;

public class Referendum {
    String nom;
    List<String> choix;
    String resultat;

    public Referendum(String nom, List<String> choix) {
        this.nom = nom;
        this.choix = choix;
    }

    public String getNom() {
        return nom;
    }

    public List<String> getChoix() {
        return choix;
    }

    public String getResultat() {
        return resultat;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setChoix(List<String> choix) {
        this.choix = choix;
    }

    public void setResultat(String resultat) {
        this.resultat = resultat;
    }

    public void addChoix(String choix) {
        this.choix.add(choix);
    }

    public void removeChoix(String choix) {
        this.choix.remove(choix);
    }
}