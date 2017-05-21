package de.fu_berlin.inf.mfm235.xswipin;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothInputReceiverStub extends Thread implements InputReceiver {

    private String bluetoothMacAddress;
    private BluetoothSocket socket;
    private OutputStream outputStream;

    public void setBluetoothDevice(String bluetoothMacAddress) {
        this.bluetoothMacAddress = bluetoothMacAddress;
    }

    @Override
    public void run() {
        while (true) {
            try {
                BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(bluetoothMacAddress);
                socket = device.createRfcommSocketToServiceRecord(UUID.fromString("add35ddb-a220-4934-8ca9-f3bcd2e6a466"));
                socket.connect();
                outputStream = socket.getOutputStream();
                break;
            }

            catch (IOException e) {
                try {
                    Thread.sleep(1000);
                }

                catch (InterruptedException ee) {
                }
            }
        }
    }

    @Override
    public void onGestureBegin() {
        if (outputStream == null) {
            return;
        }

        try {
            outputStream.write("Z".getBytes());
        }

        catch (IOException e) {
        }
    }

    @Override
    public void onGestureEnd(Gesture gesture) {
        if (outputStream == null) {
            return;
        }

        try {
            outputStream.write(gesture.toString().getBytes());
        }

        catch (IOException e) {
        }
    }
}
