package com.heb.image.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ImaggaTagResults {
    @JsonProperty("result")
    private ImaggaTags results;
}
