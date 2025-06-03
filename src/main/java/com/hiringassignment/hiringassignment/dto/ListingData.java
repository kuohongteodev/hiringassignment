package com.hiringassignment.hiringassignment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListingData {
    private String after;
    private int dist;
    private String modhash;
    private String geoFilter;
    private List<Child> children;
    private String before;
}
