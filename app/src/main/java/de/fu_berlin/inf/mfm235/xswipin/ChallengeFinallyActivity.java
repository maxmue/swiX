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

public class ChallengeFinallyActivity extends Activity {

    private ChallengeController challengeController = YotaStuff.YotaChallengeController.GetInstance();

    private ArrayList<Integer> tries;
    private ArrayList<Long> prepareTimes, entryTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_finally);

        tries = challengeController.getTries();
        prepareTimes = challengeController.getPrepareTimes();
        entryTimes = challengeController.getEntryTimes();

        int n = tries.size();
        double avgTries = 0, avgPrepareTime = 0, avgEntryTime = 0;
        for (Integer t : tries) avgTries += t;
        for (Long p : prepareTimes) avgPrepareTime += p;
        for (Long e : entryTimes) avgEntryTime += e;
        avgTries /= n;
        avgPrepareTime /= n;
        avgEntryTime /= n;

        ((TextView) findViewById(R.id.textViewTries)).setText("avgTries = " + avgTries);
        ((TextView) findViewById(R.id.textViewPrepare)).setText("avgPrepareTime = " + avgPrepareTime);
        ((TextView) findViewById(R.id.textViewEntry)).setText("avgEntryTime = " + avgEntryTime);
        ((TextView) findViewById(R.id.textViewTotal)).setText("avgTotalTime = " + (avgPrepareTime+avgEntryTime));
    }

    protected void doSave(View view) {
        String name = MainActivity.GetName();

        try {
            String dateTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            File root = new File(Environment.getExternalStorageDirectory().toString() + "/XSwiPIN");
            File logfile = new File(root, "XSwiPIN_authentication_" + name + "_" + dateTime + ".log");
            FileWriter fileWriter = new FileWriter(logfile);

            fileWriter.write("date = " + dateTime + "\n\n");
            fileWriter.write("name = " + name + "\n\n");
            fileWriter.write("pin  = " + challengeController.getPin() + "\n\n");
            fileWriter.write("#sym = " + challengeController.getAlphabet().size() + "\n\n");

            fileWriter.write("prepareTime; entryTime; totalTime; tries\n");

            for (int i = 0; i < prepareTimes.size(); i++) {
                fileWriter.write(prepareTimes.get(i) + "; ");
                fileWriter.write(entryTimes.get(i) + "; ");
                fileWriter.write((prepareTimes.get(i)+entryTimes.get(i)) + "; ");
                fileWriter.write(tries.get(i) + "\n");
            }

            fileWriter.write("\n");

            ArrayList<ArrayList<Gesture>> expectedGesturesPerRound = challengeController.getExpectedGesturesPerRound();
            ArrayList<ArrayList<Gesture>> receivedGesturesPerRound = challengeController.getReceivedGesturesPerRound();

            for (int i = 0; i < expectedGesturesPerRound.size(); i++) {
                fileWriter.write("expected = ");
                for (Gesture gesture : expectedGesturesPerRound.get(i)) {
                    fileWriter.write(gesture.string() + "; ");
                }
                fileWriter.write("\nreceived = ");
                for (Gesture gesture : receivedGesturesPerRound.get(i)) {
                    fileWriter.write(gesture.toString() + "; ");
                }
                fileWriter.write("\n\n");
            }

            fileWriter.flush();
            fileWriter.close();
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
