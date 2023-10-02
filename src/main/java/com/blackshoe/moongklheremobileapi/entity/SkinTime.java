package com.blackshoe.moongklheremobileapi.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "skin_times")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class SkinTime {
    @Id
    @GeneratedValue(generator = "uuid2")
    private UUID id;

    @Column(length = 4)
    private int year;

    @Column(length = 2)
    private int month;

    @Column(length = 2)
    private int day;

    @Column(length = 2)
    private int hour;

    @Column(length = 2)
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
