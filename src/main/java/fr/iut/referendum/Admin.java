package fr.iut.referendum;

import java.util.List;

public class Admin {
    String login;
    String password;
    Serveur serveur;

    public Admin(String login, String password, Serveur serveur) {
        this.login = login;
        this.password = password;
        this.serveur = serveur;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Serveur getServeur() {
        return serveur;
    }

    public void setServeur(Serveur serveur) {
        this.serveur = serveur;
    }

    public void creerReferendum(String nom, List<String> choix) {
        this.serveur.creerReferendum(new Referendum(nom, choix));
    }

    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
