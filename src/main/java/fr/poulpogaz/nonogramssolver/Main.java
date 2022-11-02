package fr.poulpogaz.nonogramssolver;

import fr.poulpogaz.nonogramssolver.solver.Description;
import fr.poulpogaz.nonogramssolver.solver.NonogramSolver;
import fr.poulpogaz.nonogramssolver.solver.SolverAdapter;
import fr.poulpogaz.nonogramssolver.reader.WebpbnReader;
import fr.poulpogaz.nonogramssolver.solver.SolverListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

@CommandLine.Command(version = "1.0")
public class Main implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        new CommandLine(new Main())
                .setCaseInsensitiveEnumValuesAllowed(true)
                .execute(args);
    }

    private static class Input {

        @CommandLine.Option(names = {"--image"})
        private Path image;

        @CommandLine.Option(names = {"-w", "--webpbn"})
        private String webpbn;

        public Nonogram createNonogram() throws IOException, InterruptedException {
            if (image != null) {
                if (Files.isDirectory(image)) {
                    System.err.println("Input isn't an image");
                    return null;
                }

                BufferedImage image = ImageIO.read(this.image.toFile());

                return Nonogram.fromImage(image);
            } else {
                return new WebpbnReader().read(webpbn);
            }
        }

        public String name() {
            if (image != null) {
                return Utils.getFileName(image);
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

    @CommandLine.Option(names = {"-o", "--output"}, description = "output")
    private Path output;

    @CommandLine.Option(names = {"-d", "--detailed"})
    private boolean detailed;

    @CommandLine.Option(names = {"-s", "-size", "--square-size"}, defaultValue = "8")
    private int squareSize;

    @CommandLine.Option(names = {"-t", "--time-between-frames"}, defaultValue = "-1")
    private int timeBetweenFrames;

    @CommandLine.Option(names = {"-m", "--monitor"})
    private boolean monitor;

    @CommandLine.Option(names = {"--no-contradiction"})
    private boolean noContradiction;

    @CommandLine.Option(names = {"--no-recursion"})
    private boolean noRecursion;

    @Override
    public void run() {
        Nonogram nonogram;
        try {
            nonogram = input.createNonogram();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (nonogram == null) {
            return;
        }


        long time = System.currentTimeMillis();
        try {
            try (BasicListener listener = createOutput()) {
                if (listener == null) {
                    return;
                }

                if (monitor) {
                    Monitor m = new Monitor(nonogram);
                    m.runAsynchronously();
                }

                NonogramSolver solver = new NonogramSolver();
                boolean solved = solver.solve(nonogram, listener, !noContradiction, !noRecursion);

                if (solved) {
                    listener.onSuccess(nonogram);
                } else {
                    listener.onFail(nonogram);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        long time2 = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (time2 - time));
    }

    protected BasicListener createOutput() throws IOException {
        if (output == null) {
            return new BasicListener();
        } else if ((Files.notExists(output) && Utils.getExtension(output).isEmpty()) ||
                Files.isDirectory(output)) {
            Utils.createDirectories(output);

            return new MultipleImageOutput(output, input.name(), detailed, squareSize);
        } else {
            String extension = Utils.getExtension(output);

            if (extension.equals("gif")) {
                return new GifOutput(output, timeBetweenFrames, detailed, squareSize);

            } else if (extension.equals("png")) {
                String fileName = Utils.getFileName(output);

                if (input.isImageInput() && fileName.equals(input.name())) {
                    return new ImageOutput(
                            output.getParent().resolve(fileName + "_solution.png"),
                            squareSize);
                } else {
                    return new ImageOutput(output, squareSize);
                }

            } else {
                System.err.println("Output can be a folder, a gif or a png");
                return null;
            }
        }
    }






    protected class BasicListener extends SolverAdapter implements Closeable {

        @Override
        public void onFail(Nonogram n) {
            if (noRecursion || noContradiction) {
                System.err.println("Failed to solve this nonogram. It probably requires recursion or contradiction method");
            } else {
                System.err.println("Failed to solve this nonogram. It doesn't have a solution");
            }
        }

        @Override
        public void onSuccess(Nonogram n) {
            System.out.println("Nonogram solved!");
        }

        @Override
        public void close() throws IOException {

        }
    }

    private class ImageOutput extends BasicListener {

        private final int squareSize;
        private final File output;

        public ImageOutput(Path output, int squareSize) {
            this.output = output.toFile();
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
        public void close() {

        }
    }

    private class MultipleImageOutput extends BasicListener {

        private final Path outputFolder;
        private final String baseFileName;
        private final boolean detailed;
        private final int squareSize;

        private int i = 0;

        public MultipleImageOutput(Path outputFolder, String baseFileName, boolean detailed, int squareSize) {
            this.outputFolder = outputFolder;
            this.baseFileName = baseFileName;
            this.detailed = detailed;
            this.squareSize = squareSize;
        }

        @Override
        public void onLineSolved(Nonogram n, Description d) {
            if (detailed) {
                LOGGER.debug("Writing...");
                try {
                    ImageIO.write(n.asImage(squareSize), "png", nextFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                LOGGER.debug("Done!");
            }
        }

        @Override
        public void onPassFinished(Nonogram n) {
            if (!detailed) {
                try {
                    ImageIO.write(n.asImage(squareSize), "png", nextFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private File nextFile() {
            i++;

            return outputFolder.resolve(baseFileName + "_" + i + ".png").toFile();
        }

        @Override
        public void close() {

        }
    }

    private class GifOutput extends BasicListener implements Closeable {

        private ImageOutputStream ios;
        private GifSequenceWriter writer;
        private final boolean detailed;
        private final int squareSize;

        public GifOutput(Path output, int timeBetweenFrames, boolean detailed, int squareSize) throws IOException {
            ios = new FileImageOutputStream(output.toFile());

            if (timeBetweenFrames < 0) {
                if (detailed) {
                    timeBetweenFrames = 2;
                } else {
                    timeBetweenFrames = 200;
                }
            }

            writer = new GifSequenceWriter(ios,
                    BufferedImage.TYPE_INT_RGB,
                    timeBetweenFrames,
                    false);

            this.detailed = detailed;
            this.squareSize = squareSize;
        }

        @Override
        public void onLineSolved(Nonogram n, Description d) {
            if (detailed) {
                LOGGER.debug("Writing...");
                try {
                    writer.writeToSequence(n.asImage(squareSize));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                LOGGER.debug("Done!");
            }
        }

        @Override
        public void onPassFinished(Nonogram n) {
            if (!detailed) {
                LOGGER.debug("Writing...");
                try {
                    writer.writeToSequence(n.asImage(squareSize));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                LOGGER.debug("Done!");
            }
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
