package com.dusterherz.meater.decorators;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.LineBackgroundSpan;

import com.dusterherz.meater.R;

/**
 * Created by dusterherz on 09/08/17.
 */

public class SteakSpan implements LineBackgroundSpan {
    private final Drawable drawable;

    SteakSpan(Drawable drawable) {
        this.drawable = drawable;
    }
    @Override
    public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline, int bottom, CharSequence text, int start, int end, int lnum) {
        Rect imageBounds = c.getClipBounds();
        drawable.setBounds(imageBounds);
        drawable.draw(c);
    }
}
