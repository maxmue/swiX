package de.fu_berlin.inf.mfm235.xswipin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class TrainingActivity extends Activity {

    YotaStuff.YotaTrainingController trainingController = YotaStuff.YotaTrainingController.GetInstance();
    private TrainingPad trainingPad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        trainingController.addTrainingActivity(this);

        BackActivity.inputReceiver = trainingController;

        trainingPad = new TrainingPad(this);
        //trainingPad.setInputReceiver(trainingController);
        setContentView(trainingPad);

        ArrayList<Gesture> expectedGestures = new ArrayList<>();

        expectedGestures.addAll(Gesture.getAllSwipeGestures());
        expectedGestures.addAll(Gesture.getAllSwipeGestures());

        boolean sufficientlyShuffled;
        do {
            Collections.shuffle(expectedGestures);
            sufficientlyShuffled = true;
            for (int i = 0, j = 1; j < expectedGestures.size(); i++, j++) {
                if (expectedGestures.get(i).equals(expectedGestures.get(j))) {
                    sufficientlyShuffled = false;
                    break;
                }
            }
        } while (!sufficientlyShuffled);

        trainingController.setExpectedGestures(expectedGestures);
        trainingController.setOnFinishAction(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), TrainingPostActivity.class));
            }
        });
        trainingController.startTraining();
    }

    public void setNext(Gesture gesture) {
        trainingPad.setNextGesture(gesture);
    }
}
