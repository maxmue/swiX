package de.fu_berlin.inf.mfm235.xswipin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TrainingPostActivity extends Activity {

    private TextView textViewResult;
    private TextView textViewTimes;

    private ArrayList<Gesture> expectedGestures;
    private ArrayList<Gesture> receivedGestures;

    private ArrayList<Long> prepareTimes;
    private ArrayList<Long> entryTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_post);
        textViewResult = (TextView) findViewById(R.id.textViewResult);
        textViewTimes = (TextView) findViewById(R.id.textViewTimes);

        YotaStuff.YotaTrainingController trainingController = YotaStuff.YotaTrainingController.GetInstance();

        expectedGestures = trainingController.getExpectedGestures();
        receivedGestures = trainingController.getReceivedGestures();
        prepareTimes = trainingController.getPrepareTimes();
        entryTimes = trainingController.getEntryTimes();

        doAnalysis();
    }

    private void doAnalysis() {

        int correct = 0;

        for (int i = 0; i < expectedGestures.size(); i++) {

            Gesture expected = expectedGestures.get(i);
            Gesture received = receivedGestures.get(i);

            if (expected.equals(received) || received.isInvertable() && expected.equals(received.inverseY())) {
                correct++;
            }
        }

        textViewResult.setText("count = " + expectedGestures.size() + ", correct = " + correct);

        long averagePrepareTime = 0, averageEntryTime = 0;

        for (int i = 0; i < prepareTimes.size(); i++) {
            averagePrepareTime += prepareTimes.get(i);
            averageEntryTime += entryTimes.get(i);
        }

        averagePrepareTime /= prepareTimes.size();
        averageEntryTime /= entryTimes.size();

        textViewTimes.setText("average prepare time = " + averagePrepareTime + "\naverage entry time = " + averageEntryTime);
    }

    protected void doSave(View view) {
        String name = MainActivity.GetName();

        try {
            String dateTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            File root = new File(Environment.getExternalStorageDirectory().toString() + "/XSwiPIN");
            File logfile = new File(root, "XSwiPIN_training_" + name + "_" + dateTime + ".log");
            FileWriter fileWriter = new FileWriter(logfile);

            fileWriter.write("date = " + dateTime + "\n\n");
            fileWriter.write("name = " + name + "\n\n");

            fileWriter.write("expectedGesture; receivedGesture; prepareTime; entryTime; totalTime; correct\n");

            for (int i = 0; i < expectedGestures.size(); i++) {
                fileWriter.write(expectedGestures.get(i).string() + "; ");
                fileWriter.write(receivedGestures.get(i).toString() + "; ");
                fileWriter.write(prepareTimes.get(i).toString() + "; ");
                fileWriter.write(entryTimes.get(i).toString() + "; ");
                fileWriter.write((prepareTimes.get(i)+entryTimes.get(i)) + "; ");
                fileWriter.write((expectedGestures.get(i).weakEquals(receivedGestures.get(i)) ? "1" : "0") + "\n");
            }

            fileWriter.flush();
            fileWriter.close();
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void doShowInput(View view) {
        Intent intent = new Intent(this, GestureViewActivity.class);
        intent.putExtra("expectedGestures", Gesture.ToString(expectedGestures));
        intent.putExtra("receivedGestures", Gesture.ToString(receivedGestures));
        startActivity(intent);
    }
}