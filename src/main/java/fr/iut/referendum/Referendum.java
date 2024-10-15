package fr.iut.referendum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Referendum {
    int id;
    String nom;
    ArrayList<String> choix;
    Map<String,String> loginClientvote = new HashMap<String,String>();
    String resultat = "";

    static int idCounter = 1;

    public Referendum(String nom, ArrayList<String> choix) {
        this.id = idCounter++;
        this.nom = nom;
        this.choix = choix;
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
}