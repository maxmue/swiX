package de.fu_berlin.inf.mfm235.xswipin;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class BluetoothChallengeInputStub extends Thread implements ChallengeInput {

    public static void RequestBluetoothInput(InputReceiver inputReceiver) {

        if (BluetoothAdapter.getDefaultAdapter() == null) {
            return;
        }

        if (instance == null) {
            instance = new BluetoothChallengeInputStub();
            instance.start();
        }

        instance.setInputReceiver(inputReceiver);
    }

    private static BluetoothChallengeInputStub instance = null;
    private static ReceiverThread receiverThread = null;

    private class ReceiverThread extends Thread {

        private InputStream inputStream;

        public ReceiverThread() {
            try {
                inputStream = socket.getInputStream();
            }

            catch (IOException e) {
            }
        }

        public void run() {

            while (true) {
                byte[] buffer = new byte[1024];
                int bytes;

                while (true) {
                    try {
                        bytes = inputStream.read(buffer);
                        String string = new String(buffer, 0, bytes);

                        if (string.compareTo("Z") == 0) {
                            onGestureBegin();
                        }

                        else {
                            onGestureEnd(Gesture.fromString(string));
                        }
                    }

                    catch (IOException e) {
                        break;
                    }
                }
            }
        }
    }

    private InputReceiver inputReceiver;
    private BluetoothSocket socket;

    @Override
    public void run() {
        BluetoothServerSocket serverSocket = null;

        while (true) {
            try {
                serverSocket = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord("XSwiPIN", UUID.fromString("add35ddb-a220-4934-8ca9-f3bcd2e6a466"));
                socket = serverSocket.accept();
            }

            catch (IOException e) {
                socket = null;
                return;
            }

            if (socket != null) {
                try {
                    serverSocket.close();

                    new ReceiverThread().start();

                    //return;
                }

                catch (IOException e) {
                }
            }
        }
    }

    @Override
    public void setInputReceiver(InputReceiver inputReceiver) {
        this.inputReceiver = inputReceiver;
    }

    @Override
    public void onGestureBegin() {
        if (inputReceiver != null) {
            inputReceiver.onGestureBegin();
        }
    }

    @Override
    public void onGestureEnd(Gesture gesture) {
        if (inputReceiver != null) {
            inputReceiver.onGestureEnd(gesture);
        }
    }

}
