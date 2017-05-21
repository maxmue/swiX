package de.fu_berlin.inf.mfm235.xswipin;

import java.util.ArrayList;

public interface ChallengeController extends InputReceiver {

    void setAlphabet(String alphabetString);
    void setAlphabet(int numberOfSymbols);
    void setPin(String pin);
    void setRounds(int rounds);
    void addChallengeDisplay(ChallengeDisplay challengeDisplay);
    void setOnStartAction(Runnable onStartAction);
    void setOnSuccessAction(Runnable onSuccessAction);
    void setOnFinishAction(Runnable onFinishAction);
    void startChallenge();
    void reset();

    Alphabet getAlphabet();
    String getPin();

    void onSuccess();
    void onFailure();
    void onFinish();
    void onProgress(int pinProgress, int pinLength);

    ArrayList<Integer> getTries();
    ArrayList<Long> getPrepareTimes();
    ArrayList<Long> getEntryTimes();
    ArrayList<ArrayList<Gesture>> getExpectedGesturesPerRound();
    ArrayList<ArrayList<Gesture>> getReceivedGesturesPerRound();
}
