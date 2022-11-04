package fr.poulpogaz.nonogramssolver;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Nonogram {

    private final int width;
    private final int height;

    private final Clue[][] columns;
    private final Clue[][] rows;

    private final Cell[][] cells;

    private final Color[] colors;
    private final Color background;

    private Nonogram(Clue[][] rows, Clue[][] columns, Color[] colors, Color background) {
        this.width = columns.length;
        this.height = rows.length;
        this.rows = rows;
        this.columns = columns;
        this.colors = colors;
        this.background = background;
        this.cells = new Cell[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[y][x] = new Cell();
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

    public Clue[][] getRows() {
        return rows;
    }

    public Clue[][] getColumns() {
        return columns;
    }

    public Color getBackground() {
        return background;
    }

    public Color[] getColors() {
        return colors;
    }

    public boolean isMonochrome() {
        return colors.length == 1;
    }

    public record Clue(int length, int color) {
    }

    public static class Builder {

        private Color background;
        private Clue[][] rows;
        private Clue[][] columns;
        private List<Color> colors = new ArrayList<>();

        public Builder() {

        }

        public Nonogram build() {
            if (background == null) {
                background = Color.WHITE;
            }

            if (rows == null || rows.length == 0) {
                throw new IllegalStateException("No columns");
            }
            if (columns == null || columns.length == 0) {
                throw new IllegalStateException("No rows");
            }

            for (int i = 0; i < rows.length; i++) {
                Clue[] row = rows[i];
                if (row == null) {
                    throw new IllegalStateException("Row not set at " + i);
                }

                for (Clue c : row) {
                    if (colors.get(c.color()).equals(background)) {
                        throw new IllegalStateException("Clue in row " + i + " has the same color as the background color");
                    }
                }
            }

            for (int i = 0; i < columns.length; i++) {
                Clue[] column = columns[i];
                if (column == null) {
                    throw new IllegalStateException("Column not set at " + i);
                }

                for (Clue c : column) {
                    if (colors.get(c.color()).equals(background)) {
                        throw new IllegalStateException("Clue in column " + i + " has the same color as the background color");
                    }
                }
            }

            return new Nonogram(rows, columns, colors.toArray(new Color[0]), background);
        }


        public void setWidth(int width) {
            if (columns == null) {
                columns = new Clue[width][];
            } else if (columns.length != width) {
                columns = Arrays.copyOf(columns, width);
            }
        }

        public void setHeight(int height) {
            if (rows == null) {
                rows = new Clue[height][];
            } else if (rows.length != height) {
                rows = Arrays.copyOf(rows, height);
            }
        }

        public void setNumberOfClue(int i, boolean row, int n) {
            if (row) {
                if (rows[i] == null) {
                    rows[i] = new Clue[n];
                } else {
                    rows[i] = Arrays.copyOf(rows[i], n);
                }
            } else {
                if (columns[i] == null) {
                    columns[i] = new Clue[n];
                } else {
                    columns[i] = Arrays.copyOf(columns[i], n);
                }
            }
        }

        public void addClue(int i, boolean row, int length, Color color) {
            Clue[] c;
            if (row) {
                c = rows[i];
            } else {
                c = columns[i];
            }

            if (c == null) {
                throw new IllegalStateException();
            }

            for (int j = 0; j < c.length; j++) {
                if (c[j] == null) {
                    c[j] = new Clue(length, indexOf(color));
                    break;
                }
            }
        }

        private int indexOf(Color color) {
            int i = colors.indexOf(color);

            if (i < 0) {
                colors.add(color);
                return colors.size() - 1;
            } else {
                return i;
            }
        }

        public void setBackground(Color background) {
            this.background = background;
        }

        public Color getBackground() {
            return background;
        }
    }
}
