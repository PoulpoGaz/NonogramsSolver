package fr.poulpogaz.nonogramssolver;

import java.util.List;

public class Utils {

    public static int[] toArray(List<Integer> integers) {
        int[] ints = new int[integers.size()];

        for (int i = 0; i < integers.size(); i++) {
            ints[i] = integers.get(i);
        }


        return ints;
    }

    public static int nDigit(int i) {
        return (int) Math.log10(i) + 1;
    }
}
