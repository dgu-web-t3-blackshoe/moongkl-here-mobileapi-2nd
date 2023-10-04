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
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(length = 4, nullable = false)
    private int year;

    @Column(length = 2, nullable = false)
    private int month;

    @Column(length = 2, nullable = false)
    private int day;

    @Column(length = 2, nullable = false)
    private int hour;

    @Column(length = 2, nullable = false)
    private int minute;

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
