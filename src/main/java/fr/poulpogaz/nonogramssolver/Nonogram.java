package fr.poulpogaz.nonogramssolver;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Nonogram {

    public static Nonogram fromImage(BufferedImage image) {
        int[][] cols = new int[image.getWidth()][];
        int[][] rows = new int[image.getHeight()][];

        for (int x = 0; x < image.getWidth(); x++) {
            cols[x] = createDescription(image, x, 0, false);
        }

        for (int y = 0; y < image.getHeight(); y++) {
            rows[y] = createDescription(image, 0, y, true);
        }

        return new Nonogram(rows, cols);
    }

    /**
     * Create side numbers of the puzzle
     * @param image the solved puzzle
     * @param x row from which extract numbers, if row is true then it must be 0
     * @param y col from which extract numbers, if row is false, it must be 0
     * @param row
     * @return side numbers
     */
    private static int[] createDescription(BufferedImage image, int x, int y, boolean row) {
        // precondition checks
        int xAdd = 0;
        int yAdd = 0;
        if (row) {
            xAdd = 1;

            if (x != 0) {
                throw new IllegalStateException();
            }

        } else {
            yAdd = 1;

            if (y != 0) {
                throw new IllegalStateException();
            }

        }

        List<Integer> ints = new ArrayList<>();

        int length = 0;
        while (x < image.getWidth() && y < image.getHeight()) {
            int rgb = image.getRGB(x, y);
            int red = (rgb >> 16) & 0xFF;
            int green = (rgb >> 8) & 0xFF;
            int blue = (rgb >> 0) & 0xFF;

            float gray = (red + green + blue) / (3 * 255f);

            if (gray < 0.5) {
                length++;
            } else if (length > 0) {
                ints.add(length);
                length = 0;
            }

            x += xAdd;
            y += yAdd;
        }

        if (length > 0) {
            ints.add(length);
        }

        return Utils.toArray(ints);
    }





    private final int width;
    private final int height;

    private final int[][] rows;
    private final int[][] columns;

    private final Cell[][] cells;

    private Color background = Color.WHITE;
    private Color[] colors = new Color[] {Color.BLACK};

    public Nonogram(int[][] rows, int[][] columns) {
        this.width = columns.length;
        this.height = rows.length;
        this.rows = rows;
        this.columns = columns;
        this.cells = new Cell[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[y][x] = new Cell();
            }
        }
    }

    public Nonogram(Nonogram nonogram, Cell[][] cells) {
        this.width = nonogram.getWidth();
        this.height = nonogram.getHeight();
        this.rows = nonogram.getRows();
        this.columns = nonogram.getColumns();
        this.cells = cells;

        if (cells.length != height) {
            throw new IllegalArgumentException();
        }

        for (Cell[] cells2 : cells) {
            if (cells2.length != width) {
                throw new IllegalArgumentException();
            }
        }
    }

    public BufferedImage asImage(int width, int height) {
        return asImage(bestSquareSizeFor(width, height));
    }

    public BufferedImage asImage(int squareSize) {
        NonogramDimension dim = imageDimensionFor(squareSize);

        BufferedImage image = new BufferedImage(dim.imageWidth, dim.imageHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = image.createGraphics();
        try {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, dim.imageWidth, dim.imageHeight);

            drawNonogram(g2d, dim);
        } finally {
            g2d.dispose();
        }

        return image;
    }

    public void drawNonogram(Graphics2D g2d, int width, int height) {
        drawNonogram(g2d, imageDimensionFor(bestSquareSizeFor(width, height)));
    }

    public int bestSquareSizeFor(int width, int height) {
        int maxNClueRow = 0;
        for (int[] clues : rows) {
            maxNClueRow = Math.max(clues.length, maxNClueRow);
        }

        int maxNClueCols = 0;
        for (int[] clues : columns) {
            maxNClueCols = Math.max(clues.length, maxNClueCols);
        }

        int nSquareWidth = this.width + maxNClueRow;
        int nSquareHeight = this.height + maxNClueCols;

        return Math.min(width / nSquareWidth, height / nSquareHeight);
    }

    private void drawNonogram(Graphics2D g2d, NonogramDimension dim) {
        Font oldFont = g2d.getFont();
        g2d.setFont(g2d.getFont().deriveFont((float) dim.squareSize / dim.maxDigit));

        int gridOffsetX = dim.rowWidth;
        int gridOffsetY = dim.colHeight;

        if (dim.rowWidth > 0 &&  dim.colHeight > 0) {
            Shape oldClip = g2d.getClip();

            drawColumnsClues(g2d, gridOffsetX, gridOffsetY, dim.squareSize);
            drawRowsClues(g2d, gridOffsetY, gridOffsetX, dim.squareSize);

            g2d.setClip(oldClip);
        }

        drawNonogram(g2d, gridOffsetX, gridOffsetY, dim.squareSize);
        drawGrid(g2d, gridOffsetX, gridOffsetY, dim.imageWidth, dim.imageHeight, dim.squareSize);

        g2d.setFont(oldFont);
    }


    private NonogramDimension imageDimensionFor(int squareSize) {
        int imageWidth = squareSize * width;
        int imageHeight = squareSize * height;

        int rowWidth = 0;
        int colHeight = 0;

        int maxDigit = 1;

        for (int[] clues : rows) {
            rowWidth = Math.max(clues.length * squareSize, rowWidth);

            for (int clue : clues) {
                maxDigit = Math.max(maxDigit, Utils.nDigit(clue));
            }
        }

        for (int[] clues : columns) {
            colHeight = Math.max(clues.length * squareSize, colHeight);

            for (int clue : clues) {
                maxDigit = Math.max(maxDigit, Utils.nDigit(clue));
            }
        }

        imageWidth += rowWidth;
        imageHeight += colHeight;

        return new NonogramDimension(squareSize, imageWidth, imageHeight, rowWidth, colHeight, maxDigit);
    }

    private record NonogramDimension(int squareSize, int imageWidth, int imageHeight,
                                     int rowWidth, int colHeight, int maxDigit) {}


    private void drawNonogram(Graphics2D g2d, int offsetX, int offsetY, int squareSize) {
        int drawX;
        int drawY = offsetY;
        for (int y = 0; y < height; y++) {

            drawX = offsetX;
            for (int x = 0; x < width; x++) {
                Cell cell = cells[y][x];

                if (cell.isFilled()) {
                    g2d.setColor(colors[cell.getColor()]);
                    g2d.fillRect(drawX, drawY, squareSize, squareSize);
                } else if (cell.isEmpty()) {
                    g2d.setColor(background);
                    g2d.fillRect(drawX, drawY, squareSize, squareSize);
                } else if (cell.isCrossed() && squareSize >= 3) {
                    g2d.setColor(background);
                    g2d.fillRect(drawX, drawY, squareSize, squareSize);

                    g2d.setColor(Color.BLACK);
                    g2d.drawLine(drawX, drawY, drawX + squareSize, drawY + squareSize);
                    g2d.drawLine(drawX + squareSize, drawY, drawX, drawY + squareSize);
                }

                drawX += squareSize;
            }

            drawY += squareSize;
        }
    }

    private void drawColumnsClues(Graphics2D g2d, int xOffset, int maxY, int squareSize) {
        g2d.setColor(Color.BLACK);
        FontMetrics fm = g2d.getFontMetrics();

        int x = xOffset;
        for (int[] clues : columns) {
            int y = maxY - squareSize;

            for (int i = clues.length - 1; i >= 0; i--) {
                String str = Integer.toString(clues[i]);

                g2d.setClip(x, y, squareSize, squareSize);
                g2d.drawString(str,
                        x + (squareSize - fm.stringWidth(str)) / 2,
                        y + (squareSize - fm.getHeight()) / 2 + fm.getAscent());

                y -= squareSize;
            }

            x += squareSize;
        }
    }

    private void drawRowsClues(Graphics2D g2d, int yOffset, int maxX, int squareSize) {
        g2d.setColor(Color.BLACK);
        FontMetrics fm = g2d.getFontMetrics();

        int y = yOffset;
        for (int[] clues : rows) {
            int x = maxX - squareSize;

            for (int i = clues.length - 1; i >= 0; i--) {
                String str = Integer.toString(clues[i]);

                g2d.setClip(x, y, squareSize, squareSize);
                g2d.drawString(str,
                        x + (squareSize - fm.stringWidth(str)) / 2,
                        y + (squareSize - fm.getHeight()) / 2 + fm.getAscent());

                x -= squareSize;
            }

            y += squareSize;
        }
    }

    private void drawGrid(Graphics2D g2d, int gridOffsetX, int gridOffsetY, int imgWidth, int imgHeight, int squareSize) {
        if (squareSize > 5) {
            g2d.setColor(Color.BLACK);

            // rows
            for (int y = 0; y < height; y++) {
                g2d.drawLine(0, gridOffsetY + y * squareSize,
                        imgWidth, gridOffsetY + y * squareSize);
            }

            // cols
            for (int x = 0; x < width; x++) {
                g2d.drawLine(gridOffsetX + x * squareSize, 0,
                        gridOffsetX + x * squareSize, imgHeight);
            }
        }
    }

    public Cell[][] getCells() {
        return cells;
    }

    public Cell[][] getCellsCopy() {
        Cell[][] cells = new Cell[height][width];

        for (int y = 0; y < height; y++) {
            cells[y] = Arrays.copyOf(this.cells[y], width);
        }

        return cells;
    }

    public Cell get(int x, int y) {
        return cells[y][x];
    }

    public boolean isEmpty(int x, int y) {
        return cells[y][x].isEmpty();
    }

    public boolean isFilled(int x, int y) {
        return cells[y][x].isFilled();
    }

    public boolean isCrossed(int x, int y) {
        return cells[y][x].isCrossed();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[][] getRows() {
        return rows;
    }

    public int[][] getColumns() {
        return columns;
    }
}
