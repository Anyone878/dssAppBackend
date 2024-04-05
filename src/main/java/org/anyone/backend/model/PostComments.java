package org.anyone.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class PostComments {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "CommentID")
    int commentID;

    @ManyToOne
    @JoinColumn(name = "UserID", referencedColumnName = "UserID")
    @JsonIgnore
    Users user;

    @ManyToOne
    @JoinColumn(name = "PostID", referencedColumnName = "PostID")
    @JsonIgnore
    Posts post;

    @Column(name = "CommentContent")
    String commentContent;

    @Column(name = "CommentDateTime")
    LocalDateTime commentDateTime;

    @Column(name = "Likes")
    int likes;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostComments that = (PostComments) o;
        return commentID == that.commentID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentID);
    }

    @Override
    public String toString() {
        return "PostComments{" +
                "commentID=" + commentID +
                ", user=" + user +
                ", post=" + post +
                ", commentContent='" + commentContent + '\'' +
                ", commentDateTime=" + commentDateTime +
                ", likes=" + likes +
                '}';
    }

    public int getCommentID() {
        return commentID;
    }

    public void setCommentID(int commentID) {
        this.commentID = commentID;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Posts getPost() {
        return post;
    }

    public void setPost(Posts post) {
        this.post = post;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public LocalDateTime getCommentDateTime() {
        return commentDateTime;
    }

    public void setCommentDateTime(LocalDateTime commentDateTime) {
        this.commentDateTime = commentDateTime;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
