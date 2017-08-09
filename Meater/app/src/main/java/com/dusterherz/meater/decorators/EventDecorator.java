package com.dusterherz.meater.decorators;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * Created by dusterherz on 09/08/17.
 */

public class EventDecorator implements DayViewDecorator {

    private final HashSet<CalendarDay> dates;
    private final Drawable drawable;

    public EventDecorator(Drawable drawable, Collection<CalendarDay> dates) {
        this.dates = new HashSet<>(dates);
        this.drawable = drawable;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new SteakSpan(drawable));
    }
}
