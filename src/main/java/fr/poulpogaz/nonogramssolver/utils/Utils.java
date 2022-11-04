package fr.poulpogaz.nonogramssolver.utils;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {

    public static int[] toArray(List<Integer> integers) {
        int[] ints = new int[integers.size()];

        for (int i = 0; i < integers.size(); i++) {
            ints[i] = integers.get(i);
        }


        return ints;
    }

    public static List<Integer> toList(int[] ints) {
        List<Integer> list = new ArrayList<>(ints.length);

        for (int i : ints) {
            list.add(i);
        }

        return list;
    }

    public static void fill(boolean[][] tab, boolean value) {
        for (boolean[] booleans : tab) {
            Arrays.fill(booleans, value);
        }
    }

    public static int nDigit(int i) {
        return (int) Math.log10(i) + 1;
    }

    public static String getExtension(Path path) {
        String filename = path.getFileName().toString();

        int dot = filename.lastIndexOf('.');

        if (dot < 0) {
            return "";
        } else {
            return filename.substring(dot + 1);
        }
    }

    public static String getFileName(Path path) {
        String filename = path.getFileName().toString();

        int dot = filename.lastIndexOf('.');

        if (dot < 0) {
            return filename;
        } else {
            return filename.substring(0, dot);
        }
    }

    public static void createDirectories(Path p) throws IOException {
        if (Files.notExists(p)) {
            Files.createDirectories(p);
        }
    }

    public static boolean isDark(Color c) {
        return (c.getRed() + c.getGreen() + c.getBlue()) / (3 * 255f) < 0.5;
    }
}
