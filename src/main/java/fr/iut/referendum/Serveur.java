package fr.iut.referendum;

import java.util.List;

public class Serveur {
    List<Referendum> referendums;

    public Serveur(List<Referendum> referendums) {
        this.referendums = referendums;
    }

    public List<Referendum> getReferendums() {
        return referendums;
    }

    public void setReferendums(List<Referendum> referendums) {
        this.referendums = referendums;
    }

    public void addReferendum(Referendum referendum) {
        this.referendums.add(referendum);
    }

    public void removeReferendum(Referendum referendum) {
        this.referendums.remove(referendum);
    }

    public void creerReferendum(Referendum referendum) {
        this.referendums.add(referendum);
    }

    @Override
    public String toString() {
        String result = "";
        for (Referendum referendum : referendums) {
            result += referendum.getNom() + " : " + referendum.getChoix() + "\n";
        }
        return result;
    }

    public static void main(String[] args) {
        Referendum r1 = new Referendum("Killian président ?", List.of("Oui", "Non"));
        Referendum r2 = new Referendum("Vincent revienne a Montpellier ?", List.of("Oui", "Non", "Blanc"));
        Referendum r3 = new Referendum("Ouverture BL3 ?", List.of("Oui", "Non", "Blanc", "Nul"));
        Serveur serveur = new Serveur(List.of(r1, r2, r3));
        System.out.println(serveur);
    }
}