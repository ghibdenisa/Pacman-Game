package graphics;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class ImageLoader {
    private HashMap<String, Image> images;

    public ImageLoader() {
        images = new HashMap<>();
        loadAllImages();
    }

    private void loadAllImages() {
        images.put("wall", loadImage("/images/wall.png"));
        images.put("blueGhost", loadImage("/images/blueGhost.png"));
        images.put("orangeGhost", loadImage("/images/orangeGhost.png"));
        images.put("pinkGhost", loadImage("/images/pinkGhost.png"));
        images.put("redGhost", loadImage("/images/redGhost.png"));
        images.put("pacmanUp", loadImage("/images/pacmanUp.png"));
        images.put("pacmanDown", loadImage("/images/pacmanDown.png"));
        images.put("pacmanLeft", loadImage("/images/pacmanLeft.png"));
        images.put("pacmanRight", loadImage("/images/pacmanRight.png"));
        images.put("pacmanClosed", loadImage("/images/pacmanClosed.png"));
        images.put("pause", loadImage("/images/pause.png"));
    }

    private Image loadImage(String path) {
        return new ImageIcon(getClass().getResource(path)).getImage();
    }

    public Image getImage(String path) {
        return images.get(path);
    }
}
