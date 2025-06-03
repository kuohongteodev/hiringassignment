package com.hiringassignment.hiringassignment.dao;

import com.hiringassignment.hiringassignment.entity.Crawls;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrawlsRepository extends JpaRepository<Crawls, Long> {
}