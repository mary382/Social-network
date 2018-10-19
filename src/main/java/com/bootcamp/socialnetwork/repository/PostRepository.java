package com.bootcamp.socialnetwork.repository;

import com.bootcamp.socialnetwork.domain.AbstractRepository;
import com.bootcamp.socialnetwork.domain.Post;
import com.bootcamp.socialnetwork.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Post entity.
 */
@Repository
public interface PostRepository extends AbstractRepository<Post> {

    Post findById(Long id);

    Post findByIdAndAuthor(Long postId, User author);

    List<Post> findAllByAuthor(User author);

    List<Post> findAllByOwnerId(Long id);

    Page<Post> findAllByOwnerId(Long id, Pageable pageable);

    void deleteAllByOwnerId(Long ownerId);
}
