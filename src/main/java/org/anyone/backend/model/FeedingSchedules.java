package org.anyone.backend.model;

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
    Pet pet;

    @ManyToOne
    @JoinColumn(name = "FeederID", referencedColumnName = "FeederID")
    Feeder feeder;

    @Column(name = "FeedingTime")
    Time feedingTime;

    @Column(name = "FoodAmount")
    float foodAmount;
}
