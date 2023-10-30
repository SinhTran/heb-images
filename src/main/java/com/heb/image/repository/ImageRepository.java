package com.heb.image.repository;

import com.heb.image.model.Image;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends CrudRepository<Image,Long> {
    @Query("SELECT s FROM Image s JOIN FETCH s.tags WHERE s.id in (SELECT t.image.id from Tag t where t.tag in :tags)")
    List<Image> findByTag(@Param("tags") List<String> tags);
}
