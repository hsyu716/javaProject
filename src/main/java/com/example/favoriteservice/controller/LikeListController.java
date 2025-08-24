package com.example.favoriteservice.controller;

import com.example.favoriteservice.dto.LikeListRequest;
import com.example.favoriteservice.entity.LikeList;
import com.example.favoriteservice.service.LikeListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/like-list")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*"})
public class LikeListController {

    @Autowired
    private LikeListService likeListService;

    @GetMapping
    public ResponseEntity<List<LikeList>> getAllLikeLists() {
        List<LikeList> likeLists = likeListService.getAllLikeLists();
        return ResponseEntity.ok(likeLists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LikeList> getLikeListById(@PathVariable Long id) {
        LikeList likeList = likeListService.getLikeListById(id);
        if (likeList != null) {
            return ResponseEntity.ok(likeList);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LikeList>> getLikeListsByUserId(@PathVariable String userId) {
        List<LikeList> likeLists = likeListService.getLikeListsByUserId(userId);
        return ResponseEntity.ok(likeLists);
    }

    @PostMapping
    public ResponseEntity<LikeList> createLikeList(@RequestBody LikeListRequest request) {
        LikeList createdLikeList = likeListService.createLikeList(request);
        return ResponseEntity.ok(createdLikeList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LikeList> updateLikeList(@PathVariable Long id, @RequestBody LikeListRequest request) {
        LikeList updatedLikeList = likeListService.updateLikeList(id, request);
        if (updatedLikeList != null) {
            return ResponseEntity.ok(updatedLikeList);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLikeList(@PathVariable Long id) {
        likeListService.deleteLikeList(id);
        return ResponseEntity.ok().build();
    }
}
