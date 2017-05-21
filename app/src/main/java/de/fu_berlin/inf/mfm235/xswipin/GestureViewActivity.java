package de.fu_berlin.inf.mfm235.xswipin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class GestureViewActivity extends Activity {

    private GestureView gestureView;

    private ArrayList<Gesture> expectedGestures;
    private ArrayList<Gesture> receivedGestures;

    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_view);
        gestureView = (GestureView) findViewById(R.id.gestureView);
        expectedGestures = Gesture.FromString(getIntent().getStringExtra("expectedGestures"));
        receivedGestures = Gesture.FromString(getIntent().getStringExtra("receivedGestures"));

        gestureView.displayGesture(receivedGestures.get(index), expectedGestures.get(index));
    }

    protected void doShowNext(View view) {
        index += 1;
        index %= receivedGestures.size();
        gestureView.displayGesture(receivedGestures.get(index), expectedGestures.get(index));
    }

    protected void doShowPrev(View view) {
        index -= 1;
        index %= receivedGestures.size();
        if (index < 0) index += receivedGestures.size();
        gestureView.displayGesture(receivedGestures.get(index), expectedGestures.get(index));
    }

    protected void doSave(View view) {

        try {
            String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
            File root = new File(Environment.getExternalStorageDirectory().toString());
            File logfile = new File(root, "XSwiPIN training " + dateTime + ".log");
            FileWriter fileWriter = new FileWriter(logfile);

            fileWriter.write("date  = " + dateTime + "\n\n");

            for (int i = 0; i < expectedGestures.size(); i++) {
                fileWriter.write("expected = " + expectedGestures.get(i) + "\n");
                fileWriter.write("received = " + receivedGestures.get(i) + "\n\n");
            }

            fileWriter.flush();
            fileWriter.close();
        }

        catch (IOException e) {
            e.printStackTrace();
        }

    }

}
