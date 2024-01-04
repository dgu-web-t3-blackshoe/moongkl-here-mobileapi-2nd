package com.blackshoe.moongklheremobileapi.vo;

import com.blackshoe.moongklheremobileapi.exception.PostErrorResult;
import com.blackshoe.moongklheremobileapi.exception.PostException;
import lombok.*;

import java.util.regex.Pattern;

@Data
@Builder
public class PostTimeFilter {
    private Integer fromYear;
    private Integer fromMonth;
    private Integer fromDay;
    private Integer toYear;
    private Integer toMonth;
    private Integer toDay;

    public static PostTimeFilter verifyAndConvertStringToPostTimeFilter(String from, String to) {

        final String regex = "^[0-9]{4}-[0-9]{2}-[0-9]{2}$";

        if (Pattern.matches(regex, from) == false || Pattern.matches(regex, to) == false) {
            throw new PostException(PostErrorResult.INVALID_DATE_FORMAT);
        }

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
