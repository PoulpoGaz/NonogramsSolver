package fr.poulpogaz.nonogramssolver;

import fr.poulpogaz.nonogramssolver.solver.CompleteLineSolver;
import fr.poulpogaz.nonogramssolver.solver.DefaultLineSolver;
import fr.poulpogaz.nonogramssolver.solver.LineSolver;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    private final Descriptor[] rows;
    private final Descriptor[] columns;

    private final CellWrapper[][] solution;

    public Nonogram(int[][] rows, int[][] columns) {
        this.width = columns.length;
        this.height = rows.length;

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
            r[y] = new Descriptor(true, y, rows[y], solution[y]);
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

            r[x] = new Descriptor(false, x, columns[x], wrappers);
        }

        return r;
    }

    // SOLVER!

    public boolean solve(SolverListener listener) {
        Objects.requireNonNull(listener);

        LineSolver solver = new DefaultLineSolver();
        LineSolver complete = new CompleteLineSolver();

        while (!isSolved()) {
            boolean changed = false;
            for (Descriptor col : columns) {
                if (col.hasChanged()) {
                    solver.trySolve(col);

                    if (col.hasChanged()) {
                        listener.onColumnTrySolve(this, col);
                        changed = true;
                    }
                }
            }

            for (Descriptor row : rows) {
                if (row.hasChanged()) {
                    solver.trySolve(row);

                    if (row.hasChanged()) {
                        listener.onRowTrySolve(this, row);
                        changed = true;
                    }
                }
            }

            if (!changed) {
                listener.onFail(this);
                return false;
            } else {
                listener.onPassFinished(this);
            }
        }

        listener.onSuccess(this);
        return true;
    }

    public boolean solve() {
        return solve(SolverListener.EMPTY_LISTENER);
    }

    private boolean isSolved() {
        for (Descriptor col : columns) {
            if (!col.isCompleted()) {
                return false;
            }
        }

        for (Descriptor row : rows) {
            if (!row.isCompleted()) {
                return false;
            }
        }

        return true;
    }




    // END OF SOLVER!


    public Cell[][] getSolution() {
        Cell[][] cells = new Cell[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[y][x] = solution[y][x].get();
            }
        }

        return cells;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public BufferedImage asImage(int squareSize) {
        return asImage(squareSize, true);
    }

    public BufferedImage asImage(int squareSize, boolean withClues) {
        int imgWidth = squareSize * width;
        int imgHeight = squareSize * height;
        int rowWidth = 0;
        int colHeight = 0;

        int maxDigit = 1;

        if (withClues) {
            for (Descriptor row : rows) {
                rowWidth = Math.max(row.nClues() * squareSize, rowWidth);

                for (Clue c : row.getClues()) {
                    maxDigit = Math.max(maxDigit, Utils.nDigit(c.getLength()));
                }
            }

            for (Descriptor col : columns) {
                colHeight = Math.max(col.nClues() * squareSize, colHeight);

                for (Clue c : col.getClues()) {
                    maxDigit = Math.max(maxDigit, Utils.nDigit(c.getLength()));
                }
            }

            imgWidth += rowWidth;
            imgHeight += colHeight;
        }

        BufferedImage image = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = image.createGraphics();
        try {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g2d.setFont(g2d.getFont().deriveFont((float) squareSize / maxDigit));

            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, imgWidth, imgHeight);

            int gridOffsetX = rowWidth;
            int gridOffsetY = colHeight;

            if (rowWidth > 0 && colHeight > 0) {
                drawColumnsClues(g2d, gridOffsetX, gridOffsetY, squareSize);
                drawRowsClues(g2d, gridOffsetY, gridOffsetX, squareSize);

                g2d.setClip(0, 0, imgWidth, imgHeight);
            }

            drawNonogram(g2d, gridOffsetX, gridOffsetY, squareSize);
            drawGrid(g2d, gridOffsetX, gridOffsetY, imgWidth, imgHeight, squareSize);
        } finally {
            g2d.dispose();
        }

        return image;
    }

    private void drawNonogram(Graphics2D g2d, int offsetX, int offsetY, int squareSize) {
        int drawX;
        int drawY = offsetY;
        for (int y = 0; y < height; y++) {

            drawX = offsetX;
            for (int x = 0; x < width; x++) {
                CellWrapper cell = solution[y][x];

                if (cell.isEmpty()) {
                    g2d.setColor(Color.WHITE);
                    g2d.fillRect(drawX, drawY, squareSize, squareSize);

                } else if (cell.isFilled()) {
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(drawX, drawY, squareSize, squareSize);

                } else if (cell.isCrossed() && squareSize >= 3) {
                    g2d.setColor(Color.WHITE);
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
        for (Descriptor col : columns) {
            int y = maxY - squareSize;

            for (int i = col.nClues() - 1; i >= 0; i--) {
                String str = Integer.toString(col.getClue(i).getLength());

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
        for (Descriptor row : rows) {
            int x = maxX - squareSize;

            for (int i = row.nClues() - 1; i >= 0; i--) {
                String str = Integer.toString(row.getClue(i).getLength());

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


    @Override
    public String toString() {
        // compute size

        int maxNumberInRow = 0;
        int squareSize = 1;
        for (Descriptor descriptor : rows) {
            maxNumberInRow = Math.max(maxNumberInRow, descriptor.nClues());

            for (Clue c : descriptor.getClues()) {
                squareSize = Math.max(squareSize, Utils.nDigit(c.getLength()));
            }
        }

        int maxNumberInCol = 0;
        for (Descriptor descriptor : columns) {
            maxNumberInCol = Math.max(maxNumberInCol, descriptor.nClues());

            for (Clue c : descriptor.getClues()) {
                squareSize = Math.max(squareSize, Utils.nDigit(c.getLength()));
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
     * @param clues the clues to draw
     * @param maxNumberInCol the maximal number of number that all cols contains
     */
    private void drawColNumbers(StringSurface surface,
                                Clue[] clues, int maxNumberInCol, int squareSize, int drawX) {
        int y = (maxNumberInCol - clues.length) * squareSize;
        for (Clue c : clues) {
            surface.set(drawX, y, String.valueOf(c.getLength()));

            y += squareSize;
        }
    }

    private void drawRowNumbers(StringSurface surface,
                                Clue[] clues, int maxNumberInRow, int squareSize, int drawY) {
        int x = (maxNumberInRow - clues.length) * squareSize;
        for (Clue c : clues) {
            surface.set(x, drawY, String.valueOf(c.getLength()));

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
