package org.anyone.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Likes {
    public Likes() {

    }

    public enum Type {
        POST, COMMENT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "LikeID")
    int likeID;

    @ManyToOne
    @JoinColumn(name = "UserID", referencedColumnName = "UserID")
    @JsonIgnore
    Users user;

    @Column(name = "fid")
    int fid;

    @Enumerated(EnumType.STRING)
    @Column(name = "Type")
    Type type;

    @Column(name = "LikeDateTime")
    LocalDateTime likeDateTime;

    public Likes(int likeID, Users user, int fID, Type type, LocalDateTime likeDateTime) {
        this.likeID = likeID;
        this.user = user;
        this.fid = fID;
        this.type = type;
        this.likeDateTime = likeDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Likes likes = (Likes) o;
        return likeID == likes.likeID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(likeID);
    }

    @Override
    public String toString() {
        return "Likes{" +
                "likeID=" + likeID +
                ", user=" + user +
                ", fID=" + fid +
                ", type=" + type +
                ", likeDateTime=" + likeDateTime +
                '}';
    }

    public int getLikeID() {
        return likeID;
    }

    public void setLikeID(int likeID) {
        this.likeID = likeID;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fID) {
        this.fid = fID;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public LocalDateTime getLikeDateTime() {
        return likeDateTime;
    }

    public void setLikeDateTime(LocalDateTime likeDateTime) {
        this.likeDateTime = likeDateTime;
    }
}
