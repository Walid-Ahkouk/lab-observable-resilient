package com.example.bookservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;

    // Prix récupéré du pricing-service (transient ou persisté selon le besoin,
    // ici on peut le stocker pour l'exemple ou juste le renvoyer.
    // Le sujet dit "récupérer le prix", on va supposer qu'on le stocke ou
    // l'enrichit à la volée.
    // Pour simplifier crud, on va dire que le Book a un prix de base, et le pricing
    // service donne un prix "réel" ou une remise?
    // Le prompt dit : "Appel externe à pricing-service... pour récupérer le prix".
    // On va stocker le prix dans l'entité pour le CRUD simple, et peut-être le
    // mettre à jour via le service ?
    // Ou alors le prix est purement externe.
    // Vu "Méthodes CRUD", on va stocker un prix 'fixe' en base, et peut-être une
    // méthode spécifique pour enrichir ?
    // Simplifions : On stocke un prix en base. Lors de la création/récupération, on
    // peut appeler le service pour setter/ajuster ce prix.
    // Allons au plus simple : Le book a un champ price.
    private Double price;

    @Version
    private Long version;

    public Book() {
    }

    public Book(String title, String author, Double price) {
        this.title = title;
        this.author = author;
        this.price = price;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
