package org.anyone.backend.service;

import org.anyone.backend.model.Posts;
import org.anyone.backend.model.Users;
import org.anyone.backend.repository.PostsRepository;
import org.anyone.backend.util.StringUtil;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class PostsService {
    private final PostsRepository postsRepository;

    public PostsService(PostsRepository postsRepository) {
        this.postsRepository = postsRepository;
    }

    public Iterable<Posts> getPosts(Users user) {
        return postsRepository.findAllByUserOrderByPostDateTimeDesc(user);
    }

    public Iterable<Posts> getPosts() {
        return postsRepository.findAll(Sort.by("PostDateTime").descending());
    }

    public Posts getPosts(int postID) {
        Optional<Posts> optionalPosts = postsRepository.findByPostID(postID);
        return optionalPosts.orElse(null);
    }

    public ArrayList<Posts> getPostList(Users user) {
        return convertToList(getPosts(user));
    }

    public ArrayList<Posts> getPostList() {
        return convertToList(getPosts());
    }

    public Posts addPost(Users user, String content) {
        Posts post = new Posts();
        post.setUser(user);
        post.setPostContent(content);
        String ats = StringUtil.matchedArrayToString(StringUtil.matchAts(content));
        String tags = StringUtil.matchedArrayToString(StringUtil.matchTags(content));
        post.setAts(ats);
        post.setTags(tags);
        post.setPostDateTime(LocalDateTime.now());
        return postsRepository.saveAndFlush(post);
    }

    public Posts deletePost(int postID) {
        Posts post = getPosts(postID);
        if (post == null) return null;
        postsRepository.delete(post);
        return post;
    }

    public boolean belongTo(int postID, Users user) throws NullPointerException {
        Posts post = getPosts(postID);
        if (post == null) throw new NullPointerException();
        return post.getUser() == user;
    }

    private ArrayList<Posts> convertToList(Iterable<Posts> postsIterable) {
        ArrayList<Posts> posts = new ArrayList<>();
        postsIterable.forEach(posts::add);
        return posts;
    }
}
