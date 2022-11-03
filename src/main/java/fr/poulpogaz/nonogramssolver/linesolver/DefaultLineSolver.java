package fr.poulpogaz.nonogramssolver.linesolver;

import fr.poulpogaz.nonogramssolver.Cell;
import fr.poulpogaz.nonogramssolver.solver.CellWrapper;
import fr.poulpogaz.nonogramssolver.solver.Clue;
import fr.poulpogaz.nonogramssolver.solver.Description;

import java.util.ArrayList;
import java.util.List;

public class DefaultLineSolver extends AbstractRegion implements LineSolver {

    /**
     * For each cell, contains an array of length the number of clue
     * containing true if the i-th clue can be present at the cell
     */
    private boolean[][] possibilities;

    public DefaultLineSolver() {
        super(null);
    }

    @Override
    public void trySolve(Description description) {
        setDescriptor(description);

        if (!description.hasChanged()) {
            return;
        }
        description.resetStatus();
        for (int i = 0; i < description.size(); i++) {
            getCell(i).resetStatus();
        }

        if (description.nClues() == 0) {
            draw(0, description.size(), Cell.CROSSED);
            return;
        }

        computePossibilities();
        optimizeCluesBoundWithOnePossibility();
        List<Line> lines = createLines();
        comparePossibilitiesAndLines(lines);

        if (!description.hasContradiction()) {
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
    }

    protected void setDescriptor(Description description) {
        this.description = description;

        if (possibilities == null ||
                possibilities.length < description.size() ||
                possibilities[0].length < description.nClues()) {
            possibilities = new boolean[description.size()][description.nClues()];
        }

        start = 0;
        end = description.size();
        firstClueIndex = 0;
        lastClueIndex = description.nClues();
    }

    protected List<Region> split() {
        List<Region> regions = new ArrayList<>();

        int firstClue = 0;
        for (int i = 0; i < description.nClues() - 1; i++) {
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

        if (firstClue < description.nClues()) {
            Region r = new Region(this);
            r.setFirstClueIndex(firstClue);
            r.setLastClueIndex(description.nClues());
            r.setStart(getClue(firstClue).getMinI());
            r.setEnd(getClue(description.nClues() - 1).getMaxI());

            regions.add(r);
        }

        return regions;
    }

    @Override
    protected void setPossibility(int cell, int clueIndex, boolean possibility) {
        possibilities[cell][clueIndex] = possibility;
    }

    @Override
    protected boolean isPossible(int cell, int clueIndex) {
        return possibilities[cell][clueIndex];
    }

    public boolean[][] getPossibilities() {
        return possibilities;
    }
}
