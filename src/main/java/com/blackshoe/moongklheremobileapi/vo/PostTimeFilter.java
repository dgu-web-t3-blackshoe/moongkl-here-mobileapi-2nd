package com.blackshoe.moongklheremobileapi.vo;

import lombok.*;

@Data
@Builder
public class PostTimeFilter {
    private Integer fromYear;
    private Integer fromMonth;
    private Integer fromDay;
    private Integer toYear;
    private Integer toMonth;
    private Integer toDay;

    public static PostTimeFilter convertStringToPostTimeFilter(String from, String to) {
        PostTimeFilter postTimeFilter = PostTimeFilter.builder()
                .fromYear(Integer.parseInt(from.substring(0, 4)))
                .fromMonth(Integer.parseInt(from.substring(5, 7)))
                .fromDay(Integer.parseInt(from.substring(8, 10)))
                .toYear(Integer.parseInt(to.substring(0, 4)))
                .toMonth(Integer.parseInt(to.substring(5, 7)))
                .toDay(Integer.parseInt(to.substring(8, 10)))
                .build();

        return postTimeFilter;
    }
}
