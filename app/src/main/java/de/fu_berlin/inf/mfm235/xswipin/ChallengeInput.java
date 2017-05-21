package de.fu_berlin.inf.mfm235.xswipin;

public interface ChallengeInput {

    void setInputReceiver(InputReceiver inputReceiver);

    void onGestureBegin();
    void onGestureEnd(Gesture gesture);

}
