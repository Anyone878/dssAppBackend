package org.anyone.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Objects;

@Entity
public class FeedingSchedules {
    @Id
    @Column(name = "FeedingID")
    @GeneratedValue(strategy = GenerationType.TABLE)
    int feedingID;

    @ManyToOne
    @JoinColumn(name = "UserID", referencedColumnName = "UserID")
    @JsonIgnore
    Users user;

    @ManyToOne
    @JoinColumn(name = "PetID", referencedColumnName = "PetID")
    @JsonIgnore
    Pet pet;

    @ManyToOne
    @JoinColumn(name = "FeederID", referencedColumnName = "FeederID")
    @JsonIgnore
    Feeder feeder;

    @Column(name = "FeedingTime")
    LocalTime feedingTime;

    @Column(name = "FoodAmount")
    float foodAmount;

    public FeedingSchedules() {
    }

    public FeedingSchedules(int feedingID, Pet pet, Feeder feeder, LocalTime feedingTime, float foodAmount) {
        this.feedingID = feedingID;
        this.pet = pet;
        this.feeder = feeder;
        this.feedingTime = feedingTime;
        this.foodAmount = foodAmount;
    }

    public FeedingSchedules(Users user, Pet pet, Feeder feeder, LocalTime feedingTime) {
        this.user = user;
        this.pet = pet;
        this.feeder = feeder;
        this.feedingTime = feedingTime;
    }

    @Override
    public String toString() {
        return "FeedingSchedules{" +
                "feedingID=" + feedingID +
                ", pet=" + pet +
                ", feeder=" + feeder +
                ", feedingTime=" + feedingTime +
                ", foodAmount=" + foodAmount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedingSchedules that = (FeedingSchedules) o;
        return feedingID == that.feedingID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedingID);
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public int getFeedingID() {
        return feedingID;
    }

    public void setFeedingID(int feedingID) {
        this.feedingID = feedingID;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public Feeder getFeeder() {
        return feeder;
    }

    public void setFeeder(Feeder feeder) {
        this.feeder = feeder;
    }

    public LocalTime getFeedingTime() {
        return feedingTime;
    }

    public void setFeedingTime(LocalTime feedingTime) {
        this.feedingTime = feedingTime;
    }

    public float getFoodAmount() {
        return foodAmount;
    }

    public void setFoodAmount(float foodAmount) {
        this.foodAmount = foodAmount;
    }
}
