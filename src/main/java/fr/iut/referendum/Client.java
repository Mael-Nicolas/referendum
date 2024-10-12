package fr.iut.referendum;

public class Client {
    String login;
    String password;
    Serveur serveur;

    public Client(String login, String password, Serveur serveur) {
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

    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
