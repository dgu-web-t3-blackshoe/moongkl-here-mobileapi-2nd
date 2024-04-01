package com.blackshoe.moongklheremobileapi.repository;

import com.blackshoe.moongklheremobileapi.dto.NotificationDto;
import com.blackshoe.moongklheremobileapi.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    @Query("SELECT new com.blackshoe.moongklheremobileapi.dto.NotificationDto$NotificationReadResponse " +
            "(n.id, n.title, n.content, n.createdAt, n.updatedAt) " +
            "FROM Notification n ORDER BY n.createdAt DESC")
    Page<NotificationDto.NotificationReadResponse> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
