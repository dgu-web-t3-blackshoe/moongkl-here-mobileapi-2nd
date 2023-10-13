package com.blackshoe.moongklheremobileapi.repository;

import com.blackshoe.moongklheremobileapi.entity.SkinUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SkinUrlRepository extends JpaRepository<SkinUrl, UUID> {
}
