package org.anyone.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.sql.Time;

@Entity
public class FeedingSchedules {
    @Id
    @Column(name = "FeedingID")
    @GeneratedValue(strategy = GenerationType.TABLE)
    int feedingID;

    @ManyToOne
    @JoinColumn(name = "PetID", referencedColumnName = "PetID")
    @JsonIgnore
    Pet pet;

    @ManyToOne
    @JoinColumn(name = "FeederID", referencedColumnName = "FeederID")
    @JsonIgnore
    Feeder feeder;

    @Column(name = "FeedingTime")
    Time feedingTime;

    @Column(name = "FoodAmount")
    float foodAmount;
}
