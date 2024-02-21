// ImageUtil.java

package com.example.hashinfarm.controller.utility;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtil {

    public static ImageView createImageView(String imagePath) {
        try {
            URI uri = Objects.requireNonNull(ImageUtil.class.getResource(imagePath)).toURI();
            Path path = Paths.get(uri);

            ImageView imageView = new ImageView(new Image(path.toString()));
            imageView.setFitWidth(438);
            imageView.setFitHeight(205);
            return imageView;

        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }




}




