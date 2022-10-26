package fr.poulpogaz.nonogramssolver.reader;

import fr.poulpogaz.nonogramssolver.Nonogram;

import java.io.IOException;

public interface IReader {

    Nonogram read(String url) throws IOException, InterruptedException;
}
