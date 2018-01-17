package uk.ac.reading.sis05kol.mooc;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



public class MultiThreading extends Activity {

    Handler hand = new Handler();
    Button STARTRACE;
    TextView timer;



// this class creates a loop for the startrace button mentioned in the main activity xml file , the time is reduced by 1 millisecond if the timer text is not 0;


    Runnable run = new Runnable() {
        @Override
        public void run() {
            updateTime();
        }
    };

    public void updateTime() {
        timer.setText("" + (Integer.parseInt(timer.getText().toString()) - 1));
        if (Integer.parseInt(timer.getText().toString()) == 0) {
            STARTRACE.setVisibility(View.VISIBLE);
        } else {
            hand.postDelayed(run, 1000);
        }
    }

}





