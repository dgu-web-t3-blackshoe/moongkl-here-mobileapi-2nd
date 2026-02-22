package com.blackshoe.moongklheremobileapi.entity;

import javax.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "terms")
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Terms {

    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID id;

    @Column(name = "terms", columnDefinition = "TEXT")
    private String terms;

    @LastModifiedDate
    @Column(name = "updated_at", length = 20)
    private LocalDateTime updatedAt;

    @Builder
    public Terms(UUID id, String terms) {
        this.id = id;
        this.terms = terms;
    }

    public void updateTerms(String terms) {
        this.terms = terms;
    }
}
