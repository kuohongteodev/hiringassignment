package com.hiringassignment.hiringassignment.service.impl;

import com.hiringassignment.hiringassignment.dto.PostDTO;
import com.hiringassignment.hiringassignment.service.MemeService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class MemeServiceImpl implements MemeService {

    @Override
    public PostDTO getMeme() {
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://www.reddit.com/r/memes/top.json?t=day&limit=20";

        try {
            ResponseEntity<PostDTO> response = restTemplate.getForEntity(url, PostDTO.class);
            return response.getBody();
        } catch (RestClientException e) {
            throw new RuntimeException(e);
        }
    }
}
