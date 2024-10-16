package fr.iut.referendum;

public class MainAdmin {
    public static void main(String[] args) {
        String hostname = "Localhost"; // Localhost ou 109.176.197.88 serv killian
        int port = 3390;

        Admin a1 = new Admin("Admin", "mdpAdmin");
        a1.run(hostname, port);
    }
}
