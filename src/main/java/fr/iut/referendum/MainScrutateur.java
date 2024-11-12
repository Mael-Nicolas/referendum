package fr.iut.referendum;

public class MainScrutateur {
    public static void main(String[] args) {
        String hostname = "Localhost"; // Localhost ou 109.176.197.88 serv killian
        int port = 3390;

        Scrutateur s = new Scrutateur(); // Modif pour voter (Login)
        s.run(hostname, port);
    }
}
