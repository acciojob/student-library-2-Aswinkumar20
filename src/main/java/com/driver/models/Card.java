package com.driver.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Builder

@Data
public class Card {
    public Card(){
        this.cardStatus = CardStatus.ACTIVATED;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(mappedBy = "card", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("card")
    private Student student;

    @CreationTimestamp
    private Date createdOn;

    @UpdateTimestamp
    private Date updatedOn;

    @Enumerated(value = EnumType.STRING)
    private CardStatus cardStatus;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("card")
    private List<Book> books;

    public Card(int id, Student student, Date createdOn, Date updatedOn, CardStatus cardStatus, List<Book> books) {
        this.id = id;
        this.student = student;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
        this.cardStatus = cardStatus;
        this.books = books;
    }


}
