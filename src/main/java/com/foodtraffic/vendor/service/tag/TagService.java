package com.foodtraffic.vendor.service.tag;

import com.foodtraffic.model.dto.TagDto;
import com.foodtraffic.vendor.entity.Tag;

import java.util.List;

public interface TagService {

    List<TagDto> getTags();

    TagDto createTag(Tag tag);
}
