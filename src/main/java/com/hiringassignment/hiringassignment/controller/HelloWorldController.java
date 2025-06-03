package com.hiringassignment.hiringassignment.controller;
import com.hiringassignment.hiringassignment.dto.PostDTO;
import com.hiringassignment.hiringassignment.service.MemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HelloWorldController {

    private final MemeService memeService;

    @GetMapping(value = "/hello")
    public ResponseEntity<PostDTO> hello() {
        return ResponseEntity.ok(memeService.getMeme());
    }
}
