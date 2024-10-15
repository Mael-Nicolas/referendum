package fr.iut.referendum;

public class MainClient {
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 3390;

        Client c1 = new Client("bonsc", "12345678"); // Modif pour voter (Login)
        c1.run(hostname, port);
    }
}
