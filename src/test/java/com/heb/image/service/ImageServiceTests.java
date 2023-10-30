package com.heb.image.service;

import com.heb.image.client.ImaggaClient;
import com.heb.image.model.*;
import com.heb.image.repository.ImageRepository;
import com.heb.image.util.MetadataReader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {ImageService.class,ImaggaClient.class, ImageRepository.class, MetadataReader.class})
public class ImageServiceTests {
    @MockBean
    private ImaggaClient imaggaClient;
    @MockBean
    private ImageRepository imageRepository;
    @MockBean
    private MetadataReader metadataReader;

    @Autowired
    private ImageService imageService;

    @Test
    public void testSearchImages_all_images() {
        List<Image> images = List.of(new Image(1L, "label", "metadata", null));
        when(imageRepository.findAll()).thenReturn(images);
        List<ImageResponse> actual = imageService.searchImages(null);
        assertEquals(1L, actual.get(0).getId());
        assertEquals("label", actual.get(0).getLabel());
        assertEquals("metadata", actual.get(0).getMetadata());
        assertEquals(null, actual.get(0).getObjects());
    }

    @Test
    public void testSearchImages_with_tags() {
        List<Image> images = List.of(new Image(1L, "label", "metadata", List.of(new Tag("cat", 100))));
        when(imageRepository.findByTag(any())).thenReturn(images);
        List<ImageResponse> actual = imageService.searchImages(List.of("cat", "dog"));
        assertEquals(1L, actual.get(0).getId());
        assertEquals("label", actual.get(0).getLabel());
        assertEquals("metadata", actual.get(0).getMetadata());
        assertEquals(List.of(new ImageObject("cat", 100)), actual.get(0).getObjects());
    }

    @Test
    public void testProcessImageUrl() throws IOException {
        when(metadataReader.processMetadata(anyString())).thenReturn("metadata test");
        when(imaggaClient.getTags(any())).thenReturn(createImaggaResults());

        ImageResponse actual = imageService.processImageUrl("imageUrl", null, true);
        assertEquals("cat", actual.getLabel());
        assertEquals("metadata test", actual.getMetadata());
        assertEquals("cat", actual.getObjects().get(0).getObject());
        assertEquals(100, actual.getObjects().get(0).getConfidence());
    }

    private ImaggaTagResults createImaggaResults() {
        ImaggaTagResults imaggaTagResults = new ImaggaTagResults();
        ImaggaTags imaggaTags = new ImaggaTags();
        ImaggaTag tag = new ImaggaTag(new ImaggaEnTag("cat"), 100);
        imaggaTags.setImaggaTags(List.of(tag));
        imaggaTagResults.setResults(imaggaTags);
        return imaggaTagResults;
    }
 }
