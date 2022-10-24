package fr.poulpogaz.nonogramssolver;

import picocli.CommandLine;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@CommandLine.Command
public class Main implements Runnable {

    public static void main(String[] args) {
        new CommandLine(new Main())
                .setCaseInsensitiveEnumValuesAllowed(true)
                .execute(args);
    }

    @CommandLine.Option(names = {"-i", "--input"}, required = true)
    private String input;

    @CommandLine.Option(names = {"-f", "--output-format"}, defaultValue = "detailed_gif")
    private OutputFormat outputFormat;

    @CommandLine.Option(names = {"-s", "-size", "--square-size"}, defaultValue = "20")
    private int squareSize;

    @CommandLine.Option(names = {"-t", "--time-between-frames"}, defaultValue = "-1")
    private int timeBetweenFrames;

    @Override
    public void run() {
        BufferedImage image;
        try {
            image = ImageIO.read(new File(input + ".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Nonogram nonogram = Nonogram.fromImage(image);

        long time = System.currentTimeMillis();
        try {
            File output = new File(input + ".gif");

            /*Path folder = Path.of(input);
            if (Files.notExists(folder)) {
                Files.createDirectory(folder);
            }*/

            AbstractGifOutput listener;
            if (outputFormat == OutputFormat.IMAGE) {
                return;
            } else if (outputFormat == OutputFormat.GIF) {
                listener = new GifOutput(output, timeBetweenFrames, squareSize);
            } else if (outputFormat == OutputFormat.DETAILED_GIF) {
                listener = new DetailedGifOutput(output, timeBetweenFrames, squareSize);
            } else {
                throw new IllegalStateException();
            }

            nonogram.solve(listener);

            listener.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        long time2 = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (time2 - time));
    }

    private static class DetailedGifOutput extends AbstractGifOutput {

        private final int squareSize;

        public DetailedGifOutput(File output, int timeBetweenFrames, int squareSize) throws IOException {
            super(output, timeBetweenFrames);
            this.squareSize = squareSize;
        }

        @Override
        public void onColumnTrySolve(Nonogram n, Descriptor d) {
            try {
                writer.writeToSequence(n.asImage(squareSize));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onRowTrySolve(Nonogram n, Descriptor d) {
            try {
                writer.writeToSequence(n.asImage(squareSize));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected int defaultTimeBetweenFrames() {
            return 2;
        }
    }


    private static class GifOutput extends AbstractGifOutput {

        private final int squareSize;

        public GifOutput(File output, int timeBetweenFrames, int squareSize) throws IOException {
            super(output, timeBetweenFrames);
            this.squareSize = squareSize;
        }

        @Override
        public void onPassFinished(Nonogram n) {
            try {
                writer.writeToSequence(n.asImage(squareSize));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected int defaultTimeBetweenFrames() {
            return 200;
        }
    }

    private static abstract class AbstractGifOutput extends SolverAdapter implements Closeable {

        protected ImageOutputStream ios;
        protected GifSequenceWriter writer;

        public AbstractGifOutput(File output, int timeBetweenFrames) throws IOException {
            ios = new FileImageOutputStream(output);

            if (timeBetweenFrames < 0) {
                timeBetweenFrames = defaultTimeBetweenFrames();

                if (timeBetweenFrames < 0) {
                    throw new IllegalStateException();
                }
            }

            writer = new GifSequenceWriter(ios,
                    BufferedImage.TYPE_INT_RGB,
                    timeBetweenFrames,
                    false);
        }

        protected abstract int defaultTimeBetweenFrames();

        @Override
        public void onFail(Nonogram n) {
            System.err.println("Failed to solve this nonogram. It requires advanced techniques like 'trial and error' or recursion");
        }

        @Override
        public void onSuccess(Nonogram n) {
            System.out.println("Nonogram solved!");
        }

        @Override
        public void close() throws IOException {
            if (writer != null) {
                writer.close();
                writer = null;
            }

            if (ios != null) {
                ios.close();
                ios = null;
            }
        }
    }
}
