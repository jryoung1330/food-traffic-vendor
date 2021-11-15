package com.foodtraffic.vendor.service.tag;

import com.foodtraffic.model.dto.TagDto;
import com.foodtraffic.vendor.entity.Tag;
import com.foodtraffic.vendor.repository.tag.TagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest {

    @Mock
    private TagRepository tagRepo;

    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private TagServiceImpl tagService;

    @Test
    public void givenTag_whenCreateTag_thenReturnTag() {
        Tag tag = mockTag();
        when(tagRepo.existsByName("new-tag")).thenReturn(false);
        when(tagRepo.save(any())).thenReturn(tag);
        TagDto result = tagService.createTag(tag);
        assertNotNull(result);
    }

    @Test
    public void givenNullTag_whenCreateTag_thenThrowException() {
        ResponseStatusException rse = assertThrows(ResponseStatusException.class,
                () -> tagService.createTag(null));
        assertEquals(HttpStatus.BAD_REQUEST, rse.getStatus());
    }

    @Test
    public void givenNullTagName_whenCreateTag_thenThrowException() {
        Tag tag = mockTag();
        tag.setName(null);
        ResponseStatusException rse = assertThrows(ResponseStatusException.class,
                () -> tagService.createTag(tag));
        assertEquals(HttpStatus.BAD_REQUEST, rse.getStatus());
    }

    @Test
    public void givenTagExists_whenCreateTag_thenThrowException() {
        Tag tag = mockTag();
        when(tagRepo.existsByName("new-tag")).thenReturn(true);
        ResponseStatusException rse = assertThrows(ResponseStatusException.class,
                () -> tagService.createTag(tag));
        assertEquals(HttpStatus.BAD_REQUEST, rse.getStatus());
    }

    private Tag mockTag() {
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("new-tag");
        return tag;
    }
}
