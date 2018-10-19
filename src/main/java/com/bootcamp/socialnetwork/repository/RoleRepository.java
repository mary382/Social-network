package com.bootcamp.socialnetwork.repository;

import com.bootcamp.socialnetwork.domain.AbstractRepository;
import com.bootcamp.socialnetwork.domain.Role;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Role entity.
 */
@Repository
public interface RoleRepository extends AbstractRepository<Role> {

}
