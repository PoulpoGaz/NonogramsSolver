package fr.poulpogaz.nonogramssolver;

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
}
