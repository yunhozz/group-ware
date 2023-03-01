package com.postservice.interfaces;

import com.postservice.application.CommentService;
import com.postservice.common.util.RedisUtils;
import com.postservice.dto.request.CommentRequestDto;
import com.postservice.dto.response.UserSimpleResponseDto;
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

import static com.postservice.common.util.RedisUtils.MY_INFO_KEY;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final RedisUtils redisUtils;

    @PostMapping
    public ResponseEntity<Long> createParent(@RequestParam Long postId, @Valid @RequestBody CommentRequestDto commentRequestDto) {
        UserSimpleResponseDto myInfo = getMyInfoFromRedis();
        Long commentId = commentService.makeParent(myInfo.getUserId(), postId, commentRequestDto.getContent());
        return new ResponseEntity<>(commentId, HttpStatus.CREATED);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Long> createChild(@PathVariable("id") Long parentId, @RequestParam Long postId,
                                            @Valid @RequestBody CommentRequestDto commentRequestDto) {
        UserSimpleResponseDto myInfo = getMyInfoFromRedis();
        Long commentId = commentService.makeChild(myInfo.getUserId(), postId, parentId, commentRequestDto.getContent());
        return new ResponseEntity<>(commentId, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateComment(@PathVariable Long id, @Valid @RequestBody CommentRequestDto commentRequestDto) {
        UserSimpleResponseDto myInfo = getMyInfoFromRedis();
        commentService.updateContent(id, myInfo.getUserId(), commentRequestDto.getContent());
        return new ResponseEntity<>("수정이 완료되었습니다.", HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/delete")
    public ResponseEntity<String> deleteComment(@PathVariable Long id) {
        UserSimpleResponseDto myInfo = getMyInfoFromRedis();
        commentService.deleteComment(myInfo.getUserId(), id);
        return new ResponseEntity<>("삭제가 완료되었습니다.", HttpStatus.CREATED);
    }

    private UserSimpleResponseDto getMyInfoFromRedis() {
        try {
            return redisUtils.getData(MY_INFO_KEY, UserSimpleResponseDto.class);
        } catch (Exception e) {
            throw new IllegalStateException(e.getLocalizedMessage());
        }
    }
}