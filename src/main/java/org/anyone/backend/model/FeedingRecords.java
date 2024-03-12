package org.anyone.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class FeedingRecords {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "RecordID")
    int recordID;

    @ManyToOne
    @JoinColumn(name = "PetID", referencedColumnName = "PetID")
    @JsonIgnore
    Pet pet;

    @Column(name = "FeedingDateTime")
    LocalDateTime feedingDateTime;

    @Column(name = "FoodAmount")
    float foodAmount;
}
