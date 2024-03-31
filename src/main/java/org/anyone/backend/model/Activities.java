package org.anyone.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Activities {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "ActivityID")
    int activityID;

    @ManyToOne
    @JoinColumn(name = "PetID", referencedColumnName = "PetID")
    @JsonIgnore
    Pet pet;

    @Column(name = "Date")
    LocalDate date;

    @Column(name = "Calorie")
    int calorie;

    @Column(name = "Exercise")
    @Convert(converter = DurationToStringConverter.class)
    Duration exercise;

    @Column(name = "Move")
    float move;

    @Converter
    static class DurationToStringConverter implements AttributeConverter<Duration, String> {

        @Override
        public String convertToDatabaseColumn(Duration attribute) {
            return attribute == null ? null : attribute.toString();
        }

        @Override
        public Duration convertToEntityAttribute(String dbData) {
            return dbData == null ? null : Duration.parse(dbData);
        }
    }

    public Activities() {

    }

    public Activities(int activityID, Pet pet, LocalDate date, int calorie, Duration exercise, float move) {
        this.activityID = activityID;
        this.pet = pet;
        this.date = date;
        this.calorie = calorie;
        this.exercise = exercise;
        this.move = move;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activities that = (Activities) o;
        return activityID == that.activityID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(activityID);
    }

    @Override
    public String toString() {
        return "Activities{" +
                "activityID=" + activityID +
                ", pet=" + pet +
                ", date=" + date +
                ", calorie=" + calorie +
                ", exercise=" + exercise +
                ", move=" + move +
                '}';
    }

    public int getActivityID() {
        return activityID;
    }

    public void setActivityID(int activityID) {
        this.activityID = activityID;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getCalorie() {
        return calorie;
    }

    public void setCalorie(int calorie) {
        this.calorie = calorie;
    }

    public Duration getExercise() {
        return exercise;
    }

    public void setExercise(Duration exercise) {
        this.exercise = exercise;
    }

    public float getMove() {
        return move;
    }

    public void setMove(float move) {
        this.move = move;
    }
}
