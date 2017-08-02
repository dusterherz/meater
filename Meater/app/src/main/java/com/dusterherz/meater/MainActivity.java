package com.dusterherz.meater;

import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Calendar c = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init
        Resources res = getResources();
        int time = c.get(Calendar.HOUR_OF_DAY);
        String currentMeal;
        SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);


        //Set current meal time
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
        String log_meat_text = String.format(res.getString(R.string.log_meat), currentMeal);
        TextView t_log_meat = (TextView)findViewById(R.id.textview_log_meat);
        t_log_meat.setText(log_meat_text);

        //Set meat counter and reset it if necessary
        int lastWeekLogged = prefs.getInt(getString(R.string.saved_week), 1);
        if (lastWeekLogged != c.get(Calendar.WEEK_OF_YEAR)) {
            SharedPreferences.Editor editor= prefs.edit();
            editor.putInt(getString(R.string.saved_quantity), 0);
            editor.apply();
        }
        int quantity = prefs.getInt(getString(R.string.saved_quantity), 0);
        String quantity_text = String.format(res.getString(R.string.number_times_meat), quantity);
        TextView t_times_meat = (TextView)findViewById(R.id.textview_times_meat);
        t_times_meat.setText(quantity_text);
    }

    public void incrementMeatCounter(View view){
        //Get saved value
        SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
        int quantity = prefs.getInt(getString(R.string.saved_quantity), 0) + 1;

        //Increment saved value
        SharedPreferences.Editor editor= prefs.edit();
        editor.putInt(getString(R.string.saved_quantity), quantity);
        editor.putInt(getString(R.string.saved_week), c.get(Calendar.WEEK_OF_YEAR));
        editor.apply();

        //Update display
        String quantity_text = String.format(getString(R.string.number_times_meat), quantity);
        TextView t_times_meat = (TextView)findViewById(R.id.textview_times_meat);
        t_times_meat.setText(quantity_text);
    }
}
