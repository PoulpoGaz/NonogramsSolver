package fr.poulpogaz.nonogramssolver.reader;

import fr.poulpogaz.nonogramssolver.Nonogram;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ImageReader {

    public Nonogram read(Path path) throws IOException {
        return read(path, false, null);
    }

    public Nonogram read(Path path, boolean blackAndWhite) throws IOException {
        return read(path, blackAndWhite, null);
    }

    public Nonogram read(Path path, boolean blackAndWhite, Color background) throws IOException {
        BufferedImage image = ImageIO.read(path.toFile());

        Nonogram.Builder builder = new Nonogram.Builder();
        builder.setWidth(image.getWidth());
        builder.setHeight(image.getHeight());

        if (blackAndWhite) {
            if (background != null) {
                builder.setBackground(background);
            } else {
                builder.setBackground(Color.WHITE);
            }

            image = filterBlackWhite(image);
        } else if (background == null) {
            builder.setBackground(backgroundColor(image));
        } else {
            builder.setBackground(background);
        }

        for (int row = 0; row < image.getHeight(); row++) {
            addCluesRow(builder, image, row);
        }

        for (int column = 0; column < image.getWidth(); column++) {
            addCluesColumn(builder, image, column);
        }

        return builder.build();
    }

    private void addCluesRow(Nonogram.Builder builder, BufferedImage image, int row) {
        List<Line> lines = new ArrayList<>();

        int pos = 0;
        while (pos < image.getWidth()) {
            Line line = nextLineRow(image, row, pos);

            if (!line.color().equals(builder.getBackground())) {
                lines.add(line);
            }
            pos = line.end;
        }

        builder.setNumberOfClue(row, true, lines.size());
        for (Line line : lines) {
            builder.addClue(row, true, line.length(), line.color);
        }
    }

    private void addCluesColumn(Nonogram.Builder builder, BufferedImage image, int column) {
        List<Line> lines = new ArrayList<>();

        int pos = 0;
        while (pos < image.getHeight()) {
            Line line = nextLineColumn(image, column, pos);

            if (!line.color().equals(builder.getBackground())) {
                lines.add(line);
            }
            pos = line.end;
        }

        builder.setNumberOfClue(column, false, lines.size());
        for (Line line : lines) {
            builder.addClue(column, false, line.length(), line.color);
        }
    }

    private Line nextLineRow(BufferedImage image, int row, int start) {
        int end = start + 1;
        Color color = new Color(image.getRGB(start, row));

        int x = start + 1;
        while (x < image.getWidth() && image.getRGB(x, row) == color.getRGB()) {
            x++;
            end = x;
        }

        return new Line(start, end, color);
    }

    private Line nextLineColumn(BufferedImage image, int column, int start) {
        int end = start + 1;
        Color color = new Color(image.getRGB(column, start));

        int y = start + 1;
        while (y < image.getHeight() && image.getRGB(column, y) == color.getRGB()) {
            y++;
            end = y;
        }

        return new Line(start, end, color);
    }

    /**
     * @param start inclusive
     * @param end exclusive
     * @param color
     */
    private record Line(int start, int end, Color color) {

        public int length() {
            return end - start;
        }
    }



    private BufferedImage filterBlackWhite(BufferedImage in) {
        BufferedImage out = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < in.getHeight(); y++) {
            for (int x = 0; x < in.getWidth(); x++) {
                int rgb = in.getRGB(x, y);
                int alpha = (rgb >> 24) & 0xFF;
                int red   = (rgb >> 16) & 0xFF;
                int green = (rgb >>  8) & 0xFF;
                int blue  = (rgb      ) & 0xFF;

                float gray = (red + green + blue) / (3 * 255f);

                if (gray < 0.5 && alpha >= 0.5) {
                    out.setRGB(x, y, Color.BLACK.getRGB());
                } else {
                    out.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }

        return out;
    }

    /**
     * Find the first color that is transparent or return the most present color
     */
    private Color backgroundColor(BufferedImage image) {
        SortedMap<Integer, Integer> counter = new TreeMap<>();

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                int alpha = (rgb >> 24) & 0xFF;

                if (alpha < 255) {
                    int red   = (rgb >> 16) & 0xFF;
                    int green = (rgb >>  8) & 0xFF;
                    int blue  = (rgb     ) & 0xFF;

                    return new Color(red, green, blue);
                }

                int n = counter.getOrDefault(rgb, 0);
                counter.put(rgb, n + 1);
            }
        }

        return new Color(counter.lastKey());
    }
}
