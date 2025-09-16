package com.LMS.Models;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String title;


    @Column(nullable = false, unique = true)
    private String isbn;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;


    @Column(nullable = false)
    private BigDecimal price;


    @ManyToOne
    @JoinColumn(name = "author_id")
    @JsonManagedReference
    private Author author;


    @Column(nullable = false)
    private boolean available = true;

    private BigDecimal extra_days_rental_price;

    private BigDecimal insurance_fees;
}