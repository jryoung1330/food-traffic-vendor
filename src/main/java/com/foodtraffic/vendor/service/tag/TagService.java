package com.foodtraffic.vendor.service.tag;

import com.foodtraffic.model.dto.TagDto;
import com.foodtraffic.model.response.Payload;
import com.foodtraffic.vendor.entity.Tag;

import java.util.List;

public interface TagService {

    Payload<List<TagDto>> getTags(String name, Integer page, Integer size);

    TagDto createTag(Tag tag);
}
