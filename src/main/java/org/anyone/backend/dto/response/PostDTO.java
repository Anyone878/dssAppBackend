package org.anyone.backend.dto.response;

import org.anyone.backend.model.Posts;

public class PostDTO {
    Posts post;
    boolean isLikedByCurrentUser;

    public PostDTO() {
        this.isLikedByCurrentUser = false;
    }

    public PostDTO(Posts post) {
        this.post = post;
        this.isLikedByCurrentUser = false;
    }

    public PostDTO(Posts post, boolean isLikedByCurrentUser) {
        this.post = post;
        this.isLikedByCurrentUser = isLikedByCurrentUser;
    }

    @Override
    public String toString() {
        return "PostDTO{" +
                "post=" + post +
                ", isLikedByCurrentUser=" + isLikedByCurrentUser +
                '}';
    }

    public void setPost(Posts post) {
        this.post = post;
    }

    public void setLikedByCurrentUser(boolean likedByCurrentUser) {
        isLikedByCurrentUser = likedByCurrentUser;
    }

    public Posts getPost() {
        return post;
    }

    public boolean isLikedByCurrentUser() {
        return isLikedByCurrentUser;
    }
}
