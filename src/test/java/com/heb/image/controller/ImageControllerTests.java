package com.heb.image.controller;

import com.heb.image.model.ImageResponse;
import com.heb.image.service.ImageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(ImageController.class)
public class ImageControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageService imageService;

    @Test
    void testGetImages_success() throws Exception {
        when(imageService.searchImages(any())).thenReturn(List.of(new ImageResponse()));
        this.mockMvc.perform(get("/images")).andExpect(status().isOk());
    }

    @Test
    void testGetImages_exception() throws Exception {
        when(imageService.searchImages(any())).thenThrow(new RuntimeException("test error"));
        this.mockMvc.perform(get("/images")).andExpect(status().isInternalServerError()).andExpect(content().string(containsString("test error")));
    }
}
