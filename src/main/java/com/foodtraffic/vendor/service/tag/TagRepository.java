package com.foodtraffic.vendor.service.tag;

import com.foodtraffic.vendor.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Integer> {
    boolean existsByName(String name);
}
