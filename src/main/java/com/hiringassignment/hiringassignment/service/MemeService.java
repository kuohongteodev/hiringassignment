package com.hiringassignment.hiringassignment.service;

import com.hiringassignment.hiringassignment.entity.RedditTopPost;

import java.util.List;

public interface MemeService {
    void getMeme();

    List<RedditTopPost> getCrawledPost();
}
