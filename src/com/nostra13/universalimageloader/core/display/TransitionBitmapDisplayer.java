package com.nostra13.universalimageloader.core.display;

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

public class TransitionBitmapDisplayer implements BitmapDisplayer
{
    
    private final int durationMillis;
    private int mDefaultImageResId;
    private Resources mResources;

    private final boolean animateFromNetwork;
    private final boolean animateFromDisc;
    private final boolean animateFromMemory;
    
    public TransitionBitmapDisplayer(int durationMillis, int resId, Resources res) {
        this(durationMillis, true, true, true);
        this.mDefaultImageResId = resId;
        this.mResources = res;
    }
    
    public TransitionBitmapDisplayer(int durationMillis, boolean animateFromNetwork, boolean animateFromDisc,
            boolean animateFromMemory) {
        this.durationMillis = durationMillis;
        this.animateFromNetwork = animateFromNetwork;
        this.animateFromDisc = animateFromDisc;
        this.animateFromMemory = animateFromMemory;
    }

    @Override
    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom)
    {
        imageAware.setImageBitmap(bitmap);
        if ((animateFromNetwork && loadedFrom == LoadedFrom.NETWORK) ||
                (animateFromDisc && loadedFrom == LoadedFrom.DISC_CACHE) ||
                (animateFromMemory && loadedFrom == LoadedFrom.MEMORY_CACHE)) {
            transition(imageAware, bitmap, durationMillis);
        }

    }
    
    public void transition(ImageAware imageView, Bitmap bitmap, int durationMillis) {
        Drawable[] drawables = new Drawable[2];
        Drawable defaultDrawable = null;
        try {
			defaultDrawable = mResources.getDrawable(mDefaultImageResId);
		} catch (NotFoundException e) {
			defaultDrawable = new ColorDrawable(Color.parseColor("#cccccc"));
			e.printStackTrace();
		}
        drawables[0] = defaultDrawable;
        drawables[1] = new BitmapDrawable(mResources, bitmap);
		if (drawables[0] == null || drawables[1] == null) {
			if (imageView != null && bitmap != null && !bitmap.isRecycled()) {
				imageView.setImageBitmap(bitmap);
			}
			return;
		}
        TransitionDrawable td = new TransitionDrawable(drawables);
        td.setCrossFadeEnabled(true);
        imageView.setImageDrawable(td);
        td.startTransition(durationMillis);
    }

}
