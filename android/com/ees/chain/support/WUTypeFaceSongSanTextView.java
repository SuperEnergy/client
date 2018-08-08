package com.ees.chain.ui.view.support;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.ees.chain.App;


public class WUTypeFaceSongSanTextView extends android.support.v7.widget.AppCompatTextView
{

    public static Typeface TYPEFACE_SONG_SAN;

    static {
        try {
            TYPEFACE_SONG_SAN = Typeface.create(
                    Typeface.createFromAsset(App.getInstance().getAssets(), "fonts/fang_zheng_song_san_jian_ti.ttf"),
                    0);
        } catch (Exception e) {
        }
    }
    public WUTypeFaceSongSanTextView(final Context context) {
        super(context);
        this.init();
    }
    
    public WUTypeFaceSongSanTextView(final Context context, final AttributeSet set) {
        super(context, set);
        this.init();
    }
    
    public WUTypeFaceSongSanTextView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.init();
    }
    
    private void init() {
        if (TYPEFACE_SONG_SAN != null) {
            this.setTypeface(TYPEFACE_SONG_SAN);
        }
    }
}
