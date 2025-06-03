package com.hiringassignment.hiringassignment.controller;

import com.hiringassignment.hiringassignment.entity.RedditTopPost;
import com.hiringassignment.hiringassignment.service.MemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HelloWorldController {

    private final MemeService memeService;

    @GetMapping(value = "/getLatestCrawledData")
    public ResponseEntity<List<RedditTopPost>> hello() {
        return ResponseEntity.ok(memeService.getCrawledPost());
    }
}
