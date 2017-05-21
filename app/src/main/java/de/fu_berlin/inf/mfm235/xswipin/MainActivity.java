package de.fu_berlin.inf.mfm235.xswipin;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.yotadevices.sdk.EpdIntentCompat;

public class MainActivity extends Activity {

    private Switch switchDebugMode;
    private static String name;

    public static String GetName() {
        return name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        switchDebugMode = (Switch) findViewById(R.id.switchDebugMode);
        switchDebugMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("debugMode", checked);
                editor.commit();
                Debug.Invalidate();
            }
        });

        switchDebugMode.setChecked(Debug.IsDebugModeEnabled(this));

        KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Activity.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        lock.disableKeyguard();
    }

    protected void doSetName(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Name");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                name = input.getText().toString();
                if (name != null) {
                    ((TextView) findViewById(R.id.buttonN)).setText(name);
                    findViewById(R.id.buttonT).setEnabled(true);
                    findViewById(R.id.buttonC).setEnabled(true);
                    findViewById(R.id.buttonG).setEnabled(true);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    protected void doTraining(View view) {
        Intent intent;

        intent = new Intent(this, TrainingActivity.class);
        EpdIntentCompat.setEpdFlags(intent, EpdIntentCompat.FLAG_ACTIVITY_KEEP_ON_FRONT_SCREEN);
        startActivity(intent);

        intent = new Intent(this, BackActivity.class);
        EpdIntentCompat.setEpdFlags(intent, EpdIntentCompat.FLAG_ACTIVITY_KEEP_ON_EPD_SCREEN);
        startActivity(intent);
    }

    protected void doChallenge(View view) {
        Intent intent;

        intent = new Intent(this, ChallengePreActivity.class);
        EpdIntentCompat.setEpdFlags(intent, EpdIntentCompat.FLAG_ACTIVITY_KEEP_ON_FRONT_SCREEN);
        startActivity(intent);

        intent = new Intent(this, BackActivity.class);
        EpdIntentCompat.setEpdFlags(intent, EpdIntentCompat.FLAG_ACTIVITY_KEEP_ON_EPD_SCREEN);
        startActivity(intent);
    }

    protected void doGame(View view) {
        startActivity(new Intent(this, GamePreActivity.class));
    }
}
