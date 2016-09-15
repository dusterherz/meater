package com.dusterherz.meater;

import java.util.Calendar;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Resources res = getResources();
        Calendar c = Calendar.getInstance();
        int time = c.get(Calendar.HOUR_OF_DAY);
        String currentMeal;
        if (time < 11) {
            currentMeal = "petit-déjeuner";
        }
        else if (time < 14) {
            currentMeal = "déjeuner";
        }
        else if (time < 17) {
            currentMeal = "goûter";
        }
        else {
            currentMeal = "dîner";
        }
        String text = String.format(res.getString(R.string.log_meat), currentMeal);

        TextView t = (TextView)findViewById(R.id.textview_log_meat);
        t.setText(text);
    }
}
