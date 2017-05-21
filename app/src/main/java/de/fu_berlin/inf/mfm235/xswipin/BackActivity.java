package de.fu_berlin.inf.mfm235.xswipin;

import android.app.Activity;
import android.os.Bundle;

public class BackActivity extends Activity {

    public static InputReceiver inputReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BackPad backPad = new BackPad(this);

        setContentView(backPad);
    }

}
