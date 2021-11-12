package com.foodtraffic.vendor.controller.tag;

import com.foodtraffic.model.dto.TagDto;
import com.foodtraffic.vendor.entity.Tag;
import com.foodtraffic.vendor.service.tag.TagService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"}, allowCredentials="true")
@RestController
@Api(tags = "Tag")
@RequestMapping("/vendors")
public class TagController {

    @Autowired
    TagService tagService;

    @GetMapping("/tags")
    public List<TagDto> getTags() {
        return tagService.getTags();
    }

    @PostMapping("/tags")
    @ResponseStatus(HttpStatus.CREATED)
    public TagDto createTag(@RequestBody Tag tag) {
        return tagService.createTag(tag);
    }
}
