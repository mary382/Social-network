package com.bootcamp.socialnetwork.repository;

import com.bootcamp.socialnetwork.domain.AbstractRepository;
import com.bootcamp.socialnetwork.domain.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Community entity.
 */
@Repository
public interface CommunityRepository extends AbstractRepository<Community> {
    List<Community> findAllByParticipantsId(Long memberId);
    Community findByIdAndParticipantsId(Long id, Long userId);
    Page<Community> findAllByParticipantsId(Long memberId, Pageable pageable);
}
