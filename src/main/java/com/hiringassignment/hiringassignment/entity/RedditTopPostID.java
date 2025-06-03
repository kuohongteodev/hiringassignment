package com.hiringassignment.hiringassignment.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import jakarta.persistence.Column;
import lombok.Data;

@Embeddable
@Data
public class RedditTopPostID implements Serializable {

    @Column(length = 20)
    private String id;

    @Column(name = "crawl_id")
    private Long crawlId;
}