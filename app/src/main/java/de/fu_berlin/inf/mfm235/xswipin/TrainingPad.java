package de.fu_berlin.inf.mfm235.xswipin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Shader;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;

public class TrainingPad extends View implements ChallengeInput {

    private InputReceiver inputReceiver;

    private final static float TOUCH_BORDER = 50;
    private final static float MOVE_THRESHOLD = 10;

    private float width, height, lastX, lastY;

    private ArrayList<PointF> points = new ArrayList<>();

    private Gesture nextGesture;

    private Paint paintSwipes = new Paint() {{
        setStrokeWidth(2);
        setAntiAlias(true);
    }};

    private Paint paintArrow = new Paint() {{
        setColor(Color.argb(255, 0, 0, 0));
        setStyle(Style.STROKE);
        setStrokeWidth(12);
    }};

    public TrainingPad(Context context) {
        super(context);
        setBackgroundColor(Color.BLACK);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (nextGesture != null) {
            canvas.drawCircle(width / 2, width / 2, width / 4, new Paint() {{
                setShader(new LinearGradient(0, 0, width / 4, width / 4, new int[] { Color.WHITE, Color.LTGRAY, Color.WHITE }, null, Shader.TileMode.REPEAT));
            }});
            drawArrow(width / 2, width / 2, width / 4, nextGesture, canvas);
        }

        if (!Debug.IsDebugModeEnabled(getContext())) {
            return;
        }

        ArrayList<ArrayList<PointF>> subStrokes = Gesture.ProcessStroke(points);

        for (ArrayList<PointF> subStroke : subStrokes) {
            for (int i = 0, j = 1; j < subStroke.size(); i++, j++) {

                PointF p = subStroke.get(i);
                PointF q = subStroke.get(j);

                switch (Gesture.GetDirection(subStroke)) {
                    case NORTH:
                        paintSwipes.setColor(Color.argb(153, 0, 0, 255));
                        break;
                    case EAST:
                        paintSwipes.setColor(Color.argb(153, 255, 0, 0));
                        break;
                    case SOUTH:
                        paintSwipes.setColor(Color.argb(153, 0, 255, 0));
                        break;
                    case WEST:
                        paintSwipes.setColor(Color.argb(153, 255, 255, 0));
                        break;
                    default:
                        paintSwipes.setColor(Color.argb(153, 255, 255, 255));
                        break;
                }

                canvas.drawLine(p.x, p.y, q.x, q.y, paintSwipes);
            }
        }

        paintSwipes.setColor(Color.argb(153, 127, 127, 127));

        for (int i = 0; i < points.size(); i++) {
            PointF p = points.get(i);
            canvas.drawCircle(p.x, p.y, 2, paintSwipes);
        }
    }

    private void drawArrow(float cx, float cy, float radius, Gesture gesture, Canvas canvas) {

        Path arrow = new Path();

        arrow.moveTo(cx, cy);

        float oX = 0f;
        float oY = -radius / 3;

        float length = radius / 3;

        Gesture.Direction lastSwipe = null;

        boolean reversed = gesture.getSwipes().size() > 1 && (gesture.equals(gesture.inverseX()) || gesture.equals(gesture.inverseY()));

        for (Gesture.Direction swipe : gesture.getSwipes()) {

            if (lastSwipe != null) {

                if ((lastSwipe == Gesture.Direction.NORTH || lastSwipe == Gesture.Direction.SOUTH) && (swipe == Gesture.Direction.SOUTH || swipe == Gesture.Direction.NORTH)) {
                    arrow.rLineTo(length / 2, 0);
                    oX -= length / 4;
                } else if ((lastSwipe == Gesture.Direction.WEST || lastSwipe == Gesture.Direction.EAST) && (swipe == Gesture.Direction.EAST || swipe == Gesture.Direction.WEST)) {
                    arrow.rLineTo(0, length / 2);
                    oY -= length / 4;
                }

            }

            switch (swipe) {
                case NORTH:
                    arrow.rLineTo(0, -length);
                    if (!reversed) oY += length / 2;
                    break;
                case EAST:
                    arrow.rLineTo(length, 0);
                    if (!reversed) oX -= length / 2;
                    break;
                case SOUTH:
                    arrow.rLineTo(0, length);
                    if (!reversed) oY -= length / 2;
                    break;
                case WEST:
                    arrow.rLineTo(-length, 0);
                    if (!reversed) oX += length / 2;
                    break;
                case TAP:
                    arrow.addCircle(cx, cy, length / 3, Path.Direction.CCW);
                    paintArrow.setStyle(Paint.Style.FILL_AND_STROKE);
            }

            lastSwipe = swipe;
        }

        if (lastSwipe != null) {
            switch (gesture.getSwipes().get(gesture.getSwipes().size() - 1)) {
                case NORTH:
                    arrow.rLineTo(length / 6, 0);
                    arrow.rLineTo(-length / 6, -length / 3);
                    arrow.rLineTo(-length / 6, length / 3);
                    arrow.rLineTo(length / 6, 0);
                    oY += length / 6;
                    break;
                case EAST:
                    arrow.rLineTo(0, length / 6);
                    arrow.rLineTo(length / 3, -length / 6);
                    arrow.rLineTo(-length / 3, -length / 6);
                    arrow.rLineTo(0, length / 6);
                    oX -= length / 6;
                    break;
                case SOUTH:
                    arrow.rLineTo(length / 6, 0);
                    arrow.rLineTo(-length / 6, length / 3);
                    arrow.rLineTo(-length / 6, -length / 3);
                    arrow.rLineTo(length / 6, 0);
                    oY -= length / 6;
                    break;
                case WEST:
                    arrow.rLineTo(0, length / 6);
                    arrow.rLineTo(-length / 3, -length / 6);
                    arrow.rLineTo(length / 3, -length / 6);
                    arrow.rLineTo(0, length / 6);
                    oX += length / 6;
                    break;
            }
        }

        if (reversed) {

            switch (lastSwipe) {
                case NORTH:
                    oY -= length / 4;
                    break;
                case EAST:
                    oX += length / 4;
                    break;
                case SOUTH:
                    oY += length / 4;
                    break;
                case WEST:
                    oX -= length / 4;
                    break;
            }

        }

        arrow.offset(oX, oY);
        canvas.drawPath(arrow, paintArrow);
        paintArrow.setStyle(Paint.Style.STROKE);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDown(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                touchUp(x, y);
                if (Debug.IsDebugModeEnabled(getContext())) {
                    invalidate();
                }
                break;
        }

        return true;
    }

    private boolean outsideBoundaries(float x, float y) {
        return x <= TOUCH_BORDER || x >= width - TOUCH_BORDER || y <= TOUCH_BORDER || y >= height - TOUCH_BORDER;
    }

    private void touchDown(float x, float y) {

        points.clear();

        if (outsideBoundaries(x, y)) {
            return;
        }

        points.add(new PointF(x, y));

        lastX = x;
        lastY = y;

        onGestureBegin();
    }

    private void touchMove(float x, float y) {

        if (outsideBoundaries(x, y)) {
            return;
        }

        float dx = Math.abs(x - lastX);
        float dy = Math.abs(y - lastY);
        float d = (float) Math.sqrt(dx * dx + dy * dy);

        if (d >= MOVE_THRESHOLD) {
            points.add(new PointF(x, y));

            lastX = x;
            lastY = y;
        }
    }

    private void touchUp(float x, float y) {

        Gesture gesture = evaluateGesture();
        onGestureEnd(gesture);
    }

    @Override
    public void setInputReceiver(InputReceiver inputReceiver) {
        this.inputReceiver = inputReceiver;
    }

    @Override
    public void onGestureBegin() {
        if (inputReceiver != null)
            inputReceiver.onGestureBegin();
    }

    @Override
    public void onGestureEnd(Gesture gesture) {
        if (inputReceiver != null)
            inputReceiver.onGestureEnd(gesture);
    }

    public Gesture evaluateGesture() {
        return Gesture.GetGesture(points);
    }

    public void setNextGesture(Gesture gesture) {
        nextGesture = gesture;
        invalidate();
    }

}
