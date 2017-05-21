package de.fu_berlin.inf.mfm235.xswipin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class GestureView extends View {

    private Gesture expectedGesture;
    private Gesture receivedGesture;

    private Paint paintSwipes = new Paint() {{
        setStrokeWidth(10);
        setAntiAlias(true);
    }};

    public GestureView(Context context) {
        super(context);
    }

    public GestureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GestureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void displayGesture(Gesture receivedGesture, Gesture expectedGesture) {
        this.expectedGesture = expectedGesture;
        this.receivedGesture = receivedGesture;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawRect(0, 0, getWidth(), getHeight(), new Paint() {{
            setColor(Color.BLACK);
        }});

        if (receivedGesture == null) {
            return;
        }

        canvas.drawText("expected = " + expectedGesture.string() + ", received = " + receivedGesture.string(), 25, 50, new Paint() {{
            if (expectedGesture.equals(receivedGesture))
                setColor(Color.GREEN);
            else if (expectedGesture.equals(receivedGesture.inverseY()))
                setColor(Color.YELLOW);
            else
                setColor(Color.RED);
            setTextSize(40);
        }});

        ArrayList<PointF> points = receivedGesture.getPoints();

        if (points == null) {
            return;
        }

        // todo: translate, scale, analyse

        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;

        for (PointF p : points) {
            minX = Math.min(p.x, minX);
            minY = Math.min(p.y, minY);
            maxX = Math.max(p.x, maxX);
            maxY = Math.max(p.y, maxY);
        }

        minX -= 50;
        minY -= 50;

        /* translate */
        for (PointF p : points) {
            p.x -= minX;
            p.y -= minY;
        }

        maxX -= minX - 50;
        maxY -= minY - 50;

        float factor = Math.min(getWidth() / maxX, getHeight() / maxY);

        /* scale */
        for (PointF p : points) {
            p.x *= factor;
            p.y *= factor;
        }

        ArrayList<ArrayList<PointF>> subStrokes = Gesture.ProcessStroke(points);

        for (ArrayList<PointF> subStroke : subStrokes) {
            for (int i = 0, j = 1; j < subStroke.size(); i++, j++) {

                PointF p = subStroke.get(i);
                PointF q = subStroke.get(j);

                switch (Gesture.GetDirection(subStroke, 1.2)) {
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

        for (int i = 0; i < points.size(); i++) {

            PointF p = points.get(i);

            canvas.drawCircle(p.x, p.y, 10, new Paint() {{
                setColor(Color.argb(153, 255, 255, 255));
            }});

        }

    }
}
