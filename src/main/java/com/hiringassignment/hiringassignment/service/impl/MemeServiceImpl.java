package com.hiringassignment.hiringassignment.service.impl;

import com.hiringassignment.hiringassignment.dao.CrawlsRepository;
import com.hiringassignment.hiringassignment.dao.RedditTopPostRepository;
import com.hiringassignment.hiringassignment.dto.PostDTO;
import com.hiringassignment.hiringassignment.dto.PostData;
import com.hiringassignment.hiringassignment.entity.Crawls;
import com.hiringassignment.hiringassignment.entity.RedditTopPost;
import com.hiringassignment.hiringassignment.entity.RedditTopPostID;
import com.hiringassignment.hiringassignment.service.MemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemeServiceImpl implements MemeService {

    private final CrawlsRepository crawlsRepository;
    private final RedditTopPostRepository redditTopPostRepository;

    @Override
    @Scheduled(fixedRate = 180 * 60 * 1000)
    public void getMeme() {
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://www.reddit.com/r/memes/top.json?t=day&limit=20";

        try {
            ResponseEntity<PostDTO> response = restTemplate.getForEntity(url, PostDTO.class);

            if (response.hasBody()) {
                PostDTO postDTO = response.getBody();

                Crawls crawl = new Crawls();
                crawl.setCrawlTime(LocalDateTime.now());
                crawl = crawlsRepository.save(crawl);

                Crawls finalCrawl = crawl;
                List<RedditTopPost> postsToSave = postDTO.getData().getChildren().stream()
                        .map(child -> {
                            PostData postData = child.getData();

                            RedditTopPost post = new RedditTopPost();

                            RedditTopPostID postId = new RedditTopPostID();
                            postId.setId(postData.getId());
                            postId.setCrawlId(finalCrawl.getCrawlId());
                            post.setId(postId);
                            post.setCrawl(finalCrawl);

                            post.setTitle(postData.getTitle());
                            post.setSubreddit(postData.getSubreddit());
                            post.setAuthor(postData.getAuthor());
                            post.setScore(postData.getScore());
                            post.setUrl(postData.getUrl());
                            post.setPermalink(postData.getPermalink());
                            post.setNumComments(postData.getNumComments());

                            post.setCreatedAt(Instant.ofEpochSecond(postData.getCreatedUtc())
                                    .atZone(ZoneId.systemDefault()).toLocalDateTime());

                            return post;
                        })
                        .toList();

                redditTopPostRepository.saveAll(postsToSave);

            }
        } catch (RestClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<RedditTopPost> getCrawledPost() {
        Crawls latestCrawl = crawlsRepository.findFirstByOrderByCrawlIdDesc();
        if (latestCrawl == null) {
            return Collections.emptyList();
        }
        return redditTopPostRepository.findByCrawl(latestCrawl);
    }
}
