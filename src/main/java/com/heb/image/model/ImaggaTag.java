package com.heb.image.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImaggaTag {
    @JsonProperty("tag")
    private ImaggaEnTag imaggaEnTag;
    @JsonProperty("confidence")
    private float confidence;
}
