package de.fu_berlin.inf.mfm235.xswipin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.yotadevices.sdk.Epd;
import com.yotadevices.sdk.EpdCallbacks;
import com.yotadevices.sdk.EpdIntentCompat;
import com.yotadevices.sdk.EpdManager;

import java.util.ArrayList;

public class GameActivity extends Activity {

    private YotaStuff.YotaGameController gameController = YotaStuff.YotaGameController.GetInstance();
    private Alphabet alphabet;
    private int rounds;
    private GesturePad gesturePad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        alphabet = gameController.setAlphabet(getIntent().getStringExtra("alphabet"));
        rounds = getIntent().getIntExtra("rounds", 0);

        gesturePad = new GesturePad(this);
        gesturePad.setInputReceiver(gameController);
        setContentView(gesturePad);

        gesturePad.setAlphabet(alphabet);

        BackActivity.inputReceiver = gameController;

        gameController.addDisplay(gesturePad);
        gameController.setOnNextRoundAction(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(GameActivity.this, gameController.nextPin(), Toast.LENGTH_SHORT).show();
            }
        });
        gameController.setOnFinishAction(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(GameActivity.this, GamePostActivity.class);
                startActivity(intent);
            }
        });
        gameController.startGame(rounds);

        EpdManager.getInstance().unlockEpd();
    }
}
