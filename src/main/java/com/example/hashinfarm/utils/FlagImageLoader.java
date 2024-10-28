package com.example.hashinfarm.utils;

import javafx.scene.image.Image;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class FlagImageLoader {
    public Image loadFlagImage(String flagFileName) throws IOException {
        String flagPath = "/icons/flags/" + flagFileName;
        try (InputStream flagStream = getClass().getResourceAsStream(flagPath)) {
            if (flagStream != null) {
                return new Image(flagStream);
            } else {
                return loadPlaceholderFlag();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return loadPlaceholderFlag();
        }
    }

    private Image loadPlaceholderFlag() {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/flags/placeholder.png")));
    }
}
