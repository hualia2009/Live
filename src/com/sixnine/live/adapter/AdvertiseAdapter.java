package com.sixnine.live.adapter;

import java.util.List;











import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.sixnine.live.R;
import com.sixnine.live.bean.AdvertiseInfo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap.Config;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class AdvertiseAdapter extends PagerAdapter {

	private List<AdvertiseInfo> list;
	private Context context;
	
	public static ImageLoader mImageLoader = null;
	public static DisplayImageOptions mOptions;
	
	public AdvertiseAdapter(List<AdvertiseInfo> list, Context context) {
		super();
		this.list = list;
		this.context = context;
		ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(
				context).threadPoolSize(1).memoryCache(new WeakMemoryCache())
				.build();
		mImageLoader = ImageLoader.getInstance();
		mImageLoader.init(configuration);
		mOptions = new DisplayImageOptions.Builder()
				.bitmapConfig(Config.RGB_565)

				.showStubImage(R.drawable.mainpage_moren)
				.showImageForEmptyUri(R.drawable.mainpage_moren)
				.showImageOnFail(R.drawable.mainpage_moren)
				.showImageOnLoading(R.drawable.mainpage_moren)
				.cacheOnDisc().imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.build();	
	}

	@Override
	public int getCount() {
		if (list != null) {
			return Integer.MAX_VALUE;
		}
		return 0;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}	
	
	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		View view = ((Activity)context).getLayoutInflater().inflate(R.layout.ns_livehall_ad_pageitem, container, false);
		final ImageView icon = (ImageView) view.findViewById(R.id.icon);
//		NineShowApplication.displayImage(icon,list.get(position%list.size()).getFocusPicUrl());
		mImageLoader.displayImage(list.get(position%list.size()).getFocusPicUrl(), icon, mOptions, null);
		icon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AdvertiseInfo info = list.get(position%list.size());
				ComponentName componentName = new ComponentName("com.ninexiu.plugin",
						"com.ninexiu.plugin.activity.AdvertiseActivity");
				Intent intent = new Intent();
				intent.putExtra("url", info.getFocusLinkUrl());
				intent.putExtra("title", info.getFocusTitle());
				intent.setComponent(componentName);
				context.startActivity(intent);
			}

		});
		container.addView(view);
		return view;
	}	
	
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

}
