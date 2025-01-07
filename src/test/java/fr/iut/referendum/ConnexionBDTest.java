package fr.iut.referendum;

import oracle.sql.DATE;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import java.util.Date;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;


public class ConnexionBDTest {

    private ConnexionBD maConnexionBD;

    @BeforeEach
    public void initialize() {
        maConnexionBD = new ConnexionBD();
    }

    @Test
    public void testConnexionBD() {
        assertNotNull(maConnexionBD);
    }

    @Test
    public void testEmployeConnexion() {
        boolean vrai = maConnexionBD.employeConnexion("nicolasm", "12345");
        boolean faux = maConnexionBD.employeConnexion("nico", "15");
        assertTrue(vrai);
        assertFalse(faux);
    }

    @Test
    public void testAjoutEmployeExistant() {
        boolean faux = maConnexionBD.creerEmploye("nicolasm", "12345");
        assertFalse(faux);
    }

    @Disabled
    @Test
    public void testAjoutEmploye() {
        boolean vrai = maConnexionBD.creerEmploye("ben", "12345");
        assertTrue(vrai);
    }

    @Test
    public void testAVoteUtilisateurNouveau() {
        boolean aVote = maConnexionBD.aVote("rivasr", 0);
        assertFalse(aVote);
    }

    @Disabled
    @Test
    public void testVoterUtilisateur() {
        boolean vrai = maConnexionBD.voter("nicolasm", 0);
        assertTrue(vrai);
    }

    @Test
    public void testAVoteUtilisateurDejaVotant() {
        boolean aVote = maConnexionBD.aVote("nicolasm", 0);
        assertTrue(aVote);
    }

    @Test
    public void testVoterUtilisateurNonExistant() {
        boolean faux = maConnexionBD.voter("pjoiqsdpjoqsdhoia", 0);
        assertFalse(faux);
    }

    @Test
    public void testVoterDeuxFois() {
        boolean dejaVotant = maConnexionBD.voter("nicolasm", 0);
        assertFalse(dejaVotant);
    }

    @Test
    public void testVoterCléNonExistante() {
        boolean faux = maConnexionBD.voter("nicolasm", 999);
        assertFalse(faux);
    }

    @Test
    public void testAjoutAdminExistant() {
        boolean faux = maConnexionBD.creerAdmin("ben01", "12345");
        assertFalse(faux);
    }

    @Test
    public void testPasserAdminDejaAdmin() {
        boolean vrai = maConnexionBD.passerAdmin("rivasr");
        assertTrue(vrai);
    }

    @Test
    public void testEstPasAdmin() {
        boolean faux = maConnexionBD.estAdmin("Ben10");
        assertFalse(faux);
    }

    @Test
    public void testEstAdmin() {
        boolean vrai = maConnexionBD.estAdmin("rivasr");
        assertTrue(vrai);
    }

    @Test
    public void testSuppressionReferendum() {
        boolean vrai = maConnexionBD.supprimerReferendum(8);
        assertTrue(vrai);
    }


}
