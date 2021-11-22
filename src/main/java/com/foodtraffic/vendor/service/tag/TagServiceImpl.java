package com.foodtraffic.vendor.service.tag;

import com.foodtraffic.model.dto.TagDto;
import com.foodtraffic.model.response.Payload;
import com.foodtraffic.vendor.entity.Tag;
import com.foodtraffic.vendor.repository.tag.TagRepository;
import org.apache.logging.log4j.util.Strings;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    private static final int MAX_PAGE_SIZE = 25;

    @Override
    public Payload<List<TagDto>> getTags(String name, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE));

        Page<Tag> results;
        String uri = "/vendors/tags?";
        if(Strings.isBlank(name)) {
            results = tagRepo.findAll(pageable);
        } else {
            results = tagRepo.findAllByNameIgnoreCaseContaining(name, pageable);
            uri += "name=" + name + "&";
        }

        List<TagDto> tags = modelMapper.map(results.getContent(), new TypeToken<List<TagDto>>(){}.getType());
        return new Payload<>(tags, results, uri);
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
