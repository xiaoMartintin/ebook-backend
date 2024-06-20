package com.klx.ebookbackend.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @PostMapping("/{commentId}")
    public Map<String, Object> replyComment(@PathVariable String commentId, @RequestBody Map<String, String> request) {
        return Collections.singletonMap("status", "success");
    }

    @PutMapping("/{commentId}/like")
    public Map<String, Object> likeComment(@PathVariable String commentId) {
        return Collections.singletonMap("status", "success");
    }

    @PutMapping("/{commentId}/unlike")
    public Map<String, Object> unlikeComment(@PathVariable String commentId) {
        return Collections.singletonMap("status", "success");
    }
}
