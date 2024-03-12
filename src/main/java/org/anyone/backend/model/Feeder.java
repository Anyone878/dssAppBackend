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
