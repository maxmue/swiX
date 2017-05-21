package de.fu_berlin.inf.mfm235.xswipin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

public class ChallengePreActivity extends Activity {

    private SharedPreferences preferences;
    private SharedPreferences.Editor prefEdit;

    private int pinLength;
    private int numberOfButtons;
    private String pin;

    private ChallengeController challengeController = YotaStuff.YotaChallengeController.GetInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_pre);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        prefEdit = preferences.edit();

        pinLength = preferences.getInt("pinLength", 4);
        numberOfButtons = preferences.getInt("numberOfButtons", 10);
        pin = preferences.getString("pin", "****");
        ((TextView)findViewById(R.id.textViewPIN)).setText(pin);

        challengeController.setAlphabet(numberOfButtons);
        challengeController.setPin(pin);
        challengeController.setRounds(5);
    }

    protected void onButtonReload(View view) {
        generateRandomPin();
    }

    protected void onButtonLonger(View view) {
        prefEdit.putInt("pinLength", ++pinLength);
        prefEdit.commit();
        generateRandomPin();
    }

    protected void onButtonShorter(View view) {
        prefEdit.putInt("pinLength", --pinLength);
        prefEdit.commit();
        generateRandomPin();
    }

    protected void onButtonAlphabet(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final String[] choices = new String[] { "2", "4", "8", "9", "10" };

        builder.setTitle("Choose number of buttons");
        builder.setItems(choices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                numberOfButtons = Integer.valueOf(choices[i]);
                challengeController.setAlphabet(numberOfButtons);
                prefEdit.putInt("numberOfButtons", numberOfButtons);
                prefEdit.commit();
                generateRandomPin();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    protected void onButtonStart(View view) {

        Intent intent = new Intent(this, ChallengeActivity.class);
        intent.putExtra("numberOfButtons", numberOfButtons);
        intent.putExtra("pin", pin);

        challengeController.reset();

        startActivity(intent);
    }

    private void generateRandomPin() {

        StringBuilder builder = new StringBuilder();

        Random random = new Random(System.nanoTime());

        for (int i = 0; i < pinLength; i++) {
            builder.append(Integer.toString(random.nextInt(numberOfButtons)));
        }

        pin = builder.toString();

        prefEdit.putString("pin", pin);
        prefEdit.commit();

        ((TextView)findViewById(R.id.textViewPIN)).setText(pin);

        challengeController.setPin(pin);
    }
}
