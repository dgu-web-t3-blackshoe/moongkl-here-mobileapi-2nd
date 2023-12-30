package com.blackshoe.moongklheremobileapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto<T> {
    private T payload;
    private String error;

    @Builder(builderMethodName = "success")
    public ResponseDto(T payload) {
        this.payload = payload;
    }

    @Builder(builderMethodName = "error")
    public ResponseDto(String error) {
        this.error = error;
    }

    @Builder
    public ResponseDto(T payload, String error) {
        this.payload = payload;
        this.error = error;
    }
}
