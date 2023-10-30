package com.heb.image.service;

import com.heb.image.client.ImaggaClient;
import com.heb.image.exception.ImageNotFoundException;
import com.heb.image.model.*;
import com.heb.image.repository.ImageRepository;
import com.heb.image.util.MetadataReader;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ImageService {

    private final ImaggaClient imaggaClient;
    private final ImageRepository imageRepository;
    private final MetadataReader metadataReader;

    public ImageService(ImaggaClient imaggaClient, ImageRepository imageRepository, MetadataReader metadataReader) {
        this.imaggaClient = imaggaClient;
        this.imageRepository = imageRepository;
        this.metadataReader = metadataReader;
    }

    public ImageResponse processImageFile(MultipartFile imageFile, String label, boolean objectEnabled) throws IOException {
        String metadata = metadataReader.processMetadata(imageFile);
        ImaggaTagResults tagResults = null;
        if (objectEnabled) {
            tagResults = imaggaClient.getTagsByPOST(imageFile);
        }

        return saveImage(metadata, label, tagResults);
    }

    public ImageResponse processImageUrl(String imageUrl, String label, boolean objectEnabled) throws IOException {
        String metadata = metadataReader.processMetadata(imageUrl);
        ImaggaTagResults tagResults = null;
        if (objectEnabled) {
            tagResults = imaggaClient.getTags(imageUrl);
        }
        return saveImage(metadata, label, tagResults);
    }

    private ImageResponse saveImage(String metadata, String label, ImaggaTagResults tagResults) {
        Image image = new Image();
        image.setMetadata(metadata);
        if (tagResults != null && tagResults.getResults() != null && tagResults.getResults().getImaggaTags() != null) {
            List<Tag> tags = tagResults.getResults().getImaggaTags().stream()
                    .map(tag -> new Tag(tag.getImaggaEnTag().getTag(), tag.getConfidence())).collect(Collectors.toList());
            image.addTags(tags);
        }
        if (StringUtils.isBlank(label)) {
            if (CollectionUtils.isNotEmpty(image.getTags())) {
                label = image.getTags().get(0).getTag();
            } else {
                label = "undefined_label";
            }
        }
        image.setLabel(label);
        imageRepository.save(image);
        return convertImageToResponse(image);
    }

    private ImageResponse convertImageToResponse(Image image) {
        ImageResponse response = new ImageResponse();
        response.setMetadata(image.getMetadata());
        if (image.getTags() != null) {
            response.setObjects(image.getTags().stream().map(tag -> new ImageObject(tag.getTag(), tag.getConfidence())).collect(Collectors.toList()));
        }
        response.setId(image.getId());
        response.setLabel(image.getLabel());
        return response;
    }

    public List<ImageResponse> searchImages(List<String> tags) {
        List<ImageResponse> toReturn = new ArrayList<>();
        List<Image> images;
        if (tags != null && tags.size() > 0) {
            images = imageRepository.findByTag(tags);
        } else {
            images = (List<Image>) imageRepository.findAll();
        }
        for (Image image: images) {
            toReturn.add(convertImageToResponse(image));
        }
        return toReturn;
    }

    public ImageResponse getImage(Long imageId) {
        Optional<Image> result = imageRepository.findById(imageId);
        if (result.isPresent()) {
            return convertImageToResponse(result.get());
        } else {
            throw new ImageNotFoundException("ImageId " + imageId + " not found.");
        }
    }
}
