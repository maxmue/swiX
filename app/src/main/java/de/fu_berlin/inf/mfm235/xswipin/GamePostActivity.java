package de.fu_berlin.inf.mfm235.xswipin;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import com.yotadevices.sdk.EpdManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class GamePostActivity extends Activity {

    private YotaStuff.YotaGameController gameController = YotaStuff.YotaGameController.GetInstance();

    private ArrayList<Gesture> receivedGestures;
    private ArrayList<Character> receivedCharacters;
    private ArrayList<String> alphabets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_post);

        EpdManager.getInstance().lockEpd();

        receivedGestures = gameController.getReceivedGestures();
        receivedCharacters = gameController.getReceivedCharacters();
        alphabets = gameController.getAlphabets();

        StringBuilder stringBuilder = new StringBuilder();

        /*for (Gesture gesture : receivedGestures) stringBuilder.append(gesture.string() + " ");
        ((TextView)findViewById(R.id.textViewGestures)).setText(stringBuilder.toString());

        stringBuilder.setLength(0);

        for (Character character : receivedCharacters) stringBuilder.append(character + " ");
        ((TextView)findViewById(R.id.textViewCharacters)).setText(stringBuilder.toString());*/

        /*stringBuilder.setLength(0);

        for (Character character : expectedCharacters) stringBuilder.append(character + ";");
        ((TextView)findViewById(R.id.textViewExpected)).setText(stringBuilder.toString());*/
    }

    protected void doSave(View view) {
        String name = MainActivity.GetName();

        try {
            String dateTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            File root = new File(Environment.getExternalStorageDirectory().toString() + "/XSwiPIN");
            File logfile = new File(root, "XSwiPIN_game_" + name + "_" + dateTime + ".log");
            FileWriter fileWriter = new FileWriter(logfile);

            fileWriter.write("date = " + dateTime + "\n\n");
            fileWriter.write("name = " + name + "\n\n");

            fileWriter.write("receivedGesture; receivedCharacter; alphabet\n");

            for (int i = 0; i < receivedGestures.size(); i++) {
                fileWriter.write(receivedGestures.get(i) + "; ");
                fileWriter.write(receivedCharacters.get(i) + "; ");
                fileWriter.write(alphabets.get(i) + "\n ");
            }

            fileWriter.flush();
            fileWriter.close();
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
