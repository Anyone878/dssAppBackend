package org.anyone.backend.dto.response;

import org.anyone.backend.model.PostComments;

public class CommentDTO {
    PostComments comment;
    boolean likedByCurrentUser;

    public CommentDTO() {
        this.likedByCurrentUser = false;
    }

    public CommentDTO(PostComments comment) {
        this.likedByCurrentUser = false;
    }

    public CommentDTO(PostComments comment, boolean likedByCurrentUser) {
        this.comment = comment;
        this.likedByCurrentUser = likedByCurrentUser;
    }

    public PostComments getComment() {
        return comment;
    }

    public void setComment(PostComments comment) {
        this.comment = comment;
    }

    public boolean isLikedByCurrentUser() {
        return likedByCurrentUser;
    }

    public void setLikedByCurrentUser(boolean likedByCurrentUser) {
        this.likedByCurrentUser = likedByCurrentUser;
    }
}
