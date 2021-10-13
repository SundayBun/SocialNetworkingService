package com.example.socialNetwork.demo.repository;

import com.example.socialNetwork.demo.entity.Post;
import com.example.socialNetwork.demo.entity.UserA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByUserAOrderByCreatedDatedDesc(UserA user);

    List<Post> findAllByOrderByCreatedDatedDesc();

    Optional<Post> findPostByIdAndUserA(Long id, UserA user);

}
