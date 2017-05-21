package de.fu_berlin.inf.mfm235.xswipin;

import java.util.ArrayList;

public class MatzUtils {

    public static String ToString(ArrayList<Long> input) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < input.size(); i++) {
            stringBuilder.append(input.get(i).toString() + ";");
        }
        return stringBuilder.toString();
    }

    public static ArrayList<Long> LongsFromString(String string) {
        ArrayList<Long> output = new ArrayList<>();
        if (string != null) {
            for (String s : string.split(";")) {
                if (!s.isEmpty())
                    output.add(Long.valueOf(s));
            }
        }
        return output;
    }

}
