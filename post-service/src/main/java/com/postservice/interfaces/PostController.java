package com.postservice.interfaces;

import com.postservice.application.PostService;
import com.postservice.common.enums.PostType;
import com.postservice.common.util.RedisUtils;
import com.postservice.dto.query.CommentQueryDto;
import com.postservice.dto.query.PostDetailsQueryDto;
import com.postservice.dto.query.PostSimpleQueryDto;
import com.postservice.dto.request.PostRequestDto;
import com.postservice.dto.request.PostUpdateRequestDto;
import com.postservice.dto.response.UserBasicResponseDto;
import com.postservice.dto.response.UserSimpleResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.postservice.common.util.RedisUtils.MY_INFO_KEY;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final RedisUtils redisUtils;

    @GetMapping
    public ResponseEntity<Slice<PostSimpleQueryDto>> getSimplePostSliceByType(@RequestParam PostType postType, @RequestParam Long teamId,
                                                                              @RequestParam(required = false) Long cursorId, Pageable pageable) {
        Slice<PostSimpleQueryDto> postSimpleDtoSlice = postService.findSimpleSliceDto(postType, teamId, cursorId, pageable);
        if (!postSimpleDtoSlice.isEmpty()) {
            List<String> writerIds = new ArrayList<>() {{
                for (PostSimpleQueryDto postSimpleQueryDto : postSimpleDtoSlice) {
                    add(postSimpleQueryDto.getWriterId());
                }
            }};

            ResponseEntity<Map<String, UserBasicResponseDto>> userData = getResponseOfUserBasicDtoList(writerIds);
            for (PostSimpleQueryDto postSimpleQueryDto : postSimpleDtoSlice) {
                UserBasicResponseDto userInfo = userData.getBody().get(postSimpleQueryDto.getWriterId());
                postSimpleQueryDto.setUserInfo(userInfo);
            }
        }

        return ResponseEntity.ok(postSimpleDtoSlice);
    }

    @GetMapping("/simple")
    public ResponseEntity<List<PostSimpleQueryDto>> getSimpleListByType(@RequestParam(required = false) PostType postType, @RequestParam Long teamId) {
        List<PostSimpleQueryDto> postSimpleDtoList = postService.findSimpleListDto(postType, teamId);
        if (!postSimpleDtoList.isEmpty()) {
            List<String> writerIds = new ArrayList<>() {{
                for (PostSimpleQueryDto postSimpleQueryDto : postSimpleDtoList) {
                    add(postSimpleQueryDto.getWriterId());
                }
            }};

            ResponseEntity<Map<String, UserBasicResponseDto>> userData = getResponseOfUserBasicDtoList(writerIds);
            for (PostSimpleQueryDto postSimpleQueryDto : postSimpleDtoList) {
                UserBasicResponseDto userInfo = userData.getBody().get(postSimpleQueryDto.getWriterId());
                postSimpleQueryDto.setUserInfo(userInfo);
            }
        }

        return ResponseEntity.ok(postSimpleDtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDetailsQueryDto> getPostInfo(@PathVariable Long id) {
        PostDetailsQueryDto postDetailsQueryDto = postService.findPostDetailsById(id);
        String postWriterId = postDetailsQueryDto.getWriterId();
        List<CommentQueryDto> commentDtoList = postDetailsQueryDto.getComments();

        RestTemplate restTemplate = new RestTemplate();
        URI uriForPostUserInfo = UriComponentsBuilder.fromUriString("http://localhost:8000/api/users/{writerId}/basic")
                .build()
                .expand(postWriterId)
                .encode().toUri();
        ResponseEntity<UserBasicResponseDto> userSimpleDtoOfPost = restTemplate.getForEntity(uriForPostUserInfo, UserBasicResponseDto.class);
        postDetailsQueryDto.setUserInfo(userSimpleDtoOfPost.getBody());

        if (!commentDtoList.isEmpty()) {
            List<String> commentWriterIds = new ArrayList<>() {{
                for (CommentQueryDto commentResponseDto : commentDtoList) {
                    add(commentResponseDto.getWriterId());
                }
            }};

            ResponseEntity<Map<String, UserBasicResponseDto>> userData = getResponseOfUserBasicDtoList(commentWriterIds);
            for (CommentQueryDto commentQueryDto : commentDtoList) {
                UserBasicResponseDto userInfo = userData.getBody().get(commentQueryDto.getWriterId());
                commentQueryDto.setUserInfo(userInfo);
            }

            postDetailsQueryDto.setComments(commentDtoList);
        }

        return ResponseEntity.ok(postDetailsQueryDto);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Long> createPost(@RequestParam Long teamId, @Valid @ModelAttribute PostRequestDto postRequestDto) {
        UserSimpleResponseDto myInfo = getMyInfoFromRedis();
        Long postId = postService.createPost(myInfo.getUserId(), teamId, postRequestDto);
        return new ResponseEntity<>(postId, HttpStatus.CREATED);
    }

    @PostMapping(value = "/{id}/update")
    public ResponseEntity<Long> updatePost(@PathVariable Long id, @Valid @ModelAttribute PostUpdateRequestDto postUpdateRequestDto) {
        UserSimpleResponseDto myInfo = getMyInfoFromRedis();
        Long postId = postService.updateInfo(id, myInfo.getUserId(), postUpdateRequestDto);
        return new ResponseEntity<>(postId, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deletePost(@PathVariable Long id) {
        UserSimpleResponseDto myInfo = getMyInfoFromRedis();
        postService.deletePost(id, myInfo.getUserId());
        return new ResponseEntity<>("삭제가 완료되었습니다.", HttpStatus.NO_CONTENT);
    }

    private ResponseEntity<Map<String, UserBasicResponseDto>> getResponseOfUserBasicDtoList(List<String> userIds) {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = UriComponentsBuilder.fromUriString("http://localhost:8000/api/users/basic")
                .queryParam("userIds", userIds)
                .build().toUri();
        return restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
    }

    private UserSimpleResponseDto getMyInfoFromRedis() {
        try {
            return redisUtils.getData(MY_INFO_KEY, UserSimpleResponseDto.class);
        } catch (Exception e) {
            throw new IllegalStateException(e.getLocalizedMessage());
        }
    }
}