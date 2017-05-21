package de.fu_berlin.inf.mfm235.xswipin;


import android.graphics.PointF;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Gesture {

    public enum Direction {
        INVALID,
        NORTH,
        EAST,
        SOUTH,
        WEST,
        TAP
    }

    private ArrayList<Direction> swipes = new ArrayList<>();
    private ArrayList<PointF> points;

    private boolean invertable;

    public void addSwipe(Direction d) {
        if (swipes.isEmpty() || d != swipes.get(swipes.size() - 1)) {
            swipes.add(d);
        }
    }

    public ArrayList<Direction> getSwipes() {
        return swipes;
    }

    public void setInvertable(boolean invertable) {
        this.invertable = invertable;
    }

    public boolean isInvertable() {
        return invertable;
    }

    public boolean equals(Gesture other) {
        //return eq(this, other) || eq(this, other.inverseY());
        return eq(this, other);
    }

    public boolean weakEquals(Gesture other) {
        return eq(this, other) || eq(this, other.inverseY());
    }

    private static boolean eq(Gesture g, Gesture h) {

        // fixes the RALF-Bug
        if (g == null || g.swipes == null || h == null || h.swipes == null) {
            return false;
        }

        if (g.swipes.size() == h.swipes.size()) {
            for (int i = 0; i < g.swipes.size(); i++) {
                if (!g.swipes.get(i).equals(h.swipes.get(i))) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    public Gesture inverseX() {

        Gesture gesture = new Gesture();

        for (Direction direction : swipes) {
            switch (direction) {
                case NORTH:
                    gesture.addSwipe(Direction.SOUTH);
                    break;
                case SOUTH:
                    gesture.addSwipe(Direction.NORTH);
                    break;
                default:
                    gesture.addSwipe(direction);
                    break;
            }
        }

        return gesture;
    }

    public Gesture inverseY() {

        Gesture gesture = new Gesture();

        for (Direction direction : swipes) {
            switch (direction) {
                case EAST:
                    gesture.addSwipe(Direction.WEST);
                    break;
                case WEST:
                    gesture.addSwipe(Direction.EAST);
                    break;
                default:
                    gesture.addSwipe(direction);
                    break;
            }
        }

        return gesture;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (Direction swipe : swipes) {
            char d = swipe.toString().charAt(0);

            if (d != 'I') {
                stringBuilder.append(swipe.toString().charAt(0));
            }
        }

        if (invertable) {
            stringBuilder.append('X');
        }

        if (points != null) {
            stringBuilder.append('R');

            for (PointF p : points) {
                stringBuilder.append(p.x + "," + p.y + ":");
            }
        }

        return stringBuilder.toString();
    }

    public String string() {
        StringBuilder stringBuilder = new StringBuilder();

        for (Direction swipe : swipes) {
            char d = swipe.toString().charAt(0);

            if (d != 'I') {
                stringBuilder.append(swipe.toString().charAt(0));
            }
        }

        if (invertable) {
            stringBuilder.append('X');
        }

        return stringBuilder.toString();
    }

    public static Gesture fromString(String string) {

        Gesture gesture = new Gesture();

        boolean hasPoints = false;

        while (!string.isEmpty()) {

            switch (string.charAt(0)) {
                case 'N':
                    gesture.addSwipe(Direction.NORTH);
                    break;

                case 'E':
                    gesture.addSwipe(Direction.EAST);
                    break;

                case 'S':
                    gesture.addSwipe(Direction.SOUTH);
                    break;

                case 'W':
                    gesture.addSwipe(Direction.WEST);
                    break;

                case 'T':
                    gesture.addSwipe(Direction.TAP);
                    break;

                case 'X':
                    gesture.setInvertable(true);
                    break;

                case 'R':
                    hasPoints = true;
                    break;
            }

            string = string.substring(1);

            if (hasPoints) {
                break;
            }
        }

        if (hasPoints) {
            gesture.points = new ArrayList<>();
            for (String p : string.split(":")) {
                if (!p.isEmpty()) {
                    String x = p.split(",")[0];
                    String y = p.split(",")[1];
                    gesture.points.add(new PointF(Float.parseFloat(x), Float.parseFloat(y)));
                }
            }
        }

        return gesture;
    }

    public static ArrayList<Gesture> getAllSwipeGestures() {
        ArrayList<Gesture> list = new ArrayList<>();

        /*list.add(new Gesture() {{
            addSwipe(Direction.TAP);
        }});*/

        list.addAll(getAllOneSwipeGestures());
        list.addAll(getAllTwoSwipeGestures());

        return list;
    }

    public static ArrayList<Gesture> getAllOneSwipeGestures() {

        ArrayList<Gesture> list = new ArrayList<>();

        list.add(Gesture.fromString("N"));
        list.add(Gesture.fromString("S"));
        list.add(Gesture.fromString("W"));
        list.add(Gesture.fromString("E"));

        return list;
    }

    public static ArrayList<Gesture> getAllTwoSwipeGestures() {
        ArrayList<Gesture> list = new ArrayList<>();

        for (int i = 1; i <= 4; i++) {
            for (int j = 1; j <= 4; j++) {
                if (i == j) {
                    continue;
                }
                Gesture g = new Gesture();
                g.addSwipe(Direction.values()[i]);
                g.addSwipe(Direction.values()[j]);
                list.add(g);
            }
        }

        return list;
    }

    public static String ToString(ArrayList<Gesture> gestures) {

        StringBuilder stringBuilder = new StringBuilder();

        for (Gesture gesture : gestures) {
            stringBuilder.append(gesture.toString() + ";");
        }

        return stringBuilder.toString();
    }

    public static ArrayList<Gesture> FromString(String gestureString) {

        ArrayList<Gesture> list = new ArrayList<>();

        for (String string : gestureString.split(";")) {
            if (!string.isEmpty()) {
                list.add(Gesture.fromString(string));
            }
        }

        return list;
    }

    public static Gesture GetRandomGesture() {

        if (random == null) {
            random = new Random();
            random.setSeed(System.nanoTime());
        }

        ArrayList<Gesture> gestures = getAllSwipeGestures();
        return gestures.get(random.nextInt(gestures.size()));
    }

    private static Random random;

    public static ArrayList<ArrayList<PointF>> ProcessStroke(ArrayList<PointF> points) {

        if (points == null | points.size() == 0) {
            return new ArrayList<ArrayList<PointF>>();
        }

        return ProcessStroke(points, 1.2);

        /*ArrayList<ArrayList<PointF>> subStrokes;

        double c = 2;

        do {
            subStrokes = ProcessStroke(points, c);

            if (subStrokes.size() > 1) {
                break;
            }

            c -= 0.1;
        } while (c > 1.1);

        return subStrokes;*/
    }

    private static ArrayList<ArrayList<PointF>> ProcessStroke(ArrayList<PointF> points, double c) {

        ArrayList<ArrayList<PointF>> subStrokes = new ArrayList<>();

        Gesture.Direction currentDirection = null;
        ArrayList<PointF> currentSubStroke = null;

        for (int i = 0, j = 1; j < points.size(); i++, j++) {

            PointF p = points.get(i);
            PointF q = points.get(j);

            Gesture.Direction direction = GetDirection(p, q, c);

            if (direction == Gesture.Direction.INVALID) {
                currentDirection = null;
                continue;
            }

            if (currentSubStroke == null || direction != currentDirection) {
                currentSubStroke = new ArrayList<>();
                subStrokes.add(currentSubStroke);
                currentDirection = direction;
            }

            currentSubStroke.add(p);
            currentSubStroke.add(q);
        }

        float averageSubStrokeSize = 0;

        for (ArrayList<PointF> stroke : subStrokes) {
            for (int i = 0, j = 1; j < stroke.size(); i++, j++) {
                PointF p = stroke.get(i);
                PointF q = stroke.get(j);
                averageSubStrokeSize += GetDistance(p, q);
            }
        }

        if (subStrokes.size() > 1) {
            averageSubStrokeSize /= subStrokes.size();
        }

        for (Iterator<ArrayList<PointF>> iterator = subStrokes.iterator(); iterator.hasNext(); ) {
            ArrayList<PointF> subStroke = iterator.next();
            float subStrokeSize = 0;
            for (int i = 0, j = 1; j < subStroke.size(); i++, j++) {
                PointF p = subStroke.get(i);
                PointF q = subStroke.get(j);
                subStrokeSize += GetDistance(p, q);
            }
            if (subStrokeSize < averageSubStrokeSize / 4) {
                iterator.remove();
            }
        }

        return subStrokes;
    }

    public static Gesture GetGesture(ArrayList<PointF> points) {

        if (points.isEmpty()) {
            return new Gesture();
        }

        /*if (points.size() <= 2) {
            return new Gesture() {{
                addSwipe(Direction.TAP);
            }};
        }*/

        Gesture gesture = new Gesture();


        ArrayList<ArrayList<PointF>> subStrokes = ProcessStroke(points);

        for (int i = 0; i < subStrokes.size(); i++) {
            gesture.addSwipe(GetDirection(subStrokes.get(i)));
        }

        gesture.points = new ArrayList<>(points);

        return gesture;
    }

    private static float GetDistance(PointF p, PointF q) {

        float dx = q.x - p.x;
        float dy = q.y - p.y;

        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private static Gesture.Direction GetDirection(PointF p, PointF q) {

        float dx = q.x - p.x;
        float dy = q.y - p.y;

        if (Math.abs(dx) > 2 * Math.abs(dy)) {
            return dx > 0 ? Gesture.Direction.EAST : Gesture.Direction.WEST;
        }

        else if (Math.abs(dy) > 2 * Math.abs(dx)) {
            return dy > 0 ? Gesture.Direction.SOUTH : Gesture.Direction.NORTH;
        }

        else {
            return Gesture.Direction.INVALID;
        }

    }

    private static Gesture.Direction GetDirection(PointF p, PointF q, double c) {

        float dx = q.x - p.x;
        float dy = q.y - p.y;

        if (Math.abs(dx) > c * Math.abs(dy)) {
            return dx > 0 ? Gesture.Direction.EAST : Gesture.Direction.WEST;
        }

        else if (Math.abs(dy) > c * Math.abs(dx)) {
            return dy > 0 ? Gesture.Direction.SOUTH : Gesture.Direction.NORTH;
        }

        else {
            return Gesture.Direction.INVALID;
        }

    }

    public static Gesture.Direction GetDirection(ArrayList<PointF> subStroke) {
        return GetDirection(subStroke.get(0), subStroke.get(1));
    }

    public static Gesture.Direction GetDirection(ArrayList<PointF> subStroke, double c) {
        return GetDirection(subStroke.get(0), subStroke.get(1), c);
    }

    public ArrayList<PointF> getPoints() {
        return points;
    }
}
