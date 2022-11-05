package fr.poulpogaz.nonogramssolver;

import fr.poulpogaz.nonogramssolver.utils.Utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class NonogramRenderer {

    public static final NonogramRenderer DEFAULT = new NonogramRenderer();

    public NonogramRenderer() {

    }

    public BufferedImage asImage(Nonogram n, int width, int height) {
        return asImage(n, bestSquareSizeFor(n, width, height));
    }

    public BufferedImage asImage(Nonogram n, int squareSize) {
        NonogramDimension dim = imageDimensionFor(n, squareSize);

        BufferedImage image = new BufferedImage(dim.imageWidth, dim.imageHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = image.createGraphics();
        try {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, dim.imageWidth, dim.imageHeight);

            drawNonogram(g2d, n, dim);
        } finally {
            g2d.dispose();
        }

        return image;
    }

    public void drawNonogram(Nonogram n, Graphics2D g2d, int width, int height) {
        drawNonogram(g2d, n, imageDimensionFor(n, bestSquareSizeFor(n, width, height)));
    }

    public int bestSquareSizeFor(Nonogram n, int width, int height) {
        int maxNClueRow = 0;
        for (Nonogram.Clue[] clues : n.getRows()) {
            maxNClueRow = Math.max(clues.length, maxNClueRow);
        }

        int maxNClueCols = 0;
        for (Nonogram.Clue[] clues : n.getColumns()) {
            maxNClueCols = Math.max(clues.length, maxNClueCols);
        }

        int nSquareWidth = n.getWidth() + maxNClueRow;
        int nSquareHeight = n.getHeight() + maxNClueCols;

        return Math.min(width / nSquareWidth, height / nSquareHeight);
    }

    private void drawNonogram(Graphics2D g2d, Nonogram n, NonogramDimension dim) {
        Font oldFont = g2d.getFont();
        g2d.setFont(g2d.getFont().deriveFont((float) dim.squareSize / dim.maxDigit));

        if (dim.rowWidth > 0 &&  dim.colHeight > 0) {
            Shape oldClip = g2d.getClip();

            drawColumnsClues(g2d, n, dim.gridOffsetX(), dim.gridOffsetY(), dim.squareSize);
            drawRowsClues(g2d, n, dim.gridOffsetY(), dim.gridOffsetX(), dim.squareSize);

            g2d.setClip(oldClip);
        }

        drawNonogram(g2d, n, dim.gridOffsetX(), dim.gridOffsetY(), dim.squareSize);
        drawGrid(g2d, n, dim);

        g2d.setFont(oldFont);
    }


    private NonogramDimension imageDimensionFor(Nonogram n, int squareSize) {
        int imageWidth = squareSize * n.getWidth();
        int imageHeight = squareSize * n.getHeight();

        int rowWidth = 0;
        int colHeight = 0;

        int maxDigit = 1;

        for (Nonogram.Clue[] clues : n.getRows()) {
            rowWidth = Math.max(clues.length * squareSize, rowWidth);

            for (Nonogram.Clue clue : clues) {
                maxDigit = Math.max(maxDigit, Utils.nDigit(clue.length()));
            }
        }

        for (Nonogram.Clue[] clues : n.getColumns()) {
            colHeight = Math.max(clues.length * squareSize, colHeight);

            for (Nonogram.Clue clue : clues) {
                maxDigit = Math.max(maxDigit, Utils.nDigit(clue.length()));
            }
        }

        imageWidth += rowWidth;
        imageHeight += colHeight;

        return new NonogramDimension(squareSize, imageWidth, imageHeight, rowWidth, colHeight, maxDigit);
    }

    private record NonogramDimension(int squareSize, int imageWidth, int imageHeight,
                                     int rowWidth, int colHeight, int maxDigit) {
        public int gridOffsetX() {
            return rowWidth;
        }

        public int gridOffsetY() {
            return colHeight;
        }
    }


    private void drawNonogram(Graphics2D g2d, Nonogram n, int offsetX, int offsetY, int squareSize) {
        Color[] colors = n.getColors();

        Color cross;
        if (Utils.isDark(n.getBackground())) {
            cross = Color.WHITE;
        } else {
            cross = Color.BLACK;
        }

        int drawX;
        int drawY = offsetY;
        for (int y = 0; y < n.getHeight(); y++) {

            drawX = offsetX;
            for (int x = 0; x < n.getWidth(); x++) {
                Cell cell = n.get(x, y);

                if (cell.isFilled()) {
                    g2d.setColor(colors[cell.getColor()]);
                    g2d.fillRect(drawX, drawY, squareSize, squareSize);
                } else if (cell.isEmpty()) {
                    //g2d.setColor(n.getBackground());
                    g2d.setColor(Color.WHITE);
                    g2d.fillRect(drawX, drawY, squareSize, squareSize);
                } else if (cell.isCrossed() && squareSize >= 3) {
                    g2d.setColor(n.getBackground());
                    g2d.fillRect(drawX, drawY, squareSize, squareSize);

                    g2d.setColor(cross);
                    g2d.drawLine(drawX, drawY, drawX + squareSize, drawY + squareSize);
                    g2d.drawLine(drawX + squareSize, drawY, drawX, drawY + squareSize);
                }

                drawX += squareSize;
            }

            drawY += squareSize;
        }
    }

    private void drawColumnsClues(Graphics2D g2d, Nonogram n, int xOffset, int maxY, int squareSize) {
        int x = xOffset;
        for (Nonogram.Clue[] clues : n.getColumns()) {
            int y = maxY - squareSize;

            for (int i = clues.length - 1; i >= 0; i--) {
                drawClue(g2d, x, y, n, clues[i], squareSize);

                y -= squareSize;
            }

            x += squareSize;
        }
    }

    private void drawRowsClues(Graphics2D g2d, Nonogram n, int yOffset, int maxX, int squareSize) {
        int y = yOffset;
        for (Nonogram.Clue[] clues : n.getRows()) {
            int x = maxX - squareSize;

            for (int i = clues.length - 1; i >= 0; i--) {
                drawClue(g2d, x, y, n, clues[i], squareSize);

                x -= squareSize;
            }

            y += squareSize;
        }
    }

    private void drawClue(Graphics2D g2d, int x, int y, Nonogram n, Nonogram.Clue clue, int squareSize) {
        FontMetrics fm = g2d.getFontMetrics();

        String str = Integer.toString(clue.length());

        g2d.setClip(x, y, squareSize, squareSize);

        Color background = n.getColors()[clue.color()];

        if (!n.isMonochrome() || !background.equals(Color.BLACK)) {
            g2d.setColor(background);
            g2d.fillRect(x, y, squareSize, squareSize);

            if (Utils.isDark(background)) {
                g2d.setColor(Color.WHITE);
            } else {
                g2d.setColor(Color.BLACK);
            }
        } else {
            g2d.setColor(Color.BLACK);
        }

        g2d.drawString(str,
                x + (squareSize - fm.stringWidth(str)) / 2,
                y + (squareSize - fm.getHeight()) / 2 + fm.getAscent());
    }

    private void drawGrid(Graphics2D g2d, Nonogram n, NonogramDimension dim) {
        if (dim.squareSize > 5) {
            g2d.setColor(Color.BLACK);

            // rows
            for (int y = 0; y < n.getHeight(); y++) {
                g2d.drawLine(0, dim.gridOffsetY() + y * dim.squareSize,
                        dim.imageWidth, dim.gridOffsetY() + y * dim.squareSize);
            }

            // cols
            for (int x = 0; x < n.getWidth(); x++) {
                g2d.drawLine(dim.gridOffsetX() + x * dim.squareSize, 0,
                        dim.gridOffsetX() + x * dim.squareSize, dim.imageHeight);
            }
        }
    }
}
