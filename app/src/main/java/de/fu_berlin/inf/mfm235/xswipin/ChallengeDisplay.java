package de.fu_berlin.inf.mfm235.xswipin;

public interface ChallengeDisplay  {

    void setAlphabet(Alphabet alphabet);
    void setProgress(int pinProgress, int pinLength);
    void setMilkPercent(int milkPercent);
    void update();
    void showArrows(boolean show);

}
