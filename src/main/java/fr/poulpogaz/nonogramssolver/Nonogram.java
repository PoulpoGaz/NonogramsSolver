package fr.poulpogaz.nonogramssolver;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Nonogram {

    public static Nonogram fromImage(BufferedImage image) {
        int[][] cols = new int[image.getWidth()][];
        int[][] rows = new int[image.getHeight()][];

        for (int x = 0; x < image.getWidth(); x++) {
            cols[x] = createSideNumbers(image, x, 0, false);
        }

        for (int y = 0; y < image.getHeight(); y++) {
            rows[y] = createSideNumbers(image, 0, y, true);
        }

        return new Nonogram(image.getWidth(), image.getHeight(), rows, cols);
    }

    /**
     * Create side numbers of the puzzle
     * @param image the solved puzzle
     * @param x row from which extract numbers, if row is true then it must be 0
     * @param y col from which extract numbers, if row is false, it must be 0
     * @param row
     * @return side numbers
     */
    private static int[] createSideNumbers(BufferedImage image, int x, int y, boolean row) {
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
            if (image.getRGB(x, y) == Color.BLACK.getRGB()) {
                length++;
            } else if (length > 0) {
                ints.add(length);
                length = 0;
            }

            x += xAdd;
            y += yAdd;
        }


        return Utils.toArray(ints);
    }

    private final int width;
    private final int height;

    private final Descriptor[] rows;
    private final Descriptor[] columns;

    private final CellWrapper[][] solution;
    private SolutionStatus status = SolutionStatus.NOT_SOLVED;

    public Nonogram(int width, int height, int[][] rows, int[][] columns) {
        this.width = width;
        this.height = height;

        solution = new CellWrapper[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                solution[y][x] = new CellWrapper(Cell.EMPTY, x, y);
            }
        }

        this.rows = createRows(rows);
        this.columns = createColumns(columns);
    }

    private Descriptor[] createRows(int[][] rows) {
        Descriptor[] r = new Descriptor[height];

        for (int y = 0; y < height; y++) {
            r[y] = new Descriptor(rows[y], solution[y], true);
        }

        return r;
    }

    private Descriptor[] createColumns(int[][] columns) {
        Descriptor[] r = new Descriptor[width];

        for (int x = 0; x < width; x++) {
            CellWrapper[] wrappers = new CellWrapper[height];

            for (int y = 0; y < height; y++) {
                wrappers[y] = solution[y][x];
            }

            r[x] = new Descriptor(columns[x], wrappers, false);
        }

        return r;
    }

    // SOLVER!

    public void solve() {
        /*while (true) {


        }*/

        rows[3].getCells()[4].set(Cell.FILLED);
    }













    // END OF SOLVER!

    public BufferedImage asImage(int squareSize) {
        BufferedImage image = new BufferedImage(squareSize * width, squareSize * height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2D = image.createGraphics();

        int drawX = 0;
        int drawY = 0;
        for (int y = 0; y < height; y++) {

            drawX = 0;
            for (int x = 0; x < width; x++) {
                CellWrapper cell = solution[y][x];

                if (cell.isEmpty()) {
                    g2D.setColor(Color.WHITE);
                    g2D.fillRect(drawX, drawY, squareSize, squareSize);

                } else if (cell.isFilled()) {
                    g2D.setColor(Color.BLACK);
                    g2D.fillRect(drawX, drawY, squareSize, squareSize);

                } else if (cell.isCrossed() && squareSize >= 3) {
                    g2D.setColor(Color.BLACK);
                    g2D.drawLine(drawX, drawY, drawX + squareSize, drawY + squareSize);
                    g2D.drawLine(drawX + squareSize, drawY, drawX, drawY + squareSize);
                }

                drawX += squareSize;
            }

            drawY += squareSize;
        }

        g2D.dispose();

        return image;
    }


    @Override
    public String toString() {
        // compute size

        int maxNumberInRow = 0;
        int squareSize = 1;
        for (Descriptor descriptor : rows) {
            maxNumberInRow = Math.max(maxNumberInRow, descriptor.nClues());

            for (int n : descriptor.getClues()) {
                squareSize = Math.max(squareSize, Utils.nDigit(n));
            }
        }

        int maxNumberInCol = 0;
        for (Descriptor descriptor : columns) {
            maxNumberInCol = Math.max(maxNumberInCol, descriptor.nClues());

            for (int n : descriptor.getClues()) {
                squareSize = Math.max(squareSize, Utils.nDigit(n));
            }
        }

        int w = (width + maxNumberInCol) * squareSize;
        int h = (height + maxNumberInCol) * squareSize;

        StringSurface surface = new StringSurface(w, h);

        // begin draw!
        // columns
        int drawX = maxNumberInRow * squareSize;
        for (int x = 0; x < width; x++) {
            drawColNumbers(surface, columns[x].getClues(), maxNumberInCol, squareSize, drawX);

            drawX += squareSize;
        }

        // rows
        int drawY = maxNumberInCol * squareSize;
        for (int y = 0; y < height; y++) {
            drawRowNumbers(surface, rows[y].getClues(), maxNumberInRow, squareSize, drawY);

            drawY += squareSize;
        }

        // solution
        int xDraw;
        int yDraw = maxNumberInRow * squareSize;
        for (int y = 0; y < height; y++) {
            xDraw = maxNumberInCol * squareSize;

            for (int x = 0; x < width; x++) {
                CellWrapper cell = solution[y][x];

                for (int x2 = 0; x2 < squareSize; x2++) {
                    for (int y2 = 0; y2 < squareSize; y2++) {
                        surface.set(xDraw + x2, yDraw + y2, cell.get().getChar());
                    }
                }

                xDraw += squareSize;
            }

            yDraw += squareSize;
        }

        return surface.getBuilder().toString();
    }

    /**
     * Draw a col
     * @param surface the surface to draw on
     * @param numbers the numbers to draw
     * @param maxNumberInCol the maximal number of number that all cols contains
     */
    private void drawColNumbers(StringSurface surface,
                                int[] numbers, int maxNumberInCol, int squareSize, int drawX) {
        int y = (maxNumberInCol - numbers.length) * squareSize;
        for (int n : numbers) {
            surface.set(drawX, y, String.valueOf(n));

            y += squareSize;
        }
    }

    private void drawRowNumbers(StringSurface surface,
                                int[] numbers, int maxNumberInRow, int squareSize, int drawY) {
        int x = (maxNumberInRow - numbers.length) * squareSize;
        for (int n : numbers) {
            surface.set(x, drawY, String.valueOf(n));

            x += squareSize;
        }
    }


    private static class StringSurface {

        private final StringBuilder builder;
        private final int width;
        private final int height;

        private final int widthWithLineSeparator;

        public StringSurface(int width, int height) {
            this.width = width;
            this.height = height;
            this.widthWithLineSeparator = width + System.lineSeparator().length();

            int s = widthWithLineSeparator * height;
            builder = new StringBuilder(s);
            builder.setLength(s);

            clear();
        }

        private void clear() {
            for (int i = 0; i < builder.length(); i++) {
                builder.setCharAt(i, ' ');
            }

            setLineSeparators();
        }

        private void setLineSeparators() {
            String line = System.lineSeparator();

            int i = width;
            for (int y = 0; y < height; y++) {
                builder.replace(i, i + line.length(), line);

                i += widthWithLineSeparator;
            }
        }

        public void set(int x, int y, String str) {
            check(x, y);

            int start = y * widthWithLineSeparator + x;
            int end = start + str.length();

            builder.replace(start, end, str);
        }

        public void set(int x, int y, char c) {
            check(x, y);
            builder.setCharAt(y * widthWithLineSeparator + x, c);
        }

        private void check(int x, int y) {
            if (x < 0 || y < 0 || x > width || y > height) {
                throw new IllegalArgumentException("X/Y is out of bounds: coords=(%d; %d) size=(%d; %d)".formatted(x, y, width, height));
            }
        }

        public StringBuilder getBuilder() {
            return builder;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }
}