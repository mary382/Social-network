package com.bootcamp.socialnetwork.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Abstract repository.
 */
public interface AbstractRepository<T extends AbstractEntity> extends JpaRepository<T, Long> {

}
