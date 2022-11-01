package fr.poulpogaz.nonogramssolver.solver;

import fr.poulpogaz.nonogramssolver.Cell;

/**
 *
 * @param x x position of the guess
 * @param y y position of the guess
 * @param cells a copy of the old cells except at x, y it is now filled or crossed
 */
public record Guess(int x, int y, Cell[][] cells) {

    public Cell guess() {
        return cells[y][x];
    }
}
