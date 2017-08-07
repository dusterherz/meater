package com.dusterherz.meater.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

/**
 * Created by dusterherz on 04/08/17.
 */

@IgnoreExtraProperties
public class Consumption {

    public int weekly;
    public List<String> history;

    public Consumption() {

    }

    public Consumption(int weekly, List<String> history) {
        this.weekly = weekly;
        this.history = history;
    }
}
