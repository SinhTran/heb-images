package com.heb.image.controller;

import com.heb.image.exception.ClientException;
import com.heb.image.model.ImageResponse;
import com.heb.image.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping
    public ResponseEntity<List<ImageResponse>> getImages(@RequestParam(name = "objects", required = false) List<String> objects) {
        return ResponseEntity.ok(imageService.searchImages(objects));
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<ImageResponse> getImage(@PathVariable("imageId") Long imageId) {
        return ResponseEntity.ok(imageService.getImage(imageId));
    }

    @PostMapping(
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ImageResponse> postImage(
            @RequestParam(name = "file", required = false) MultipartFile imageFile,
            @RequestParam(name = "label", required = false) String label,
            @RequestParam(name = "imageUrl", required = false) String imageUrl,
            @RequestParam(name = "objectEnabled", required = false) boolean objectEnabled
    ) throws IOException {
        System.out.println("postImage");
        if (imageFile == null && imageUrl == null) {
            throw new ClientException(HttpStatus.BAD_REQUEST, "Please provide imageUrl or upload a image");
        }
        ImageResponse response;
        if (imageFile != null) {
            response = imageService.processImageFile(imageFile, label, objectEnabled);
        } else {
            response = imageService.processImageUrl(imageUrl, label, objectEnabled);
        }
        return ResponseEntity.ok(response);
    }
}
