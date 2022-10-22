package fr.poulpogaz.nonogramssolver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        BufferedImage image;
        try {
            image = ImageIO.read(new File("std_mouse.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Nonogram nonogram = Nonogram.fromImage(image);
        System.out.println(nonogram);

        List<BufferedImage> images = nonogram.solveStepByStep(20, 20);

        try {
            for (int i = 0; i < images.size(); i++) {
                BufferedImage img = images.get(i);
                ImageIO.write(img, "png", new File("output/image_" + i + ".png"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
