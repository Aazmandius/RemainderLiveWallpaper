package com.example.ReminderLiveWallpaperExample;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import com.example.ReminderLiveWallpaperExample.GlowService;

/**
 * Created by User on 23.11.2014.
 */
public class GlowDrawable extends ShapeDrawable {

    private Rect mBounds;
    private float mCenterX = 0.0f;
    private float mCenterY = 0.0f;
    private float mOffsetX = 40.0f;
    private float mOffsetY = 80.0f;
    private float mRadius = 0.0f;
    private float mSpeedX = 15.0f;
    private float mSpeedY = 10.0f;

    private int mColorFG = Color.rgb(0xFF, 0xFF, 0x00); // yellow
    private int mColorBG = Color.rgb(0xFF, 0x66, 0x33); // orange

    public String mesText = "";

    public GlowDrawable() {
        this(new RectShape());
    }

    public GlowDrawable(Shape s) {
        super(s);
    }

    public void setBounds(Rect bounds) {
        super.setBounds(bounds);
        mBounds = bounds;
        if (mRadius == 0.0f) {
            mCenterX = (mBounds.right - mBounds.left)/2.0f;
            mCenterY = (mBounds.bottom - mBounds.top)/2.0f;
            mRadius = mCenterX + mCenterY;
        }
    }

    public void animate() {
        mCenterX += mSpeedX;
        mCenterY += mSpeedY;

        if (mCenterX < mBounds.left + mOffsetX ||
                mCenterX > mBounds.right - mOffsetX) {
            mSpeedX *= -1.0f;
        }

        if (mCenterY < mBounds.top + mOffsetY ||
                mCenterY > mBounds.bottom - mOffsetY) {
            mSpeedY *= -1.0f;
        }
    }

    public Paint getPaint(float width, float height) {
        animate();

        RadialGradient shader = new RadialGradient(
                mCenterX, mCenterY, mRadius,
                mColorFG, mColorBG,
                Shader.TileMode.CLAMP);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(shader);
        return paint;
    }

    public void draw(Canvas c) {
        float width = c.getWidth();
        float height = c.getHeight();

        Paint tp = new Paint(Paint.ANTI_ALIAS_FLAG);
        tp.setStyle(Paint.Style.STROKE);

        TextPaint tpp = new TextPaint(tp);
        tpp.setTextSize(18);
        float realTextWidth = 0;

        for(String str : mesText.split("\n")){
            if (str.length() > realTextWidth){
                realTextWidth = tpp.measureText(str);
            }
        }

        StaticLayout sl = new StaticLayout(mesText, tpp, (int)realTextWidth, Layout.Alignment.ALIGN_CENTER, 1, 1, false);
        float realTextHeight = (float)sl.getHeight();
        mRadius = mCenterX + mCenterY + realTextWidth;
        mOffsetX = realTextWidth / 2 + realTextWidth * 0.1f;
        mOffsetY = realTextHeight;

        Paint p = getPaint(width, height);
        c.drawRect(0, 0, width, height, p);

        float leftBorder = mCenterX - realTextWidth / 2 - realTextWidth * 0.1f;
        float rightBorder = mCenterX + realTextWidth / 2 + realTextWidth * 0.1f;

        float topBorder = mCenterY - realTextHeight / 2;
        float bottomBorder = mCenterY + realTextHeight / 2;
        RectF borderRect = new RectF(leftBorder, topBorder, rightBorder, bottomBorder);
//        tp.setShadowLayer(realTextWidth, 0, 0, Color.BLACK);
        tp.setMaskFilter(new BlurMaskFilter(12, BlurMaskFilter.Blur.OUTER));
//        c.drawRect(leftBorder, topBorder, rightBorder, bottomBorder, tp);
        c.drawRoundRect(borderRect, 10, 10, tp);
        tp.setMaskFilter(null);
//        c.drawRect(leftBorder, topBorder, rightBorder, bottomBorder, tp);
//        c.drawText(mesText, leftBorder + realTextWidth * 0.1f, mCenterY, tp);

        c.save();
        c.translate(leftBorder + realTextWidth * 0.1f, topBorder);
        sl.draw(c);
        c.restore();
    }
}