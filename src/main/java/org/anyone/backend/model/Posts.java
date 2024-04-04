package org.anyone.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

@Entity
public class Posts {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "PostID")
    int postID;

    @ManyToOne
    @JoinColumn(name = "UserID", referencedColumnName = "UserID")
    @JsonIgnore
    Users user;

    @Column(name = "PostContent")
    @Lob
    String postContent;

    @Column(name = "PostDateTime")
    LocalDateTime postDateTime;

    @Column(name = "TmpATs")
    @JsonSerialize(using = AtAndTagSerializer.class)
    String ats;

    @Column(name = "TmpTags")
    @JsonSerialize(using = AtAndTagSerializer.class)
    String tags;

    @Column(name = "Likes")
    int likes;

    @Column(name = "Comments")
    int comments;

    static class AtAndTagSerializer extends JsonSerializer<String> {
        @Override
        public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            ArrayList<String> list = new ArrayList<>(Arrays.asList(value.split(",")));
            serializers.defaultSerializeValue(list, gen);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Posts posts = (Posts) o;
        return postID == posts.postID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(postID);
    }

    @Override
    public String toString() {
        return "Posts{" +
                "postID=" + postID +
                ", user=" + user +
                ", postContent='" + postContent + '\'' +
                ", postDateTime=" + postDateTime +
                ", ats='" + ats + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getPostID() {
        return postID;
    }

    public void setPostID(int postID) {
        this.postID = postID;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public LocalDateTime getPostDateTime() {
        return postDateTime;
    }

    public void setPostDateTime(LocalDateTime postDateTime) {
        this.postDateTime = postDateTime;
    }

    public String getAts() {
        return ats;
    }

    public void setAts(String ats) {
        this.ats = ats;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
