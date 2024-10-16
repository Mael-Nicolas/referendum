package fr.iut.referendum;

public class MainClient {
    public static void main(String[] args) {
        String hostname = "109.176.197.88"; // Localhost ou 109.176.197.88 serv killian
        int port = 3390;

        Client c1 = new Client("bonsc", "12345678"); // Modif pour voter (Login)
        c1.run(hostname, port);
    }
}
