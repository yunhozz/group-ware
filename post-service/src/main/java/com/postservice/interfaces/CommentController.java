package com.postservice.interfaces;

import com.postservice.application.CommentService;
import com.postservice.common.annotation.HeaderToken;
import com.postservice.common.util.TokenParser;
import com.postservice.dto.request.CommentRequestDto;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final TokenParser tokenParser;

    @PostMapping("/create")
    public ResponseEntity<Long> createParent(@HeaderToken String token, @RequestParam Long postId, @Valid @RequestBody CommentRequestDto commentRequestDto) {
        Claims claims = tokenParser.execute(token);
        Long commentId = commentService.makeParent(claims.getSubject(), postId, commentRequestDto.getContent());
        return new ResponseEntity<>(commentId, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/create")
    public ResponseEntity<Long> createChild(@HeaderToken String token, @PathVariable("id") Long parentId, @RequestParam Long postId,
                                            @Valid @RequestBody CommentRequestDto commentRequestDto) {
        Claims claims = tokenParser.execute(token);
        Long commentId = commentService.makeChild(claims.getSubject(), postId, parentId, commentRequestDto.getContent());
        return new ResponseEntity<>(commentId, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/update")
    public ResponseEntity<String> updateComment(@HeaderToken String token, @PathVariable Long id, @Valid @RequestBody CommentRequestDto commentRequestDto) {
        Claims claims = tokenParser.execute(token);
        commentService.updateContent(id, claims.getSubject(), commentRequestDto.getContent());
        return new ResponseEntity<>("수정이 완료되었습니다.", HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/delete")
    public ResponseEntity<String> deleteComment(@HeaderToken String token, @PathVariable Long id) {
        Claims claims = tokenParser.execute(token);
        commentService.deleteComment(claims.getSubject(), id);
        return new ResponseEntity<>("삭제가 완료되었습니다.", HttpStatus.CREATED);
    }
}