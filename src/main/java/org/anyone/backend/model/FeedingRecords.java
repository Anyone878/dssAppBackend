package org.anyone.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

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

    public FeedingRecords() {
    }

    public FeedingRecords(int recordID, Pet pet, LocalDateTime feedingDateTime, float foodAmount) {
        this.recordID = recordID;
        this.pet = pet;
        this.feedingDateTime = feedingDateTime;
        this.foodAmount = foodAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedingRecords that = (FeedingRecords) o;
        return recordID == that.recordID;
    }

    @Override
    public String toString() {
        return "FeedingRecords{" +
                "recordID=" + recordID +
                ", pet=" + pet +
                ", feedingDateTime=" + feedingDateTime +
                ", foodAmount=" + foodAmount +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(recordID);
    }

    public int getRecordID() {
        return recordID;
    }

    public void setRecordID(int recordID) {
        this.recordID = recordID;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public LocalDateTime getFeedingDateTime() {
        return feedingDateTime;
    }

    public void setFeedingDateTime(LocalDateTime feedingDateTime) {
        this.feedingDateTime = feedingDateTime;
    }

    public float getFoodAmount() {
        return foodAmount;
    }

    public void setFoodAmount(float foodAmount) {
        this.foodAmount = foodAmount;
    }
}
