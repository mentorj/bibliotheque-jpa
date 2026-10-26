# Bibliothèque – entités JPA immuables et insertion en masse

Modèle simple **Auteur** (nom, prénom, pseudonyme, date de naissance) et **Livre** (titre, éditeur,
date de publication, auteur), plus un programme qui insère des auteurs et des livres aléatoires
([Datafaker](https://www.datafaker.net/)) dans PostgreSQL via JPA/Hibernate.

## Lancer

Prérequis : JDK 21+, Maven 3.9+, Docker (ou un PostgreSQL existant).

```bash
docker compose up -d          # PostgreSQL 16 : base, utilisateur et mot de passe « bibliotheque »

# 1000 auteurs x 5 livres, transactions de 200 auteurs (valeurs par défaut)
mvn -q compile exec:java

# 20 000 auteurs x 10 livres, lots de 500 auteurs
mvn -q compile exec:java -Dexec.args="20000 10 500"
```

Connexion (variables d'environnement) : `DB_URL`, `DB_USER`, `DB_PASSWORD`. Par défaut
`jdbc:postgresql://localhost:5432/bibliotheque?reWriteBatchedInserts=true`.
Les tables `auteur` et `livre` sont créées au premier lancement (`hibernate.hbm2ddl.auto=update`).

## Choix de conception

* **Entités immuables** : champs `final`, aucun mutateur, `@Immutable` (jamais d'UPDATE),
  constructeur `protected` sans argument réservé à Hibernate, fabriques statiques `nouveau(...)`.
  Les valeurs optionnelles sont exposées en `Optional` (`Auteur.pseudonyme()`).
* **Identifiants UUID générés côté application** : pas de séquence ni d'aller-retour pour obtenir une
  clé, donc des insertions groupées efficaces.
* **Insertion groupée** : `hibernate.jdbc.batch_size=50`, `hibernate.order_inserts=true` et
  `reWriteBatchedInserts=true` côté pilote PostgreSQL.
* **Programme principal** : la génération et le découpage en lots sont des flux sur des listes
  immuables (`Options`, `Resultat`, `List.copyOf`/`toList`). Seule l'écriture en base a des effets de
  bord, dans une transaction par lot.

```
src/main/java/dev/bibliotheque/
├── Main.java                      programme principal
├── Options.java                   paramètres (record)
├── domaine/Auteur.java, Livre.java
└── generation/DonneesAleatoires.java
```
