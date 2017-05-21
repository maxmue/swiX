package de.fu_berlin.inf.mfm235.xswipin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class ChallengePostActivity extends Activity {

    private String pin;
    private int numberOfButtons;
    private ArrayList<Long> prepareTimes, entryTimes, numberOfTries;

    //private ArrayList<Gesture> expectedGestures = new ArrayList<>();
    //private ArrayList<Gesture> receivedGestures = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_post);

        Intent intent = getIntent();

        numberOfButtons = intent.getIntExtra("numberOfButtons", 0);
        pin = intent.getStringExtra("pin");
        prepareTimes = MatzUtils.LongsFromString(intent.getStringExtra("prepareTimes"));
        entryTimes = MatzUtils.LongsFromString(intent.getStringExtra("entryTimes"));
        numberOfTries = MatzUtils.LongsFromString(intent.getStringExtra("numberOfTries"));

        ((Button) findViewById(R.id.button)).setText(prepareTimes.size() < 10 ? "next round" : "show results");
    }

    protected void onButton(View view) {

        Intent intent;

        if (prepareTimes.size() < 10) {
            intent = new Intent(this, ChallengeActivity.class);
        }

        else {
            intent = new Intent(this, ChallengeFinallyActivity.class);
        }

        intent.putExtra("pin", pin);
        intent.putExtra("numberOfButtons", numberOfButtons);
        intent.putExtra("prepareTimes", MatzUtils.ToString(prepareTimes));
        intent.putExtra("entryTimes", MatzUtils.ToString(entryTimes));
        intent.putExtra("numberOfTries", MatzUtils.ToString(numberOfTries));

        startActivity(intent);
    }

}
