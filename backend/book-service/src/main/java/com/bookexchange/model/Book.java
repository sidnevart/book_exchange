package com.bookexchange.model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private String genre;

    private Long ownerId; 




}
