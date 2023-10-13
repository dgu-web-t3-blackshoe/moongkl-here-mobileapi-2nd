package com.blackshoe.moongklheremobileapi.repository;

import com.blackshoe.moongklheremobileapi.entity.TemporaryPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TemporaryPostRepository extends JpaRepository<TemporaryPost, UUID> {
}
