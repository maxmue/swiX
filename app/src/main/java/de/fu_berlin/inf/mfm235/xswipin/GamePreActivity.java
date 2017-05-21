package de.fu_berlin.inf.mfm235.xswipin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.yotadevices.sdk.EpdIntentCompat;
import com.yotadevices.sdk.EpdManager;

public class GamePreActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_pre);
    }

    protected void doNS(View view) {
        startGame("1:N;0:S", 10);
    }

    protected void doEW(View view) {
        startGame("1:E;0:W", 10);
    }

    protected void doComplex(View view) {
        startGame("3:N;0:E;1:S;2:W", 5);
    }

    protected void doTenOnTen(View view) {
        startGame("", 5);
    }

    private void startGame(String alphabet, int rounds) {
        Intent intent;

        intent = new Intent(this, GameActivity.class);
        intent.putExtra("alphabet", alphabet);
        intent.putExtra("rounds", rounds);
        EpdIntentCompat.addEpdFlags(intent, EpdIntentCompat.FLAG_ACTIVITY_KEEP_ON_FRONT_SCREEN);
        startActivity(intent);

        intent = new Intent(this, BackActivity.class);
        EpdIntentCompat.setEpdFlags(intent, EpdIntentCompat.FLAG_ACTIVITY_KEEP_ON_EPD_SCREEN);
        startActivity(intent);

        EpdManager.getInstance().unlockEpd();
    }
}
