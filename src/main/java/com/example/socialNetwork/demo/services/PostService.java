package com.example.socialNetwork.demo.services;

import com.example.socialNetwork.demo.dto.PostDTO;
import com.example.socialNetwork.demo.entity.ImageModel;
import com.example.socialNetwork.demo.entity.Post;
import com.example.socialNetwork.demo.entity.UserA;
import com.example.socialNetwork.demo.exceptions.PostNotFoundException;
import com.example.socialNetwork.demo.repository.ImageRepository;
import com.example.socialNetwork.demo.repository.PostRepository;
import com.example.socialNetwork.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    public static final Logger LOG = LoggerFactory.getLogger(PostService.class);

    // Principal нужен для того, чтобы авторизованный юзер мог удалить/изменить только свои данные

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository, ImageRepository imageRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
    }

    public Post createPost(PostDTO postDTO, Principal principal) {
        UserA user = getUserByPrincipal(principal);
        Post post = new Post();
        post.setUserA(user);
        post.setCaption(postDTO.getCaption());
        post.setLocation(postDTO.getLocation());
        post.setTitle(postDTO.getTitle());
        post.setLikes(0);

        LOG.info("Saving Post for User: {}", user.getEmail());
        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedDatedDesc();
    }

    public Post getPostById(Long postId, Principal principal) {
        UserA user = getUserByPrincipal(principal);
        return postRepository.findPostByIdAndUserA(postId, user)
                .orElseThrow(() -> new PostNotFoundException("Post cannot be found for username: " + user.getEmail()));
    }

    public List<Post> getAllPostForUser(Principal principal) {
        UserA user = getUserByPrincipal(principal);
        return postRepository.findAllByUserAOrderByCreatedDatedDesc(user);
    }

    public Post likePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post cannot be found"));

        Optional<String> userLiked = post.getLikedUsers()
                .stream()
                .filter(u -> u.equals(username)).findAny();

        if (userLiked.isPresent()) {
            post.setLikes(post.getLikes() - 1);
            post.getLikedUsers().remove(username);
        } else {
            post.setLikes(post.getLikes() + 1);
            post.getLikedUsers().add(username);
        }
        return postRepository.save(post);
    }

    public void deletePost(Long postId, Principal principal) {
        Post post = getPostById(postId, principal);
        Optional<ImageModel> imageModel = imageRepository.findByPostId(post.getId());
        postRepository.delete(post);
        imageModel.ifPresent(imageRepository::delete);
    }

    private UserA getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findUserAByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found with username " + username));

    }
}
