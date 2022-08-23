package fr.poulpogaz.nonogramssolver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        BufferedImage image;
        try {
            image = ImageIO.read(new File("007.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Nonogram nonogram = Nonogram.fromImage(image);
        System.out.println(nonogram);
    }
}
