package com.hiringassignment.hiringassignment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostData {

    private String id;
    private String title;
    private String author;
    private String subreddit;
    private int ups;
    private int downs;
    private int score;
    private String url;
    private String permalink;

    @JsonProperty("created_utc")
    private long createdUtc;

    private String thumbnail;
}
