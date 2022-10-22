package fr.poulpogaz.nonogramssolver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String input = "std_mouse";

        BufferedImage image;
        try {
            image = ImageIO.read(new File(input + ".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Nonogram nonogram = Nonogram.fromImage(image);
        System.out.println(nonogram);

        List<BufferedImage> images = nonogram.solveStepByStep(30, 20);

        try {
            Path folder = Path.of(input);
            if (Files.notExists(folder)) {
                Files.createDirectory(folder);
            }

            for (int i = 0; i < images.size(); i++) {
                BufferedImage img = images.get(i);
                ImageIO.write(img, "png", new File(input + "/image_" + i + ".png"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
