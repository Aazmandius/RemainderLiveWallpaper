package com.example.ReminderLiveWallpaperExample;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

/**
 * Created by User on 23.11.2014.
 */
public class GlowService extends WallpaperService {

    public GlowEngine eng;

    @Override
    public Engine onCreateEngine() {
        eng = new GlowEngine();
        setMessageText();
        return eng;
    }

    private class GlowEngine extends Engine {
        private com.example.ReminderLiveWallpaperExample.GlowDrawable mDrawable;
        private boolean mVisible = false;
        private final Handler mHandler = new Handler();
        private final Runnable mUpdateDisplay = new Runnable() {
            public void run() {
                draw();
            }
        };

        public GlowEngine() {
            super();
            mDrawable = new GlowDrawable();
        }

        private void draw() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas c = null;
            try {
                c = holder.lockCanvas();
                mDrawable.setBounds(c.getClipBounds());
                mDrawable.draw(c);
            } finally {
                if (c != null)
                    holder.unlockCanvasAndPost(c);
            }

            mHandler.removeCallbacks(mUpdateDisplay);
            if (mVisible) {
                mHandler.postDelayed(mUpdateDisplay, 100);
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            mVisible = visible;
            if (visible) {
                draw();
            } else {
                mHandler.removeCallbacks(mUpdateDisplay);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder,
                                     int format, int width, int height) {
            draw();
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mVisible = false;
            mHandler.removeCallbacks(mUpdateDisplay);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mVisible = false;
            mHandler.removeCallbacks(mUpdateDisplay);
        }
    }

    public void setMessageText() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        eng.mDrawable.mesText = prefs.getString("pref_message", "Keep flying...");
    }
}