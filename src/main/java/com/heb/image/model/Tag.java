package com.heb.image.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
public class Tag implements Serializable {
    @Id
    @GeneratedValue
    private Long id;
    private String tag;
    private float confidence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="image_id", referencedColumnName = "id")
    private Image image;

    public Tag(String tag, float confidence) {
        this.tag = tag;
        this.confidence = confidence;
    }

}
