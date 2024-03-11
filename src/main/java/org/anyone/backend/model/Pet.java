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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "UserID", referencedColumnName = "UserID")
    @JsonIgnore
    Users user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "FeederID", referencedColumnName = "FeederID")
    @JsonIgnore
    Feeder feeder;

    @Column(name = "PetName")
    String petName;

    @Column(name = "Gender")
    String gender;

    @Column(name = "AvatarURL")
    String avatarURL;

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
