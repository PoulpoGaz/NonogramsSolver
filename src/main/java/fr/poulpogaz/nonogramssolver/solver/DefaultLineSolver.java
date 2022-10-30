package fr.poulpogaz.nonogramssolver.solver;

import fr.poulpogaz.nonogramssolver.Cell;
import fr.poulpogaz.nonogramssolver.CellWrapper;
import fr.poulpogaz.nonogramssolver.Clue;
import fr.poulpogaz.nonogramssolver.Descriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultLineSolver extends AbstractRegion implements LineSolver {

    private Descriptor descriptor;

    /**
     * For each cell, contains an array of length the number of clue
     * containing true if the i-th clue can be present at the cell
     */
    private boolean[][] possibilities;

    public DefaultLineSolver() {
    }

    @Override
    public void trySolve(Descriptor descriptor) {
        setDescriptor(descriptor);

        if (!descriptor.hasChanged()) {
            return;
        }
        descriptor.setHasChanged(false);

        if (descriptor.nClues() == 0) {
            draw(0, descriptor.size(), Cell.CROSSED);
            return;
        }

        //System.out.println("-------------------------------------");
        //System.out.printf("Row: %b. Index: %d%n", descriptor.isRow(), descriptor.getIndex());
        //System.out.println("Clues: " + Arrays.toString(descriptor.getClues()));

        shrink();
        initClues();
        computePossibilities();
        optimizeCluesBoundWithOnePossibility();
        List<Line> lines = createLines();
        comparePossibilitiesAndLines(lines);
        crossZeroCells();

        List<Region> regions = split();

        if (regions.size() > 1) {
            for (Region r : regions) {
                r.trySolve();
            }
        } else {
            tryFill(lines);
        }
    }

    protected void setDescriptor(Descriptor descriptor) {
        this.descriptor = descriptor;

        if (possibilities == null ||
                possibilities.length < descriptor.size() ||
                possibilities[0].length < descriptor.nClues()) {
            possibilities = new boolean[descriptor.size()][descriptor.nClues()];
        }

        start = 0;
        end = descriptor.size();
        firstClueIndex = 0;
        lastClueIndex = descriptor.nClues();
    }

    protected List<Region> split() {
        List<Region> regions = new ArrayList<>();

        int firstClue = 0;
        for (int i = 0; i < descriptor.nClues() - 1; i++) {
            Clue clue = getClue(i);
            Clue next = getClue(i + 1);

            if (clue.getMaxI() <= next.getMinI()) { // no intersection => new region
                Region r = new Region(this);
                r.setFirstClueIndex(firstClue);
                r.setLastClueIndex(i + 1);
                r.setStart(getClue(firstClue).getMinI());
                r.setEnd(clue.getMaxI());

                regions.add(r);

                firstClue = i + 1;
            }
        }

        if (firstClue < descriptor.nClues()) {
            Region r = new Region(this);
            r.setFirstClueIndex(firstClue);
            r.setLastClueIndex(descriptor.nClues());
            r.setStart(getClue(firstClue).getMinI());
            r.setEnd(getClue(descriptor.nClues() - 1).getMaxI());

            regions.add(r);
        }

        return regions;
    }

    protected void shrink() {
        for (int i = 0; i < descriptor.size(); i++) {
            if (isCrossed(i)) {
                start = i + 1;
            } else {
                break;
            }
        }

        for (int i = descriptor.size() - 1; i >= 0; i--) {
            if (isCrossed(i)) {
                end = i;
            } else {
                break;
            }
        }
    }

    @Override
    protected CellWrapper getCell(int index) {
        return descriptor.getCell(index);
    }

    @Override
    protected void setCell(int index, Cell cell) {
        descriptor.setCell(index, cell);
    }

    @Override
    protected Clue getClue(int index) {
        return descriptor.getClue(index);
    }

    @Override
    protected void setPossibility(int cell, int clueIndex, boolean possibility) {
        possibilities[cell][clueIndex] = possibility;
    }

    @Override
    protected boolean possibility(int cell, int clueIndex) {
        return possibilities[cell][clueIndex];
    }

    public boolean[][] getPossibilities() {
        return possibilities;
    }
}
