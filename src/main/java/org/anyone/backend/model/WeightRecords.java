package org.anyone.backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class WeightRecords {
    @Id
    @Column(name = "RecordID")
    @GeneratedValue(strategy = GenerationType.TABLE)
    int RecordID;

    @ManyToOne
    @JoinColumn(name = "PetID", referencedColumnName = "PetID")
    Pet pet;

    @Column(name = "Weight")
    float weight;

    @Column(name = "RecordDateTime")
    LocalDateTime recordDateTime;

    public WeightRecords() {
    }

    public WeightRecords(int recordID, Pet pet, float weight, LocalDateTime recordDateTime) {
        RecordID = recordID;
        this.pet = pet;
        this.weight = weight;
        this.recordDateTime = recordDateTime;
    }

    @Override
    public String toString() {
        return "WeightRecords{" +
                "RecordID=" + RecordID +
                ", pet=" + pet +
                ", weight=" + weight +
                ", recordDateTime=" + recordDateTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeightRecords that = (WeightRecords) o;
        return RecordID == that.RecordID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(RecordID);
    }

    public int getRecordID() {
        return RecordID;
    }

    public void setRecordID(int recordID) {
        RecordID = recordID;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public LocalDateTime getRecordDateTime() {
        return recordDateTime;
    }

    public void setRecordDateTime(LocalDateTime recordDateTime) {
        this.recordDateTime = recordDateTime;
    }
}
