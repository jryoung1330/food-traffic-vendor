package com.foodtraffic.vendor.service.tag;

import com.foodtraffic.model.dto.TagDto;
import com.foodtraffic.vendor.entity.Tag;
import com.foodtraffic.vendor.repository.tag.TagRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepository tagRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<TagDto> getTags() {
        List<Tag> tags = tagRepo.findAll();
        return modelMapper.map(tags, new TypeToken<List<TagDto>>(){}.getType());
    }

    @Override
    public TagDto createTag(Tag tag) {
        if(tag == null || tag.getName() == null || tagRepo.existsByName(tag.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else {
            tag = tagRepo.save(tag);
            return modelMapper.map(tag, TagDto.class);
        }
    }
}
