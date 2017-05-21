package de.fu_berlin.inf.mfm235.xswipin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.yotadevices.sdk.EpdManager;

public class ChallengeActivity extends Activity {
    private Alphabet alphabet;
    private ChallengeController challengeController = YotaStuff.YotaChallengeController.GetInstance();

    private GesturePad gesturePad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        alphabet = challengeController.getAlphabet();
        alphabet.shuffleGestures();

        gesturePad = new GesturePad(this);
        gesturePad.setAlphabet(alphabet);
        //gesturePad.setInputReceiver(challengeController);
        setContentView(gesturePad);

        BackActivity.inputReceiver = challengeController;

        challengeController.addChallengeDisplay(gesturePad);
        challengeController.setOnStartAction(new Runnable() {
            @Override
            public void run() {
                EpdManager.getInstance().unlockEpd();
            }
        });
        challengeController.setOnSuccessAction(new Runnable() {
            @Override
            public void run() {
                EpdManager.getInstance().lockEpd();
                startActivity(new Intent(ChallengeActivity.this, ChallengePostActivity.class));
            }
        });
        challengeController.setOnFinishAction(new Runnable() {
            @Override
            public void run() {
                EpdManager.getInstance().lockEpd();
                startActivity(new Intent(ChallengeActivity.this, ChallengeFinallyActivity.class));
            }
        });
        challengeController.startChallenge();
    }

}
