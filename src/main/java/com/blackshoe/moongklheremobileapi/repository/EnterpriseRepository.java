package com.blackshoe.moongklheremobileapi.repository;

import com.blackshoe.moongklheremobileapi.entity.Enterprise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EnterpriseRepository extends JpaRepository<Enterprise, UUID> {

    @Query("SELECT DISTINCT e FROM Enterprise e JOIN e.storyUrls s WHERE e.name LIKE %:enterpriseName% ORDER BY s.createdAt DESC")
    List<Enterprise> findByNameContainingOrderByStoryCreatedAtDesc(String enterpriseName);
}
