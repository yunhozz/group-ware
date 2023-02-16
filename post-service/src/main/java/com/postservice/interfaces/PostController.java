package com.postservice.interfaces;

import com.postservice.application.PostService;
import com.postservice.common.annotation.HeaderToken;
import com.postservice.common.enums.PostType;
import com.postservice.common.enums.Role;
import com.postservice.common.util.TokenParser;
import com.postservice.dto.query.PostDetailsQueryDto;
import com.postservice.dto.query.PostSimpleQueryDto;
import com.postservice.dto.request.PostRequestDto;
import com.postservice.dto.request.PostUpdateRequestDto;
import com.postservice.dto.response.CommentResponseDto;
import com.postservice.dto.response.UserSimpleResponseDto;
import com.postservice.persistence.repository.PostRepository;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostRepository postRepository;
    private final TokenParser tokenParser;
    private final RestTemplate restTemplate;

    @GetMapping("/{id}")
    public ResponseEntity<PostDetailsQueryDto> getPostInfo(@HeaderToken(role = {Role.ADMIN, Role.USER}) String token, @PathVariable Long id) {
        PostDetailsQueryDto postDetailsQueryDto = postService.findPostDetailsById(id);
        String postWriterId = postDetailsQueryDto.getWriterId();
        List<CommentResponseDto> commentDtoList = postDetailsQueryDto.getComments();

        URI uriForPostUserInfo = UriComponentsBuilder.fromUriString("http://localhost:8000/api/auth/users/{writerId}/simple")
                .build()
                .expand(postWriterId)
                .encode().toUri();

        ResponseEntity<UserSimpleResponseDto> userSimpleDtoOfPost = restTemplate.getForEntity(uriForPostUserInfo, UserSimpleResponseDto.class);
        postDetailsQueryDto.setUserInfo(userSimpleDtoOfPost.getBody());

        if (!commentDtoList.isEmpty()) {
            List<String> commentWriterIds = new ArrayList<>() {{
                for (CommentResponseDto commentResponseDto : commentDtoList) {
                    add(commentResponseDto.getWriterId());
                }
            }};

            ResponseEntity<List<UserSimpleResponseDto>> userSimpleDtoOfComments = getResponseOfUserSimpleDtoList(commentWriterIds);
            int idx = 0;

            for (CommentResponseDto commentResponseDto : commentDtoList) {
                UserSimpleResponseDto userInfo = userSimpleDtoOfComments.getBody().get(idx++);
                commentResponseDto.setUserInfo(userInfo);
            }

            postDetailsQueryDto.setComments(commentDtoList);
        }

        return ResponseEntity.ok(postDetailsQueryDto);
    }

    @GetMapping("/simple")
    public ResponseEntity<List<PostSimpleQueryDto>> getSimpleListByType(@RequestParam(required = false) PostType postType) {
        List<PostSimpleQueryDto> postSimpleDtoList = postRepository.getPostSimpleListByType(postType);
        if (!postSimpleDtoList.isEmpty()) {
            List<String> writerIds = new ArrayList<>() {{
                for (PostSimpleQueryDto postSimpleQueryDto : postSimpleDtoList) {
                    add(postSimpleQueryDto.getUserId());
                }
            }};

            ResponseEntity<List<UserSimpleResponseDto>> userSimpleDtoList = getResponseOfUserSimpleDtoList(writerIds);
            int idx = 0;

            for (PostSimpleQueryDto postSimpleQueryDto : postSimpleDtoList) {
                UserSimpleResponseDto userInfo = userSimpleDtoList.getBody().get(idx++);
                postSimpleQueryDto.setUserInfo(userInfo);
            }
        }

        return ResponseEntity.ok(postSimpleDtoList);
    }

    @PostMapping
    public ResponseEntity<Slice<PostSimpleQueryDto>> getSimplePostSliceByType(@RequestParam PostType postType, @RequestParam(required = false) Long cursorId, Pageable pageable) {
        Slice<PostSimpleQueryDto> postSimpleDtoSlice = postRepository.getPostSimpleSliceByType(postType, cursorId, pageable);
        if (!postSimpleDtoSlice.isEmpty()) {
            List<String> writerIds = new ArrayList<>() {{
                for (PostSimpleQueryDto postSimpleQueryDto : postSimpleDtoSlice) {
                    add(postSimpleQueryDto.getUserId());
                }
            }};

            ResponseEntity<List<UserSimpleResponseDto>> userSimpleDtoList = getResponseOfUserSimpleDtoList(writerIds);
            int idx = 0;

            for (PostSimpleQueryDto postSimpleQueryDto : postSimpleDtoSlice) {
                UserSimpleResponseDto userInfo = userSimpleDtoList.getBody().get(idx++);
                postSimpleQueryDto.setUserInfo(userInfo);
            }
        }

        return ResponseEntity.ok(postSimpleDtoSlice);
    }

    @PostMapping(value = "/create", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Long> createPost(@HeaderToken(role = {Role.ADMIN, Role.USER}) String token,
                                           @RequestParam(required = false) Long teamId,
                                           @Valid @RequestBody PostRequestDto postRequestDto,
                                           @RequestPart(name = "files", required = false) List<MultipartFile> files) {
        Claims claims = tokenParser.execute(token);
        Long postId = postService.createPost(claims.getSubject(), teamId, postRequestDto, files);
        return ResponseEntity.ok(postId);
    }

    @PatchMapping(value = "/{id}/update", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Long> updatePost(@HeaderToken(role = {Role.ADMIN, Role.USER}) String token,
                                           @PathVariable Long id,
                                           @Valid @RequestBody PostUpdateRequestDto postUpdateRequestDto,
                                           @RequestPart(name = "files", required = false) List<MultipartFile> files) {
        Claims claims = tokenParser.execute(token);
        Long postId = postService.updateInfo(id, claims.getSubject(), postUpdateRequestDto, files);
        return new ResponseEntity<>(postId, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/delete")
    public ResponseEntity<String> deletePost(@HeaderToken(role = {Role.ADMIN, Role.USER}) String token, @PathVariable Long id) {
        Claims claims = tokenParser.execute(token);
        postService.deletePost(id, claims.getSubject());
        return new ResponseEntity<>("삭제가 완료되었습니다.", HttpStatus.NO_CONTENT);
    }

    private ResponseEntity<List<UserSimpleResponseDto>> getResponseOfUserSimpleDtoList(List<String> userIds) {
        URI uri = UriComponentsBuilder.fromUriString("http://localhost:8000/api/auth/users/simple")
                .queryParam("userIds", userIds)
                .build().toUri();
        return restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
    }
}