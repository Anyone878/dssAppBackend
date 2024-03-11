package org.anyone.backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Location {
    @Id
    @Column(name = "RecordID")
    @GeneratedValue(strategy = GenerationType.TABLE)
    int recordID;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PetID", referencedColumnName = "PetID")
    Pet pet;

    @Column(name = "Lng")
    String lng;

    @Column(name = "Lat")
    String Lat;

    @Column(name = "RecordDateTime")
    LocalDateTime recordDateTime;

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
