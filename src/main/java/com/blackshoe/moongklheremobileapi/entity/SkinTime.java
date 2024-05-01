package com.blackshoe.moongklheremobileapi.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "skin_times")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class SkinTime {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "skin_year", length = 4, nullable = false)
    private int year;

    @Column(name = "skin_month", length = 2, nullable = false)
    private int month;

    @Column(name = "skin_day", length = 2, nullable = false)
    private int day;

    @Column(name = "skin_hour", length = 2, nullable = false)
    private int hour;

    @Column(name = "skin_minute", length = 2, nullable = false)
    private int minute;

    @PrePersist
    public void prePersist() {
        if (this.id == null)
            this.id = UUID.randomUUID();
    }

    @Builder
    public SkinTime(UUID id, int year, int month, int day, int hour, int minute) {
        this.id = id;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }
}
