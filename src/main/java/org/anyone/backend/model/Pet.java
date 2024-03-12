package org.anyone.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Pet {
    @Id
    @Column(name = "PetID")
    @GeneratedValue(strategy = GenerationType.TABLE)
    int petID;

    @OneToOne
    @JoinColumn(name = "UserID", referencedColumnName = "UserID")
    @JsonIgnore
    Users user;

    @OneToOne
    @JoinColumn(name = "FeederID", referencedColumnName = "FeederID")
    @JsonIgnore
    Feeder feeder;

    @Column(name = "PetName")
    String petName;

    @Column(name = "Gender")
    String gender;

    @Column(name = "AvatarURL")
    String avatarURL;

    public Pet() {
    }

    public Pet(int petID, Users user, Feeder feeder, String petName, String gender, String avatarURL) {
        this.petID = petID;
        this.user = user;
        this.feeder = feeder;
        this.petName = petName;
        this.gender = gender;
        this.avatarURL = avatarURL;
    }

    @Override
    public String toString() {
        return "Pet{" +
                "petID=" + petID +
                ", user=" + user +
                ", feeder=" + feeder +
                ", petName='" + petName + '\'' +
                ", gender='" + gender + '\'' +
                ", avatarURL='" + avatarURL + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return petID == pet.petID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(petID);
    }

    public int getPetID() {
        return petID;
    }

    public void setPetID(int petID) {
        this.petID = petID;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Feeder getFeeder() {
        return feeder;
    }

    public void setFeeder(Feeder feeder) {
        this.feeder = feeder;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }
}
