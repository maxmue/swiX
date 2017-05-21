package de.fu_berlin.inf.mfm235.xswipin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class BackPad extends View implements ChallengeInput {

    private static float[] borders = new float[4];// left, top, right, bottom
    private final static float MOVE_THRESHOLD = 5;

    private float width, height, lastX, lastY;

    private ArrayList<PointF> points = new ArrayList<>();

    private Paint paintSwipes = new Paint() {{
        setStrokeWidth(2);
        setAntiAlias(true);
    }};

    private Paint paintBackground = new Paint(){{
        setColor(Color.WHITE);
    }};

    private Timer timer;
    private Handler handler;
    private boolean timerRunning;
    private boolean begun;

    public BackPad(Context context) {
        super(context);
        setBackgroundColor(Color.BLACK);

        handler = new Handler() {
            public void handleMessage(Message message) {
                onGestureBegin();
                begun = true;
            }
        };
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = w;
        height = h;

        borders[0] = 100;
        borders[1] = 100;
        borders[2] = 100;
        borders[3] = height/2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(borders[0], borders[1], width-borders[2], height-borders[3], paintBackground);

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
        return x <= borders[0] || x >= width-borders[2] || y <= borders[1] || y >= height-borders[3];
    }

    private void touchDown(float x, float y) {

        points.clear();

        if (outsideBoundaries(x, y)) {
            return;
        }

        points.add(new PointF(x, y));

        lastX = x;
        lastY = y;

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timerRunning = false;
                handler.obtainMessage(1).sendToTarget();
            }
        }, 150);
        timerRunning = true;
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

        if (timerRunning) {
            timer.cancel();
            return;
        }

        if (!begun) return;

        Gesture gesture = evaluateGesture();

        if (gesture != null) {
            gesture.setInvertable(true);
        }

        onGestureEnd(gesture);
    }

    @Override
    public void setInputReceiver(InputReceiver inputReceiver) {
    }

    @Override
    public void onGestureBegin() {
        BackActivity.inputReceiver.onGestureBegin();
    }

    @Override
    public void onGestureEnd(Gesture gesture) {
        BackActivity.inputReceiver.onGestureEnd(gesture);
    }

    public Gesture evaluateGesture() {
        return Gesture.GetGesture(points);
    }

}
