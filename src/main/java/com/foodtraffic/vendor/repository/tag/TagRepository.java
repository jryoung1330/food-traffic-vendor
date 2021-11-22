package com.foodtraffic.vendor.repository.tag;

import com.foodtraffic.vendor.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Integer> {
    boolean existsByName(String name);

    Page<Tag> findAllByNameIgnoreCaseContaining(String name, Pageable page);

    Page<Tag> findAll(Pageable page);
}
