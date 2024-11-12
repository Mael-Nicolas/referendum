package fr.iut.referendum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class AdminTest {

    private Admin admin;
    private PrintWriter writer;
    private BufferedReader reader;

    @BeforeEach
    void setUp() {
        admin = new Admin("admin", "password");
        writer = new PrintWriter(new StringWriter(), true);
        reader = new BufferedReader(new StringReader(""));
    }

    @Test
    void testGetMaxDaysInMonth() {
        assertEquals(31, Admin.getMaxDaysInMonth(2023, 1)); // Janvier
        assertEquals(28, Admin.getMaxDaysInMonth(2023, 2)); // Février non bissextile
        assertEquals(29, Admin.getMaxDaysInMonth(2024, 2)); // Février bissextile
        assertEquals(30, Admin.getMaxDaysInMonth(2023, 4)); // Avril
    }

    @Test
    void testEnvoyeDate() {
        StringWriter output = new StringWriter();
        PrintWriter writer = new PrintWriter(output, true);
        String simulatedInput = "2024\n3\n15\n10\n"; // 15 mars 2024 à 10h
        Scanner scanner = new Scanner(simulatedInput);

        Admin.envoyeDate(writer, scanner);

        String[] lines = output.toString().split(System.lineSeparator());
        assertEquals("2024", lines[0]); // Année
        assertEquals("3", lines[1]); // Mois
        assertEquals("15", lines[2]); // Jour
        assertEquals("10", lines[3]); // Heure
    }

    @Test
    void testInfoReferendum() throws IOException {
        StringWriter output = new StringWriter();
        PrintWriter writer = new PrintWriter(output, true);
        String simulatedResponse = "Server response: Referendum information\n\n"; // Réponse simulée du serveur
        BufferedReader reader = new BufferedReader(new StringReader(simulatedResponse));

        Admin.infoReferendum(writer, reader);

        // Vérifie que la commande "GET_SERVER_INFO" a bien été envoyée
        assertTrue(output.toString().contains("GET_SERVER_INFO"));
    }

    @Test
    void testNewReferendum() throws IOException {
        StringWriter output = new StringWriter();
        PrintWriter writer = new PrintWriter(output, true);
        String simulatedInput = "Test Referendum\n2024\n3\n15\n10\n"; // Nom et date
        Scanner scanner = new Scanner(simulatedInput);
        BufferedReader reader = new BufferedReader(new StringReader("Referendum created\n"));

        Admin.newReferendum(writer, scanner, reader);

        String[] lines = output.toString().split(System.lineSeparator());
        assertEquals("NEW_REFERENDUM", lines[0]); // Vérifie la commande envoyée
        assertEquals("Test Referendum", lines[1]); // Vérifie le nom du référendum
        assertEquals("2024", lines[2]); // Année
        assertEquals("3", lines[3]); // Mois
        assertEquals("15", lines[4]); // Jour
        assertEquals("10", lines[5]); // Heure
    }
}
