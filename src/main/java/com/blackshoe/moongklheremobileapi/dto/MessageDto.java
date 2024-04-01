package com.blackshoe.moongklheremobileapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

import java.util.Map;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageDto {
    private String from;
    private String topic;
    private Map<String, String> message;
}

