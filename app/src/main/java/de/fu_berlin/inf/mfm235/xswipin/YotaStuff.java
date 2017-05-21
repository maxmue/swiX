package de.fu_berlin.inf.mfm235.xswipin;

import android.util.Log;

import com.yotadevices.sdk.EpdManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class YotaStuff {

    public static class YotaChallengeController implements ChallengeController {

        private static YotaChallengeController instance = new YotaChallengeController();

        public static YotaChallengeController GetInstance() {
            return instance;
        }

        private ArrayList<ChallengeDisplay> challengeDisplays = new ArrayList<>();
        private Runnable onStartAction, onSuccessAction, onFinishAction;

        private Alphabet alphabet;
        private char[] pin;
        private int rounds, roundsCompleted;
        private Gesture nextExpected;
        private Gesture[] expected;
        private Gesture[] received;
        private int cursor;

        private int currentTries;
        private long tDisplay, tStart, tEnd;

        private ArrayList<Integer> tries = new ArrayList<>();
        private ArrayList<Long> prepareTimes = new ArrayList<>();
        private ArrayList<Long> entryTimes = new ArrayList<>();
        ArrayList<ArrayList<Gesture>> expectedGesturesPerRound = new ArrayList<>();
        ArrayList<ArrayList<Gesture>> receivedGesturesPerRound = new ArrayList<>();
        ArrayList<Gesture> expectedGestures;
        ArrayList<Gesture> receivedGestures;

        @Override
        public void setAlphabet(String alphabetString) {
            alphabet = new Alphabet();

            for (String s : alphabetString.split(";")) {
                if (!s.isEmpty()) {
                    alphabet.characters.add(s.charAt(0));
                    alphabet.gestures.add(Gesture.fromString(s.substring(2)));
                }
            }
        }

        @Override
        public void setAlphabet(int numberOfSymbols) {
            Alphabet alphabet = Alphabet.MakeAlphabet(numberOfSymbols);
            setAlphabet(alphabet.toString());
        }

        @Override
        public void setPin(String pin) {
            this.pin = pin.toCharArray();
            expected = new Gesture[pin.length()];
            received = new Gesture[pin.length()];
        }

        @Override
        public void setRounds(int rounds) {
            this.rounds = rounds;
        }

        @Override
        public void addChallengeDisplay(ChallengeDisplay challengeDisplay) {
            challengeDisplays.add(challengeDisplay);
        }

        @Override
        public void setOnStartAction(Runnable onStartAction) {
            this.onStartAction = onStartAction;
        }

        @Override
        public void setOnSuccessAction(Runnable onSuccessAction) {
            this.onSuccessAction = onSuccessAction;
        }

        @Override
        public void setOnFinishAction(Runnable onFinishAction) {
            this.onFinishAction = onFinishAction;
        }

        @Override
        public void startChallenge() {
            startRound();
            updateChallengeDisplays();
        }

        @Override
        public void reset() {
            roundsCompleted = 0;
            cursor = 0;
            currentTries = 1;
            expectedGesturesPerRound.clear();
            receivedGesturesPerRound.clear();
//            expectedGestures.clear();
//            receivedGestures.clear();
        }

        @Override
        public Alphabet getAlphabet() {
            return alphabet;
        }

        @Override
        public String getPin() {
            return String.valueOf(pin);
        }

        @Override
        public void onSuccess() {
            tEnd = System.currentTimeMillis();
            roundsCompleted += 1;

            tries.add(currentTries);
            prepareTimes.add(tStart-tDisplay);
            entryTimes.add(tEnd-tStart);
            expectedGesturesPerRound.add(expectedGestures);
            receivedGesturesPerRound.add(receivedGestures);

            if (roundsCompleted < rounds) {
                startRound();
                onSuccessAction.run();
            }

            else {
                onFinish();
            }

            updateChallengeDisplays();
        }

        @Override
        public void onFailure() {
            currentTries += 1;
            cursor = 0;
            updateChallengeDisplays();
        }

        @Override
        public void onFinish() {
            onFinishAction.run();
        }

        @Override
        public void onProgress(int pinProgress, int pinLength) {
            for (ChallengeDisplay challengeDisplay : challengeDisplays) {
                challengeDisplay.setProgress(pinProgress, pinLength);
            }
        }

        @Override
        public ArrayList<Integer> getTries() {
            return tries;
        }

        @Override
        public ArrayList<Long> getPrepareTimes() {
            return prepareTimes;
        }

        @Override
        public ArrayList<Long> getEntryTimes() {
            return entryTimes;
        }

        @Override
        public ArrayList<ArrayList<Gesture>> getExpectedGesturesPerRound() {
            return expectedGesturesPerRound;
        }

        @Override
        public ArrayList<ArrayList<Gesture>> getReceivedGesturesPerRound() {
            return receivedGesturesPerRound;
        }

        @Override
        public void onGestureBegin() {
            if (tStart == 0) {
                tStart = System.currentTimeMillis();
            }
            nextExpected = alphabet.getGestureFromCharacter(pin[cursor]);
            expectedGestures.add(nextExpected);
            alphabet.shuffleGestures();
            updateChallengeDisplays();
        }

        @Override
        public void onGestureEnd(Gesture gesture) {
            expected[cursor] = nextExpected;
            received[cursor] = gesture;

            cursor += 1;

            receivedGestures.add(gesture);

            updateChallengeDisplays();
            checkSuccess();
        }

        private void startRound() {
            currentTries = 1;
            cursor = 0;
            onStartAction.run();
            tDisplay = System.currentTimeMillis();
            tStart = 0;
            expectedGestures = new ArrayList<>();
            receivedGestures = new ArrayList<>();
        }

        private void checkSuccess() {
            if (cursor < received.length) {
                return;
            }

            int correct = 0;
            int correctInverted = 0;

            for (int i = 0; i < pin.length; i++) {

                if (expected[i].equals(received[i])) {
                    correct += 1;
                }

                if (expected[i].inverseY().equals(received[i])) {
                    correctInverted += 1;
                }
            }

            if (correct == pin.length || correctInverted == pin.length) {
                onSuccess();
            }

            else {
                onFailure();
            }
        }

        private void updateChallengeDisplays() {
            for (ChallengeDisplay challengeDisplay : challengeDisplays) {
                onProgress(cursor, pin.length);
                challengeDisplay.update();
            }
        }
    }

    public static class YotaTrainingController implements InputReceiver {

        private static YotaTrainingController instance = new YotaTrainingController();
        private static ArrayList<TrainingActivity> trainingActivities = new ArrayList<>();

        public static YotaTrainingController GetInstance() {
            return instance;
        }

        public void addTrainingActivity(TrainingActivity trainingActivity) {
            trainingActivities.add(trainingActivity);
        }

        private ArrayList<Gesture> expectedGestures;
        private ArrayList<Gesture> receivedGestures = new ArrayList<>();

        private Iterator<Gesture> iterator;

        private Runnable onFinishAction;

        private long tDisplay, tStart, tEnd;

        private ArrayList<Long> prepareTimes = new ArrayList<>();
        private ArrayList<Long> entryTimes = new ArrayList<>();

        public void setExpectedGestures(ArrayList<Gesture> expectedGestures) {
            this.expectedGestures = expectedGestures;
            iterator = expectedGestures.iterator();
        }

        public void setOnFinishAction(Runnable onFinishAction) {
            this.onFinishAction = onFinishAction;
        }

        public void startTraining() {
            receivedGestures.clear();
            EpdManager.getInstance().unlockEpd();
            onNext();
        }

        public ArrayList<Gesture> getExpectedGestures() {
            return expectedGestures;
        }

        public ArrayList<Gesture> getReceivedGestures() {
            return receivedGestures;
        }

        public ArrayList<Long> getPrepareTimes() {
            return prepareTimes;
        }

        public ArrayList<Long> getEntryTimes() {
            return entryTimes;
        }

        @Override
        public void onGestureBegin() {
            tStart = System.currentTimeMillis();
        }

        @Override
        public void onGestureEnd(Gesture gesture) {
            tEnd = System.currentTimeMillis();

            receivedGestures.add(gesture);
            prepareTimes.add(tStart - tDisplay);
            entryTimes.add(tEnd - tStart);

            if (iterator.hasNext()) {
                onNext();
            }

            else {
                onFinish();
            }
        }

        private void onNext() {
            tDisplay = System.currentTimeMillis();

            Gesture nextGesture = iterator.next();
            for (TrainingActivity trainingActivity : trainingActivities) {
                trainingActivity.setNext(nextGesture);
            }
        }

        private void onFinish() {
            EpdManager.getInstance().lockEpd();
            onFinishAction.run();
        }
    }

    public static class YotaGameController implements InputReceiver {

        private static YotaGameController instance = new YotaGameController();

        public static YotaGameController GetInstance() {
            return instance;
        }

        private Alphabet alphabet = new Alphabet();
        private Alphabet tmpAlphabeth = new Alphabet();
        private Random random = new Random(System.currentTimeMillis());
        private ArrayList<ChallengeDisplay> challengeDisplays = new ArrayList<>();
        private int rounds;

        public Alphabet setAlphabet(String alphabetString) {
            alphabet.gestures.clear();
            alphabet.characters.clear();

            if (alphabetString.isEmpty()) {
                for (int n = 0; n < 10; n++) {
                    alphabet.characters.add(Integer.toString(n).charAt(0));
                }
                for (Gesture g : Gesture.getAllSwipeGestures()) {
                    alphabet.gestures.add(g);
                }
            }

            else {
                for (String s : alphabetString.split(";")) {
                    if (!s.isEmpty()) {
                        alphabet.characters.add(s.charAt(0));
                        alphabet.gestures.add(Gesture.fromString(s.substring(2)));
                    }
                }
            }


            return alphabet;
        }

        private void setTmpAlphabeth(String alphabetString) {
            tmpAlphabeth.gestures.clear();
            tmpAlphabeth.characters.clear();

            for (String s : alphabetString.split(";")) {
                if (!s.isEmpty()) {
                    tmpAlphabeth.characters.add(s.charAt(0));
                    tmpAlphabeth.gestures.add(Gesture.fromString(s.substring(2)));
                }
            }

        }

        private String pin;
        private boolean paused = true;
        private boolean finished = false;

        private ArrayList<Gesture> receivedGestures = new ArrayList<>();
        private ArrayList<Character> receivedCharacters = new ArrayList<>();
        private ArrayList<Character> expectedCharacters = new ArrayList<>();
        private ArrayList<String> alphabets = new ArrayList<>();

        private Runnable onNextRoundAction, onFinishAction;

        public void addDisplay(ChallengeDisplay challengeDisplay) {
            challengeDisplays.add(challengeDisplay);
        }

        public void setOnNextRoundAction(Runnable onNextRoundAction) {
            this.onNextRoundAction = onNextRoundAction;
        }

        public void setOnFinishAction(Runnable onFinishAction) {
            this.onFinishAction = onFinishAction;
        }

        public void startGame(int rounds) {
            this.rounds = rounds;
            receivedGestures.clear();
            receivedCharacters.clear();
            alphabets.clear();
            finished = false;
            nextRound();
        }

        public String nextPin() {
            return pin;
        }

        private void nextRound() {
            pin = Integer.toString(random.nextInt(alphabet.size()));

            paused = true;

            onNextRoundAction.run();
            updateDisplays();
        }

        public ArrayList<Gesture> getReceivedGestures() {
            return receivedGestures;
        }

        public ArrayList<Character> getReceivedCharacters() {
            return receivedCharacters;
        }

        public ArrayList<Character> getExpectedCharacters() {
            return expectedCharacters;
        }

        public ArrayList<String> getAlphabets() { return alphabets; }

        @Override
        public void onGestureBegin() {
            if (!paused) {
                setTmpAlphabeth(alphabet.toString());
                alphabets.add(alphabet.toString());
                alphabet.shuffleGestures();
                updateDisplays();
            }
        }

        @Override
        public void onGestureEnd(Gesture gesture) {
            if (finished) {
                EpdManager.getInstance().lockEpd();
                onFinishAction.run();
            }

            if (paused) {
                paused = false;
                updateDisplays();
                return;
            }

            gesture.setInvertable(false);
            gesture = gesture.inverseY();
            
            receivedGestures.add(gesture);
            receivedCharacters.add(tmpAlphabeth.getCharacterFromGesture(gesture));
            expectedCharacters.add(pin.charAt(0));

            if (--rounds > 0) {
                nextRound();
            }

            else {
                paused = true;
                finished = true;
                updateDisplays();
            }
        }

        private void updateDisplays() {
            for (ChallengeDisplay challengeDisplay : challengeDisplays) {
                challengeDisplay.showArrows(!paused);
                challengeDisplay.update();
            }
        }
    }

}
