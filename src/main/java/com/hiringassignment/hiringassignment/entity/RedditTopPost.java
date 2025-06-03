package com.hiringassignment.hiringassignment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.JoinColumn;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "reddit_top_posts")
public class RedditTopPost {

    @EmbeddedId
    private RedditTopPostID id;

    @ManyToOne
    @MapsId("crawlId")
    @JoinColumn(name = "crawl_id")
    private Crawls crawl;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(nullable = false, length = 100)
    private String subreddit;

    @Column(length = 100)
    private String author;

    private Integer score;

    @Column(columnDefinition = "TEXT")
    private String url;

    @Column(columnDefinition = "TEXT")
    private String permalink;

    @Column(name = "num_comments")
    private Integer numComments;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}