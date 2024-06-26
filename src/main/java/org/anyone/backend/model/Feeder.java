package org.anyone.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Feeder {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "FeederID")
    int feederID;

    @OneToOne
    @JoinColumn(name = "UserID", referencedColumnName = "UserID")
    @JsonIgnore
    Users user;

    @Column(name = "FoodCapacity")
    float foodCapacity;

    @Column(name = "WaterCapacity")
    float waterCapacity;

    @Column(name = "EverydayFoodPlan")
    float everydayFoodPlan;

    public Feeder() {
    }

    public Feeder(int feederID, Users user, float foodCapacity, float waterCapacity, float everydayFoodPlan) {
        this.feederID = feederID;
        this.user = user;
        this.foodCapacity = foodCapacity;
        this.waterCapacity = waterCapacity;
        this.everydayFoodPlan = everydayFoodPlan;
    }

    @Override
    public String toString() {
        return "Feeder{" +
                "feederID=" + feederID +
                ", user=" + user +
                ", foodCapacity=" + foodCapacity +
                ", waterCapacity=" + waterCapacity +
                ", everydayFoodPlan=" + everydayFoodPlan +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feeder feeder = (Feeder) o;
        return feederID == feeder.feederID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(feederID);
    }

    public float getEverydayFoodPlan() {
        return everydayFoodPlan;
    }

    public void setEverydayFoodPlan(float everydayFoodPlan) {
        this.everydayFoodPlan = everydayFoodPlan;
    }

    public int getFeederID() {
        return feederID;
    }

    public void setFeederID(int feederID) {
        this.feederID = feederID;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public float getFoodCapacity() {
        return foodCapacity;
    }

    public void setFoodCapacity(float foodCapacity) {
        this.foodCapacity = foodCapacity;
    }

    public float getWaterCapacity() {
        return waterCapacity;
    }

    public void setWaterCapacity(float waterCapacity) {
        this.waterCapacity = waterCapacity;
    }
}
