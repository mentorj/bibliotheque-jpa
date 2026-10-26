package dev.bibliotheque;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Paramètres d'exécution, immuables.
 *
 * @param nbAuteurs       nombre d'auteurs à insérer
 * @param livresParAuteur nombre de livres insérés pour chaque auteur
 * @param auteursParLot   nombre d'auteurs (et de leurs livres) par transaction
 */
public record Options(
        int nbAuteurs,
        int livresParAuteur,
        int auteursParLot,
        String url,
        String utilisateur,
        String motDePasse) {

    public Options {
        if (nbAuteurs < 1 || livresParAuteur < 1 || auteursParLot < 1) {
            throw new IllegalArgumentException("Les nombres d'auteurs, de livres et de lots doivent être >= 1");
        }
    }

    /**
     * Arguments positionnels : {@code [nbAuteurs [livresParAuteur [auteursParLot]]]}.
     * Connexion via les variables d'environnement {@code DB_URL}, {@code DB_USER}, {@code DB_PASSWORD}.
     */
    public static Options depuis(String[] args, Map<String, String> env) {
        return new Options(
                entier(args, 0, 1_000),
                entier(args, 1, 5),
                entier(args, 2, 200),
                env.getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/bibliotheque?reWriteBatchedInserts=true"),
                env.getOrDefault("DB_USER", "bibliotheque"),
                env.getOrDefault("DB_PASSWORD", "bibliotheque"));
    }

    /** Tailles des lots successifs, ex. 1000 auteurs par 400 donne {@code [400, 400, 200]}. */
    public List<Integer> taillesDesLots() {
        return IntStream.iterate(0, debut -> debut < nbAuteurs, debut -> debut + auteursParLot)
                .map(debut -> Math.min(auteursParLot, nbAuteurs - debut))
                .boxed()
                .toList();
    }

    /** Propriétés JPA qui surchargent celles de {@code persistence.xml}. */
    public Map<String, Object> proprietesJpa() {
        return Map.of(
                "jakarta.persistence.jdbc.url", url,
                "jakarta.persistence.jdbc.user", utilisateur,
                "jakarta.persistence.jdbc.password", motDePasse);
    }

    private static int entier(String[] args, int index, int parDefaut) {
        return args.length > index ? Integer.parseInt(args[index]) : parDefaut;
    }
}
