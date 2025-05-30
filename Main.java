import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    static class EntrepriseComparable {
        String nom;
        BigDecimal vcp;
        BigDecimal vd;
        BigDecimal rex;

        public EntrepriseComparable(String[] line) {
            this.nom = line[0];
            this.vcp = new BigDecimal(line[1]);
            this.vd = new BigDecimal(line[2]);
            this.rex = new BigDecimal(line[3]);
        }

        public BigDecimal getVe() {
            return vcp.add(vd); // Ve = Vcp + Vd
        }

        public BigDecimal getMultiple() {
            if (rex.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
            return getVe().divide(rex, 4, RoundingMode.HALF_UP); // Ve / REX
        }

        public boolean isBeneficiaire() {
            return rex.compareTo(BigDecimal.ZERO) > 0;
        }
    }

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("data/comparables.csv"));
        lines.remove(0); // Supprimer l'en-tête

        List<EntrepriseComparable> comparables = new ArrayList<>();

        for (String line : lines) {
            String[] parts = line.split(";");
            EntrepriseComparable e = new EntrepriseComparable(parts);
            if (e.isBeneficiaire()) {
                comparables.add(e);
            }
        }

        // Calcul de la moyenne des multiples
        BigDecimal sommeMultiples = BigDecimal.ZERO;
        int compteur = 0;
        for (EntrepriseComparable e : comparables) {
            BigDecimal multiple = e.getMultiple();
            if (multiple.compareTo(BigDecimal.ZERO) > 0) {
                sommeMultiples = sommeMultiples.add(multiple);
                compteur++;
            }
        }

        BigDecimal multipleMoyen = sommeMultiples.divide(BigDecimal.valueOf(compteur), 4, RoundingMode.HALF_UP);
        System.out.println("Multiple moyen Ve/REX : " + multipleMoyen);

        // Saisie utilisateur
        Scanner scanner = new Scanner(System.in);
        System.out.print("Entrez le Résultat d'exploitation (REX) de l'entreprise cible : ");
        BigDecimal rexAM = scanner.nextBigDecimal();

        System.out.print("Entrez la dette nette (Vd) de l'entreprise cible : ");
        BigDecimal vdAM = scanner.nextBigDecimal();

        // Calculs
        BigDecimal veAM = multipleMoyen.multiply(rexAM);
        BigDecimal vcpAM = veAM.subtract(vdAM);

        // Résultats
        System.out.println("Valeur de l'actif économique (Ve) estimée : " + veAM.setScale(2, RoundingMode.HALF_UP) + " M€");
        System.out.println("Valeur des capitaux propres (Vcp) estimée : " + vcpAM.setScale(2, RoundingMode.HALF_UP) + " M€");
    }
}
