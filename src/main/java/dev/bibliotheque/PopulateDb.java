package dev.bibliotheque;

import dev.bibliotheque.domaine.Auteur;
import dev.bibliotheque.domaine.Livre;
import dev.bibliotheque.generation.DonneesAleatoires;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import net.datafaker.Faker;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Insère en masse des auteurs et des livres aléatoires dans PostgreSQL.
 *
 * <p>Usage : {@code Main [nbAuteurs [livresParAuteur [auteursParLot]]]}
 */
public final class PopulateDb {

    private record Resultat(int auteurs, int livres) {
        static final Resultat VIDE = new Resultat(0, 0);

        Resultat plus(Resultat autre) {
            return new Resultat(auteurs + autre.auteurs, livres + autre.livres);
        }
    }

    private PopulateDb() {
    }

    public static void main(String[] args) {
        Options options = Options.depuis(args, System.getenv());
        DonneesAleatoires donnees = new DonneesAleatoires(new Faker(Locale.FRENCH), Clock.systemDefaultZone());
        Instant debut = Instant.now();

        try (EntityManagerFactory emf =
                     Persistence.createEntityManagerFactory("bibliotheque", options.proprietesJpa())) {

            Resultat total = options.taillesDesLots().stream()
                    .map(taille -> insererLot(emf, donnees, taille, options.livresParAuteur()))
                    .reduce(Resultat.VIDE, Resultat::plus);

            Duration duree = Duration.between(debut, Instant.now());
            System.out.printf("Terminé : %d auteurs et %d livres insérés en %.1f s%n",
                    total.auteurs(), total.livres(), duree.toMillis() / 1000.0);
        }
    }

    /** Génère un lot (immuable), puis l'insère dans une seule transaction. */
    private static Resultat insererLot(
            EntityManagerFactory emf, DonneesAleatoires donnees, int nbAuteurs, int livresParAuteur) {

        List<Auteur> auteurs = Stream.generate(donnees::auteur).limit(nbAuteurs).toList();
        List<Livre> livres = auteurs.stream()
                .flatMap(auteur -> donnees.livres(auteur, livresParAuteur))
                .toList();

        // Un EntityManager neuf par lot : le contexte de persistance reste petit.
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            try {
                auteurs.forEach(em::persist);
                livres.forEach(em::persist);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }
        }

        Resultat lot = new Resultat(auteurs.size(), livres.size());
        System.out.printf("Lot inséré : %d auteurs, %d livres%n", lot.auteurs(), lot.livres());
        return lot;
    }
}
