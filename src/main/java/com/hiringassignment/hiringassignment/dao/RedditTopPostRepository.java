package com.hiringassignment.hiringassignment.dao;

import com.hiringassignment.hiringassignment.entity.RedditTopPost;
import com.hiringassignment.hiringassignment.entity.RedditTopPostID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedditTopPostRepository extends JpaRepository<RedditTopPost, RedditTopPostID> {
    // Query methods if needed
}