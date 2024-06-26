package org.anyone.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Location {
    @Id
    @Column(name = "RecordID")
    @GeneratedValue(strategy = GenerationType.TABLE)
    int recordID;

    @ManyToOne
    @JoinColumn(name = "PetID", referencedColumnName = "PetID")
    @JsonIgnore
    Pet pet;

    @Column(name = "Lng")
    String lng;

    @Column(name = "Lat")
    String Lat;

    @Column(name = "RecordDateTime")
    LocalDateTime recordDateTime;

    public Location() {
    }

    public Location(int recordID, Pet pet, String lng, String lat, LocalDateTime recordDateTime) {
        this.recordID = recordID;
        this.pet = pet;
        this.lng = lng;
        Lat = lat;
        this.recordDateTime = recordDateTime;
    }

    @Override
    public String toString() {
        return "Location{" +
                "recordID=" + recordID +
                ", pet=" + pet +
                ", lng='" + lng + '\'' +
                ", Lat='" + Lat + '\'' +
                ", recordDateTime=" + recordDateTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return recordID == location.recordID;
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

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public LocalDateTime getRecordDateTime() {
        return recordDateTime;
    }

    public void setRecordDateTime(LocalDateTime recordDateTime) {
        this.recordDateTime = recordDateTime;
    }
}
