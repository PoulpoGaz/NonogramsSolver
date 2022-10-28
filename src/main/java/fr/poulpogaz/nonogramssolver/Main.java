package fr.poulpogaz.nonogramssolver;

import fr.poulpogaz.nonogramssolver.reader.WebpbnReader;
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

// netero
// drakkar
// red_deer
@CommandLine.Command(version = "1.0")
public class Main implements Runnable {

    public static void main(String[] args) {
        new CommandLine(new Main())
                .setCaseInsensitiveEnumValuesAllowed(true)
                .execute(args);
    }

    private static class Input {

        @CommandLine.Option(names = {"--image"})
        private String image;

        @CommandLine.Option(names = {"-w", "--webpbn"})
        private String webpbn;

        public Nonogram createNonogram() throws IOException, InterruptedException {
            if (image != null) {
                BufferedImage image = ImageIO.read(new File(this.image + ".png"));

                return Nonogram.fromImage(image);
            } else {
                return new WebpbnReader().read(webpbn);
            }
        }

        public String name() {
            if (image != null) {
                return image;
            } else {
                return webpbn;
            }
        }

        public boolean isImageInput() {
            return image != null;
        }

        public boolean isWebpbnInput() {
            return webpbn != null;
        }
    }

    @CommandLine.ArgGroup(exclusive = true)
    private Input input;

    @CommandLine.Option(names = {"-o", "--output"}, description = "output folder")
    private Path output;

    @CommandLine.Option(names = {"-f", "--output-format"}, defaultValue = "detailed_gif")
    private OutputFormat outputFormat;

    @CommandLine.Option(names = {"-s", "-size", "--square-size"}, defaultValue = "20")
    private int squareSize;

    @CommandLine.Option(names = {"-t", "--time-between-frames"}, defaultValue = "-1")
    private int timeBetweenFrames;

    @Override
    public void run() {
        Nonogram nonogram;
        try {
            nonogram = input.createNonogram();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        long time = System.currentTimeMillis();
        try {
            File output;
            BasicListener listener;
            if (outputFormat == OutputFormat.IMAGE) {
                if (input.isImageInput()) {
                    output = getOutput("solution", "png");
                } else {
                    output = getOutput(null, "png");
                }

                listener = new ImageOutput(output, squareSize);
            } else if (outputFormat == OutputFormat.GIF) {
                output = getOutput(null, "gif");

                listener = new GifOutput(output, timeBetweenFrames, squareSize);
            } else if (outputFormat == OutputFormat.DETAILED_GIF) {
                output = getOutput(null, "gif");

                listener = new DetailedGifOutput(output, timeBetweenFrames, squareSize);
            } else {
                throw new IllegalStateException();
            }

            createDirectories(output);

            try (listener) {
                nonogram.solve(listener);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        long time2 = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (time2 - time));
    }

    protected File getOutput(String subName, String extension) {
        String fileName;
        if (subName == null) {
            fileName = input.name() + "." + extension;
        } else {
            fileName = input.name() + "_" + subName + "." + extension;
        }

        if (output != null) {
            return output.resolve(fileName).toFile();
        } else {
            return new File(fileName);
        }
    }

    private void createDirectories(File output) throws IOException {
        Path p = output.toPath();

        if (!Files.exists(p.getParent())) {
            Files.createDirectories(p.getParent());
        }
    }


    protected static abstract class BasicListener extends SolverAdapter implements Closeable {

        @Override
        public void onFail(Nonogram n) {
            System.err.println("Failed to solve this nonogram. It requires advanced techniques like 'trial and error' or recursion");
        }

        @Override
        public void onSuccess(Nonogram n) {
            System.out.println("Nonogram solved!");
        }
    }

    private static class ImageOutput extends BasicListener {

        private final int squareSize;
        private final File output;

        public ImageOutput(File output, int squareSize) {
            this.output = output;
            this.squareSize = squareSize;
        }

        @Override
        public void onFail(Nonogram n) {
            super.onFail(n);

            try {
                ImageIO.write(n.asImage(squareSize), "png", output);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSuccess(Nonogram n) {
            super.onSuccess(n);

            try {
                ImageIO.write(n.asImage(squareSize), "png", output);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void close() throws IOException {

        }
    }

    private static abstract class AbstractGifOutput extends BasicListener implements Closeable {

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
}
