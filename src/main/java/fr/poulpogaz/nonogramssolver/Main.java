package fr.poulpogaz.nonogramssolver;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

        try {
            Path folder = Path.of(input);
            if (Files.notExists(folder)) {
                Files.createDirectory(folder);
            }

            ImageOutputStream ios = new FileImageOutputStream(new File(input + ".gif"));
            GifSequenceWriter writer = new GifSequenceWriter(ios,
                    BufferedImage.TYPE_INT_RGB,
                    200,
                    false);


            nonogram.solve((n) -> {
                try {
                    writer.writeToSequence(n.asImage(20));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            writer.close();
            ios.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
