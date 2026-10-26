package dev.bibliotheque.domaine;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Auteur d'un livre. Entité immuable : tous les champs sont {@code final}, il n'y a aucun
 * mutateur et Hibernate n'émet jamais d'UPDATE ({@link Immutable}).
 *
 * <p>L'identifiant est un UUID généré côté application à la création : pas d'aller-retour avec la
 * base pour obtenir une clé, ce qui convient bien à l'insertion en masse.
 */
@Entity
@Immutable
@Table(name = "auteur")
public class Auteur {

    @Id
    private final UUID id;

    @Column(nullable = false)
    private final String nom;

    @Column(nullable = false)
    private final String prenom;

    /** Optionnel : peut être {@code null} en base. */
    private final String pseudonyme;

    @Column(name = "date_naissance", nullable = false)
    private final LocalDate dateNaissance;

    /** Réservé à JPA/Hibernate, qui renseigne les champs par réflexion. */
    protected Auteur() {
        this.id = null;
        this.nom = null;
        this.prenom = null;
        this.pseudonyme = null;
        this.dateNaissance = null;
    }

    public Auteur(UUID id, String nom, String prenom, String pseudonyme, LocalDate dateNaissance) {
        this.id = Objects.requireNonNull(id, "id");
        this.nom = Objects.requireNonNull(nom, "nom");
        this.prenom = Objects.requireNonNull(prenom, "prenom");
        this.pseudonyme = pseudonyme;
        this.dateNaissance = Objects.requireNonNull(dateNaissance, "dateNaissance");
    }

    /** Crée un nouvel auteur avec un identifiant fraîchement généré. */
    public static Auteur nouveau(String nom, String prenom, String pseudonyme, LocalDate dateNaissance) {
        return new Auteur(UUID.randomUUID(), nom, prenom, pseudonyme, dateNaissance);
    }

    public UUID id() {
        return id;
    }

    public String nom() {
        return nom;
    }

    public String prenom() {
        return prenom;
    }

    public Optional<String> pseudonyme() {
        return Optional.ofNullable(pseudonyme);
    }

    public LocalDate dateNaissance() {
        return dateNaissance;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Auteur autre && id().equals(autre.id());
    }

    @Override
    public int hashCode() {
        return id().hashCode();
    }

    @Override
    public String toString() {
        return "Auteur[" + prenom() + " " + nom() + "]";
    }
}
