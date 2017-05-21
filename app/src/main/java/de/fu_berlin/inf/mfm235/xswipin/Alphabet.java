package de.fu_berlin.inf.mfm235.xswipin;

import java.util.ArrayList;
import java.util.Collections;

public class Alphabet {

    public ArrayList<Character> characters = new ArrayList<>();
    public ArrayList<Gesture> gestures = new ArrayList<>();

    public int size() {
        return characters.size();
    }

    public Character getCharacterFromGesture(Gesture gesture) {
        int index = -1;

        for (int i = 0; i < gestures.size(); i++) {
            if (gesture.equals(gestures.get(i))) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            return characters.get(index);
        }

        return null;
    }

    public Gesture getGestureFromCharacter(Character character) {
        int index = characters.indexOf(character);

        if (index != -1) {
            return gestures.get(index);
        }

        return null;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < characters.size(); i++) {
            builder.append(characters.get(i) + ":" + gestures.get(i) + ";");
        }

        return builder.toString();
    }

    public static Alphabet FromString(String string) {

        if (string.isEmpty()) {
            return null;
        }

        Alphabet alphabet = new Alphabet();

        for (String s : string.split(";")) {
            if (!s.isEmpty()) {
                alphabet.characters.add(s.charAt(0));
                alphabet.gestures.add(Gesture.fromString(s.substring(2)));
            }
        }

        return alphabet;
    }

    public static Alphabet MakeAlphabet(int numberOfSymbols) {

        Alphabet alphabet = new Alphabet();

        alphabet.gestures = Gesture.getAllSwipeGestures();

        for (int i = 0; i < numberOfSymbols; i++) {
            alphabet.characters.add(Character.forDigit(i, 10));
        }

        return alphabet;
    }

    public void shuffleGestures() {
        Collections.shuffle(gestures);
    }

}
