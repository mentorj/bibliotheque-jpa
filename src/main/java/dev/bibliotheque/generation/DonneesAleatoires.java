package dev.bibliotheque.generation;

import dev.bibliotheque.domaine.Auteur;
import dev.bibliotheque.domaine.Livre;
import net.datafaker.Faker;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

/**
 * Fabrique d'auteurs et de livres aux valeurs aléatoires (Datafaker).
 *
 * <p>Ne produit que des objets immuables et ne garde aucun état hormis le générateur aléatoire.
 */
public final class DonneesAleatoires {

    private static final int AGE_MIN = 18;
    private static final int AGE_MAX = 90;

    private final Faker faker;
    private final Clock horloge;

    public DonneesAleatoires(Faker faker, Clock horloge) {
        this.faker = faker;
        this.horloge = horloge;
    }

    /** Un auteur adulte, avec un pseudonyme une fois sur deux. */
    public Auteur auteur() {
        LocalDate naissance = faker.timeAndDate().birthday(AGE_MIN, AGE_MAX);
        String pseudonyme = faker.bool().bool() ? faker.name().username() : null;
        return Auteur.nouveau(faker.name().lastName(), faker.name().firstName(), pseudonyme, naissance);
    }

    /** Un livre publié par {@code auteur} entre ses 18 ans et aujourd'hui. */
    public Livre livre(Auteur auteur) {
        LocalDate premierePublicationPossible = auteur.dateNaissance().plusYears(AGE_MIN);
        long jours = ChronoUnit.DAYS.between(premierePublicationPossible, LocalDate.now(horloge));
        LocalDate publication = premierePublicationPossible
                .plusDays(faker.number().numberBetween(0L, Math.max(1L, jours)));
        return Livre.nouveau(faker.book().title(), faker.book().publisher(), publication, auteur);
    }

    /** {@code nombre} livres de {@code auteur}, sous forme de flux évalué paresseusement. */
    public Stream<Livre> livres(Auteur auteur, int nombre) {
        return Stream.generate(() -> livre(auteur)).limit(nombre);
    }
}
