package com.heb.image.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Image implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    private String label;
    @Lob
    private String metadata;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "image", fetch = FetchType.LAZY)
    private List<Tag> tags = new ArrayList<>();

    public void addTags(List<Tag> tags) {
        for (Tag tag: tags) {
            tag.setImage(this);
        }
        this.setTags(tags);
    }

}
