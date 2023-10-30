package com.heb.image.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ImaggaTags {
    @JsonProperty("tags")
    private List<ImaggaTag> imaggaTags;
}
