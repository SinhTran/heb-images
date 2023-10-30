package com.heb.image.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageResponse {
    private Long id;
    private String label;
    private String metadata;
    private List<ImageObject> objects;

}
