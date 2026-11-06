package dev.bibliotheque;

import dev.bibliotheque.domaine.Auteur;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Persistence;
import org.hibernate.Session;

import java.util.List;

/**
 * Query data from the bibliotheque database using different JPA  queries
 * with the P6Spy JDBC proxy driver to find slow queries.
 * Use the proxied persistent  unit from persistent.xml
 *
 * @author deadbrain
 */
public class QueryData {
    public static void main(String[] args) {
        try (EntityManagerFactory emf =
                     Persistence.createEntityManagerFactory(
                             "bibliotheque-spy");) {
            EntityManager mgr = emf.createEntityManager();
            List<Auteur> auteursWithLongNames =
                    mgr.createNamedQuery("findByLargeNames")
                            .getResultList();

            List<Auteur> auteursByNameAndFirstName =
                    mgr.createNamedQuery("findByNameAndFirstName")
                            .setParameter("nom", "toto")
                            .setParameter("prenom", "tata")
                            .getResultList();


            auteursByNameAndFirstName.stream().forEach(System.out::println);
            System.out.println("Show auteurs with large names");
            auteursWithLongNames.stream().forEach(System.out::println);

            // query with joins
            List<Auteur> profilicAuthors = mgr.createNamedQuery("findProlificAuthors")
                    .setParameter("numBooks",5L)
                    .getResultList();

            System.out.println("Show prolific authors among "+ profilicAuthors.size() + "results found");
            profilicAuthors.stream().forEach(System.out::println);

        }
    }
}
