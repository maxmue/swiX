package de.fu_berlin.inf.mfm235.xswipin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Shader;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class GesturePad extends View implements ChallengeDisplay, ChallengeInput {

    private Alphabet alphabet;
    private InputReceiver inputReceiver;

    private int pinLength;
    private int pinProgress;

    private final static float TOUCH_BORDER = 50;
    private final static float MOVE_THRESHOLD = 50;

    private float width, height, lastX, lastY;

    private ArrayList<PointF> points = new ArrayList<>();

    private int milkPercent = 100;
    private boolean arrowsVisible = true;

    private Paint paintButtons = new Paint() {{
        //setColor(Color.argb(255, 255, 255, 255));
        setAntiAlias(true);
    }};

    private Paint paintText = new Paint() {{
        setColor(Color.argb(255, 0, 0, 0));
        setTextAlign(Align.CENTER);
        //setStyle(Style.STROKE);
        //setStrokeWidth(8);
    }};

    private Paint paintArrow = new Paint() {{
        setColor(Color.argb(255, 0, 0, 0));
        setStyle(Style.STROKE);
        setStrokeWidth(12);
    }};

    private Paint paintSwipes = new Paint() {{
        setStrokeWidth(4);
        setAntiAlias(true);
    }};

    public GesturePad(Context context) {
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

        int nButtons = alphabet.size();

        int cols;
        int rows;

        switch (nButtons) {
            case 1:
            case 4:
            case 9:
                cols = rows = (int)Math.sqrt(nButtons);
                break;
            case 2:
                cols = 2;
                rows = 1;
                break;
            case 8:
                cols = 2;
                rows = 4;
                break;
            case 10:
                cols = 3;
                rows = 4;
                break;
            default:
                return;
        }

        final float radius = Math.min(width/(2*cols), height/(2*rows));
        paintButtons.setShader(new LinearGradient(0, 0, radius, radius, new int[] { Color.WHITE, Color.LTGRAY, Color.WHITE }, null, Shader.TileMode.REPEAT));
        paintText.setTextSize(radius/2);

        for (int i = 0; i < pinProgress; i++) {
            float x = radius/20 + (i+0.5f)*(width - radius/10) / pinLength;
            float y = radius/2;
            canvas.drawCircle(x, y, radius/4, new Paint() {{
                setColor(Color.argb((int)(2.55*milkPercent), 255, 255, 255));
                setStyle(Style.FILL);
            }});
        }

        for (int i = 0; i < pinLength; i++) {
            canvas.drawCircle(radius/20 + (i+0.5f)*(width - radius/10) / pinLength, radius/2, radius/4, new Paint() {{
                setColor(Color.WHITE);
                setStyle(Style.STROKE);
                setStrokeWidth(8);
            }});
        }

        rowLoop:for (int row = 0, i = 0; row < rows; row++) {
            for (int column = 0; column < cols; column++, i++) {

                if (nButtons == 10 && row == 3) {
                    column = 1;
                }

                float x0 = radius + 2*column*radius;
                float y0 = radius + 2*row*radius + radius;

                canvas.drawCircle(x0, y0, radius*0.9f, paintButtons);
                canvas.drawText(alphabet.characters.get((i+1)%alphabet.characters.size()).toString(), x0, y0 + radius/4 - ((paintText.descent() + paintText.ascent()) / 2), paintText);
                //canvas.drawText(alphabet.gestures.get((i+1)%alphabet.characters.size()).toString(), x0, y0 + radius/4 - ((paintText.descent() + paintText.ascent()) / 2), paintText);
                if (arrowsVisible)
                    drawArrow(x0, y0, radius, alphabet.gestures.get((i+1)%alphabet.characters.size()), canvas);

                if (nButtons == 10 && row == 3) {
                    break rowLoop;
                }
            }
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
            canvas.drawCircle(p.x, p.y, 10, paintSwipes);
        }
    }

    private void drawArrow(float cx, float cy, float radius, Gesture gesture, Canvas canvas) {

        Path arrow = new Path();

        arrow.moveTo(cx, cy);

        float oX = 0f;
        float oY = -radius/3;

        float length = radius/3;

        Gesture.Direction lastSwipe = null;

        boolean reversed = gesture.getSwipes().size() > 1 && (gesture.equals(gesture.inverseX()) || gesture.equals(gesture.inverseY()));

        for (Gesture.Direction swipe : gesture.getSwipes()) {

            if (lastSwipe != null) {

                if ((lastSwipe == Gesture.Direction.NORTH || lastSwipe == Gesture.Direction.SOUTH) && (swipe == Gesture.Direction.SOUTH || swipe == Gesture.Direction.NORTH)) {
                    arrow.rLineTo(length/2, 0);
                    oX -= length/4;
                }

                else if ((lastSwipe == Gesture.Direction.WEST || lastSwipe == Gesture.Direction.EAST) && (swipe == Gesture.Direction.EAST || swipe == Gesture.Direction.WEST)) {
                    arrow.rLineTo(0, length/2);
                    oY -= length/4;
                }

            }

            switch (swipe) {
                case NORTH:
                    arrow.rLineTo(0, -length);
                    if (!reversed) oY += length/2;
                    break;
                case EAST:
                    arrow.rLineTo(length, 0);
                    if (!reversed) oX -= length/2;
                    break;
                case SOUTH:
                    arrow.rLineTo(0, length);
                    if (!reversed) oY -= length/2;
                    break;
                case WEST:
                    arrow.rLineTo(-length, 0);
                    if (!reversed) oX += length/2;
                    break;
                case TAP:
                    arrow.addCircle(cx, cy, length/3, Path.Direction.CCW);
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
                break;
        }

        invalidate();

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
        float d = (float)Math.sqrt(dx*dx + dy*dy);

        if (d >= MOVE_THRESHOLD) {
            points.add(new PointF(x, y));

            lastX = x;
            lastY = y;
        }
    }

    private void touchUp(float x, float y) {

        if (!outsideBoundaries(x, y)) {
            points.add(new PointF(x, y));
        }

        Gesture gesture = evaluateGesture();
        onGestureEnd(gesture);
    }

    @Override
    public void setAlphabet(Alphabet alphabet) {
        this.alphabet = alphabet;
    }

    public Gesture evaluateGesture() {
        return Gesture.GetGesture(points);
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

    @Override
    public void setProgress(int pinProgress, int pinLength) {
        this.pinProgress = pinProgress;
        this.pinLength = pinLength;
        invalidate();
    }

    public void setMilkPercent(int milkPercent) {
        this.milkPercent = milkPercent;
        invalidate();
    }

    @Override
    public void update() {
        postInvalidate();
    }

    @Override
    public void showArrows(boolean show) {
        arrowsVisible = show;
        postInvalidate();
    }
}
