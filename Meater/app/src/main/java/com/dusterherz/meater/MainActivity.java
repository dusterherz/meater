package com.dusterherz.meater;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dusterherz.meater.models.Consumption;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private final Calendar c = Calendar.getInstance();
    private DatabaseReference mDatabase;
    private String mUserUid;
    private TextView mTxtConsumption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        mUserUid = intent.getStringExtra(LoginActivity.EXTRA_USER_UID);
        mTxtConsumption = (TextView) findViewById(R.id.txt_times_meat);
                mDatabase = FirebaseDatabase.getInstance().getReference();

        //Init
        int time = c.get(Calendar.HOUR_OF_DAY);
        String currentMeal;

        //Set current meal time
        if (time < 11) {
            currentMeal = getString(R.string.breakfast);
        }
        else if (time < 14) {
            currentMeal = getString(R.string.lunch);
        }
        else if (time < 17) {
            currentMeal = getString(R.string.snack);
        }
        else {
            currentMeal = getString(R.string.dinner);
        }

        currentMeal = String.format(getString(R.string.log_meat), currentMeal);
        TextView txtCurrentMeal = (TextView)findViewById(R.id.textview_log_meat);
        txtCurrentMeal.setText(currentMeal);

        updateConsumptionUi();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.icn_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void incrementMeatCounter(View v) {
        Log.d(TAG, "Increment meat");
        final DatabaseReference consumptionUser = mDatabase.child(mUserUid);
        consumptionUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // get current consumption. If doesn't work -> send error msg.
                Consumption consumption = dataSnapshot.getValue(Consumption.class);
                if (consumption == null) {
                    Toast.makeText(MainActivity.this, R.string.error_send_data, Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                // increment consumption
                consumption.weekly++;

                // add date to calendar of consumption
                SimpleDateFormat simpleDateFormat =
                        new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                Date now = calendar.getTime();
                String timestamp = simpleDateFormat.format(now);
                if (consumption.history == null) {
                    consumption.history = new ArrayList<>();
                }
                if (!consumption.history.contains(timestamp)) {
                    consumption.history.add(timestamp);
                }
                consumptionUser.child("weekly").setValue(consumption.weekly);
                consumptionUser.child("history").setValue(consumption.history);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, R.string.error_send_data, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void updateConsumptionUi() {
        final Consumption newConsumption = new Consumption(0, new ArrayList<String>());

        Log.d(TAG, "Update Interface");
        // check if user exists. If not, create it.
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(mUserUid)) {
                    mDatabase.child(mUserUid).setValue(newConsumption);
                    Log.d(TAG, "Create a user");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, R.string.error_update_data, Toast.LENGTH_SHORT)
                        .show();
            }
        });

        // Update UI when data is updated on Firebase
        mDatabase.child(mUserUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Consumption consumption = dataSnapshot.getValue(Consumption.class);
                if (consumption != null) {
                    checkWeeklyClean(consumption);
                    if (mTxtConsumption.getVisibility() == View.INVISIBLE)
                        mTxtConsumption.setVisibility(View.VISIBLE);
                    mTxtConsumption.setText(String.format(getString(R.string.number_times_meat), consumption.weekly));
                    Log.d(TAG, "Update data");
                }
                else {
                    mDatabase.child(mUserUid).setValue(newConsumption);
                    mTxtConsumption.setText(String.format(getString(R.string.number_times_meat), 0));
                    Log.d(TAG, "Data set by default");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private void checkWeeklyClean(Consumption consumption) {
        Collections.sort(consumption.history, new StringDateComparator());
        String lastConsumption = consumption.history.get(consumption.history.size() - 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = dateFormat.parse(lastConsumption);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            if (cal.get(Calendar.WEEK_OF_YEAR) != c.get(Calendar.WEEK_OF_YEAR)) {
                resetWeeklyConsumption();
            }
        } catch (ParseException e) { }
    }

    private void resetWeeklyConsumption() {
        final DatabaseReference consumptionUser = mDatabase.child(mUserUid);
        consumptionUser.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                consumptionUser.child("weekly").setValue(0);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private class StringDateComparator implements Comparator<String>
    {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        public int compare(String lhs, String rhs)
        {
            try {
                return dateFormat.parse(lhs).compareTo(dateFormat.parse(rhs));
            } catch (ParseException e) {
                return 1;
            }
        }
    }
}
