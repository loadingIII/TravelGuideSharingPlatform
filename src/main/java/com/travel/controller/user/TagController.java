package com.travel.controller.user;

import com.travel.pojo.common.ApiResponse;
import com.travel.pojo.model.Tag;
import com.travel.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping("/tags")
    public ApiResponse<List<Tag>> listTags() {
        return ApiResponse.success(tagService.listAllTags());
    }

    @GetMapping("/guides/{id}/tags")
    public ApiResponse<List<Tag>> guideTags(@PathVariable("id") Long id) {
        return ApiResponse.success(tagService.getTagsByGuideId(id));
    }

    @GetMapping("/tags/hot")
    public ApiResponse<List<Tag>> listHotTags() {
        return ApiResponse.success(tagService.listHotTags());
    }
}
