package com.heb.image.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.heb.image.exception.ImageException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Iterator;

@Component
public class MetadataReader {

    private static final String TEMP_FILE = "downloaded.jpg";

    public String processMetadata(MultipartFile imageFile) throws IOException {
        File file = new File(TEMP_FILE);
        try (OutputStream os = new FileOutputStream(file)) {
            os.write(imageFile.getBytes());
        }
        String metadata = readMetadata(file);
        file.delete();
        return metadata;
    }

    public String processMetadata(String imageUrl) throws IOException {
        BufferedImage img;
        try {
            URL url = new URL(imageUrl);
            img = ImageIO.read(url);
        } catch (IOException e) {
            throw new ImageException(e, HttpStatus.BAD_REQUEST, "Unable to read image from url " + imageUrl);
        }
        File file = new File(TEMP_FILE);
        ImageIO.write(img, "jpg", file);
        String metadata = readMetadata(file);
        file.delete();
        return metadata;
    }

    private String readMetadata(File file) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode metadataArray = objectMapper.createArrayNode();
        try {
            ImageInputStream iis = ImageIO.createImageInputStream(file);
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

            if (readers.hasNext()) {

                // pick the first available ImageReader
                ImageReader reader = readers.next();

                // attach source to the reader
                reader.setInput(iis, true);

                // read metadata of first image
                IIOMetadata metadata = reader.getImageMetadata(0);
                String[] names = metadata.getMetadataFormatNames();
                for (int i = 0; i < names.length; i++) {
                    metadataArray.add(readRootMetadata(metadata.getAsTree(names[i])));
                }
            }
            return objectMapper.writeValueAsString(metadataArray);
        } catch (Exception e) {
            throw new ImageException(e);
        }
    }

    private ObjectNode readRootMetadata(Node root) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode.set(root.getNodeName(), readChildMetadata(root));
        return rootNode;
    }

    private ArrayNode readChildMetadata(Node node) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode nodeInJson = objectMapper.createArrayNode();

        NamedNodeMap map = node.getAttributes();
        if (map != null && map.getLength() > 0) {
            // attribute values
            ArrayNode attributes = objectMapper.createArrayNode();
            for (int i = 0; i < map.getLength(); i++) {
                Node attr = map.item(i);
                ObjectNode attribute = objectMapper.createObjectNode();
                attribute.put(attr.getNodeName(), attr.getNodeValue());
                attributes.add(attribute);
            }
            ObjectNode attrNode = objectMapper.createObjectNode();
            attrNode.set("attributes", attributes);
            nodeInJson.add(attrNode);
        }

        Node child = node.getFirstChild();
        if (child == null) {
            // no children, return
            return nodeInJson;
        }

        // children
        while (child != null) {
            // loop children recursively
            ObjectNode childNode = objectMapper.createObjectNode();
            childNode.set(child.getNodeName(), readChildMetadata(child));
            nodeInJson.add(childNode);
            child = child.getNextSibling();
        }
        return nodeInJson;
    }
}
