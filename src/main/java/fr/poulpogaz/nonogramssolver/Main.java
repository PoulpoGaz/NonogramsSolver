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
import java.util.ArrayDeque;
import java.util.Queue;

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

    @CommandLine.Option(names = {"--no-contradiction"})
    private boolean noContradiction;

    @CommandLine.Option(names = {"--no-recursion"})
    private boolean noRecursion;

    @CommandLine.Option(names = {"-m", "--monitor"})
    private boolean monitor;

    @CommandLine.Option(names = {"--auto-close", "--close-monitor-on-end"},
            description = "Close automatically the monitor when the nonogram is solved")
    private boolean shutdownOnEnd;

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
            try (BasicListener outputListener = createOutput()) {
                if (outputListener == null) {
                    return;
                }

                SolverListener listener = outputListener;
                Monitor m = null;
                if (monitor) {
                    m = new Monitor(nonogram);
                    listener = m.runAsynchronously(outputListener);
                }

                NonogramSolver solver = new NonogramSolver();
                boolean solved = solver.solve(nonogram, listener, !noContradiction, !noRecursion);

                if (solved) {
                    listener.onSuccess(nonogram);
                } else {
                    listener.onFail(nonogram);
                }

                if (m != null && shutdownOnEnd) {
                    m.shutdown();
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


    private abstract class MultipleOutput extends BasicListener {

        protected final boolean detailed;
        protected final int squareSize;

        protected final Queue<Cell[][]> cells = new ArrayDeque<>();

        public MultipleOutput(boolean detailed, int squareSize) {
            this.detailed = detailed;
            this.squareSize = squareSize;
        }

        @Override
        public void onLineSolved(Nonogram n, Description d, int mode) {
            if (detailed) {
                if (mode == CONTRADICTION) {
                    cells.offer(n.getCellsCopy());
                } else if (mode == LINE_SOLVING) {
                    newImage(n);
                }
            }
        }

        @Override
        public void onPassFinished(Nonogram n, int mode) {
            if (!detailed) {
                if (mode == CONTRADICTION) {
                    cells.offer(n.getCellsCopy());
                } else if (mode == LINE_SOLVING) {
                    newImage(n);
                }
            }
        }

        @Override
        public void onContradiction(Nonogram n, boolean found) {
            if (found) {
                newImage(n);
            }
            cells.clear();
        }

        @Override
        public void onSuccess(Nonogram n) {
            while (!cells.isEmpty()) {
                newImage(new Nonogram(n, cells.remove()));
            }

            super.onSuccess(n);
        }

        protected abstract void newImage(Nonogram nonogram);
    }






    private class MultipleImageOutput extends MultipleOutput {

        private final Path outputFolder;
        private final String baseFileName;

        private int i = 0;

        public MultipleImageOutput(Path outputFolder, String baseFileName, boolean detailed, int squareSize) {
            super(detailed, squareSize);
            this.outputFolder = outputFolder;
            this.baseFileName = baseFileName;
        }

        @Override
        protected void newImage(Nonogram nonogram) {
            LOGGER.debug("Writing...");
            try {
                ImageIO.write(nonogram.asImage(squareSize), "png", nextFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
            LOGGER.debug("Done!");
        }


        private File nextFile() {
            i++;

            return outputFolder.resolve(baseFileName + "_" + i + ".png").toFile();
        }

        @Override
        public void close() {

        }
    }

    private class GifOutput extends MultipleOutput implements Closeable {

        private ImageOutputStream ios;
        private GifSequenceWriter writer;

        public GifOutput(Path output, int timeBetweenFrames, boolean detailed, int squareSize) throws IOException {
            super(detailed, squareSize);
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
        }

        @Override
        protected void newImage(Nonogram nonogram) {
            LOGGER.debug("Writing...");
            try {
                writer.writeToSequence(nonogram.asImage(squareSize));
            } catch (IOException e) {
                e.printStackTrace();
            }
            LOGGER.debug("Done!");
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
