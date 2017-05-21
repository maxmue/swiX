package de.fu_berlin.inf.mfm235.xswipin;

public interface InputReceiver {

    void onGestureBegin();
    void onGestureEnd(final Gesture gesture);

}
