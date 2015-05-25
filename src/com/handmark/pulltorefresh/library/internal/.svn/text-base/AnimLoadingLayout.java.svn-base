package com.handmark.pulltorefresh.library.internal;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Orientation;
import com.sixnine.live.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;



public class AnimLoadingLayout extends LoadingLayout{

	private FrameLayout frameLayout;
//	private TextView textView;
//	private ImageView imageView;
	
//	private CharSequence mPullLabel;
//	private CharSequence mRefreshingLabel;
//	private CharSequence mReleaseLabel;
	
	private boolean mUseIntrinsicAnimation;
	
	private Animation animation;
	
	public AnimLoadingLayout(Context context,Mode mode, Orientation scrollDirection, TypedArray attrs) {
		super(context, mode, scrollDirection, attrs);
		LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header_simple, this);


		frameLayout = (FrameLayout) findViewById(R.id.fl_inner);
		textView = (TextView) frameLayout.findViewById(R.id.pull_to_refresh_yy_text);
		imageView = (ImageView) frameLayout.findViewById(R.id.pull_to_refresh_yy_image);

		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) frameLayout.getLayoutParams();

		lp.gravity = Gravity.BOTTOM;
		mPullLabel = "下拉刷新";
		mRefreshingLabel = "加载中";
		mReleaseLabel = "释放更新";

		// Try and get defined drawable from Attrs
		Drawable imageDrawable = context.getResources().getDrawable(getDefaultDrawableResId());

		// Set Drawable, and save width/height
		setLoadingDrawable(imageDrawable);

		reset();
	}

	public void reset() {
		if (null != textView) {
			textView.setText(mPullLabel);
		}
		imageView.setVisibility(View.VISIBLE);

		if (mUseIntrinsicAnimation) {
			((AnimationDrawable) imageView.getDrawable()).stop();
		} else {
			// Now call the callback
			resetImpl();
		}
		
	}

	public int getContentSize() {
		return frameLayout.getHeight();
	}
	
	protected void resetImpl() {
		if (imageView!=null) {
			imageView.clearAnimation();
			imageView.setVisibility(View.VISIBLE);
		}
	}

	protected int getDefaultDrawableResId() {
		return R.drawable.yy_pullrefresh;
	}

	@Override
	public void setLastUpdatedLabel(CharSequence label) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setLoadingDrawable(Drawable drawable) {
		imageView.setImageDrawable(drawable);
		mUseIntrinsicAnimation = (drawable instanceof AnimationDrawable);
		onLoadingDrawableSet(drawable);
	}

	protected void onLoadingDrawableSet(Drawable imageDrawable) {
//		if (null != imageDrawable) {
//			final int dHeight = imageDrawable.getIntrinsicHeight();
//			final int dWidth = imageDrawable.getIntrinsicWidth();
//
//			/**
//			 * We need to set the width/height of the ImageView so that it is
//			 * square with each side the size of the largest drawable dimension.
//			 * This is so that it doesn't clip when rotated.
//			 */
//			ViewGroup.LayoutParams lp = imageView.getLayoutParams();
//			lp.width = lp.height = Math.max(dHeight, dWidth);
//			imageView.requestLayout();
//
//			/**
//			 * We now rotate the Drawable so that is at the correct rotation,
//			 * and is centered.
//			 */
//			imageView.setScaleType(ScaleType.MATRIX);
//			Matrix matrix = new Matrix();
//			matrix.postTranslate((lp.width - dWidth) / 2f, (lp.height - dHeight) / 2f);
//			matrix.postRotate(getDrawableRotationAngle(), lp.width / 2f, lp.height / 2f);
//			imageView.setImageMatrix(matrix);
//		}
		
	}

	public  void hideAllViews() {
		super.hideAllViews();
		if (View.VISIBLE == imageView.getVisibility()) {
			imageView.setVisibility(View.INVISIBLE);
		}
		if (View.VISIBLE == textView.getVisibility()) {
			textView.setVisibility(View.INVISIBLE);
		}
	}	

	public final void showInvisibleViews() {
		super.showInvisibleViews();
		if (View.INVISIBLE == imageView.getVisibility()) {
			imageView.setVisibility(View.VISIBLE);
		}
		if (View.INVISIBLE == textView.getVisibility()) {
			textView.setVisibility(View.VISIBLE);
		}
	}	
	
	@Override
	public void setPullLabel(CharSequence pullLabel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRefreshingLabel(CharSequence refreshingLabel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setReleaseLabel(CharSequence releaseLabel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTextTypeface(Typeface tf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onPullImpl(float scaleOfLayout) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void pullToRefreshImpl() {
		if (null != textView) {
			textView.setText(mPullLabel);
		}
	}

	@Override
	protected void refreshingImpl() {
		if (null != textView) {
			textView.setText(mRefreshingLabel);
		}
		if (null != imageView) {
			AnimationDrawable drawable=(AnimationDrawable) imageView.getDrawable();
			drawable.start();
		}
	}

	@Override
	protected void releaseToRefreshImpl() {
		if (null != textView) {
			textView.setText(mReleaseLabel);
		}
	}	

}
