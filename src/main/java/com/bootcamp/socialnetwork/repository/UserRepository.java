package com.bootcamp.socialnetwork.repository;

import com.bootcamp.socialnetwork.domain.AbstractRepository;
import com.bootcamp.socialnetwork.domain.User;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the User entity.
 */
@Repository
public interface UserRepository extends AbstractRepository<User> {

    User findByEmail(String email);
}
