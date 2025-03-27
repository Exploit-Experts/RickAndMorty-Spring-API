package com.rickmorty.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }

    public String uploadFileFromUrl(String imageUrl, Long publicId, String folder) {
        try {
            URL url = new URL(imageUrl);
            BufferedImage bufferedImage = ImageIO.read(url);

            ByteArrayOutputStream pngStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", pngStream);
            byte[] pngData = pngStream.toByteArray();

            Map uploadResult = cloudinary.uploader().upload(pngData, ObjectUtils.asMap(
                    "public_id", folder + "/" + publicId,
                    "asset_folder", folder,
                    "resource_type", "image"
            ));
            return uploadResult.get("secure_url").toString();
        } catch (IOException io) {
            throw new RuntimeException(io);
        }
    }
}
