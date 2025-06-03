package com.hiringassignment.hiringassignment.dao;

import com.hiringassignment.hiringassignment.entity.Crawls;
import com.hiringassignment.hiringassignment.entity.RedditTopPost;
import com.hiringassignment.hiringassignment.entity.RedditTopPostID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RedditTopPostRepository extends JpaRepository<RedditTopPost, RedditTopPostID> {
    // Query methods if needed
    List<RedditTopPost> findByCrawl(Crawls crawl);
}