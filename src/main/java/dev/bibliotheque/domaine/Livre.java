package dev.bibliotheque.domaine;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/** Livre écrit par un auteur. Entité immuable, voir {@link Auteur}. */
@Entity
@Immutable
@Table(name = "livre")
public class Livre {

    @Id
    private final UUID id;

    @Column(nullable = false)
    private final String titre;

    @Column(nullable = false)
    private final String editeur;

    @Column(name = "date_publication", nullable = false)
    private final LocalDate datePublication;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auteur_id", nullable = false)
    private final Auteur auteur;

    /** Réservé à JPA/Hibernate, qui renseigne les champs par réflexion. */
    protected Livre() {
        this.id = null;
        this.titre = null;
        this.editeur = null;
        this.datePublication = null;
        this.auteur = null;
    }

    public Livre(UUID id, String titre, String editeur, LocalDate datePublication, Auteur auteur) {
        this.id = Objects.requireNonNull(id, "id");
        this.titre = Objects.requireNonNull(titre, "titre");
        this.editeur = Objects.requireNonNull(editeur, "editeur");
        this.datePublication = Objects.requireNonNull(datePublication, "datePublication");
        this.auteur = Objects.requireNonNull(auteur, "auteur");
    }

    /** Crée un nouveau livre avec un identifiant fraîchement généré. */
    public static Livre nouveau(String titre, String editeur, LocalDate datePublication, Auteur auteur) {
        return new Livre(UUID.randomUUID(), titre, editeur, datePublication, auteur);
    }

    public UUID id() {
        return id;
    }

    public String titre() {
        return titre;
    }

    public String editeur() {
        return editeur;
    }

    public LocalDate datePublication() {
        return datePublication;
    }

    public Auteur auteur() {
        return auteur;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Livre autre && id().equals(autre.id());
    }

    @Override
    public int hashCode() {
        return id().hashCode();
    }

    @Override
    public String toString() {
        return "Livre[" + titre() + ", " + datePublication() + "]";
    }
}
