package com.sixnine.live.fragment;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


//import com.baidu.sapi2.SapiAccountManager;
//import com.baidu.sapi2.SapiConfiguration;
//import com.baidu.sapi2.activity.LoginActivity;
//import com.baidu.sapi2.utils.enums.Domain;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.cache.memory.MemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.sixnine.live.R;
import com.sixnine.live.bean.AdInfo;
import com.sixnine.live.bean.Anchor;
import com.sixnine.live.bean.Version;
import com.sixnine.live.data.MySharedPrefs;
import com.sixnine.live.install.FileUtil;
import com.sixnine.live.install.UpdateService;
import com.sixnine.live.thread.ThreadPoolWrap;
import com.sixnine.live.view.CustomViewpager;

@SuppressLint("SimpleDateFormat")
public class LiveFragment extends Fragment {

	private View view;
	private View mLoadingView;
	private View mRetryView;
	private View headerView;
	private PullToRefreshListView pullToRefreshListView;
	private ListView listView;
	private CustomViewpager viewPager;
	private View[] adPlans;
	private LinearLayout pagerDot;
	private RelativeLayout loadMoreLayout;
	private Handler handler;
	protected int mCurrentPageIndex;
	private static List<Anchor> hostList = new ArrayList<Anchor>();
	private List<AdInfo> adInfoList;
	private static Version version;
	private AnchorListAdapter anchorAdapter;
    private int currentPageNum = 0;
    private boolean isLoadingNextPage = false;
    private long lastRefreshTime = 0;
	private boolean isDataFirstLoaded;
	private static boolean isUpdate;
	private MyInstalledReceiver installedReceiver;

	private static final int MSG_VIEWPAGER = 1;
	private static final int MSG_ANCHOR = 2;
	private static final int MSG_ANCHOR_PAGE = 3;
	private static final int MSG_VIEWPAGER_CIRCLE = 4;
	private static final int NETWORK_ERROR = 5;
	private static final int MSG_LOCAL_ANCHOR = 6;
	private static final int UNKNOWN_HOST= 7;
	private static final int MSG_LOGIN_FINISH = -9999;

	private static final int TIME_CIRCLE_DELAY = 5000;

	private static final String URL_AD = "http://api.69xiu.com/common/getads";
	private static final String URL_ANCHOR = "http://api.69xiu.com/artist/getartist?";
	private static final String URL_VERSION = "http://api.69xiu.com/common/appVersion?os=1&channel=nineLive";

	private static final String TAG = "LiveFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		isDataFirstLoaded = true;
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (!isDataFirstLoaded && view != null) {
			((ViewGroup) view.getParent()).removeView(view);
			return view;
		}
		view = inflater.inflate(R.layout.cotent_layout, container, false);
		initViews();
		return view;
	}

	@SuppressLint("HandlerLeak")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (!isDataFirstLoaded) {
			return;
		}

		handler = new Handler() {

			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				setLoadingView(false);
				switch (msg.what) {
				case MSG_VIEWPAGER:
					// ViewPager load data success
					viewPager.setAdapter(new AdPagerAdapter(adInfoList,
							getActivity()));
					if (adInfoList == null) {
						break;
					}
					initAdPlan(pagerDot, adInfoList.size());
					handler.sendEmptyMessageDelayed(MSG_VIEWPAGER_CIRCLE,
							TIME_CIRCLE_DELAY);
					break;
				case MSG_ANCHOR:
					// AnchorList load data success
                    pullToRefreshListView.setLastUpdatedLabel(getLastUpdateTimeStamp());
					pullToRefreshListView.onRefreshComplete();
					if (hostList.size() == 0) {
						Toast.makeText(getActivity(), R.string.loading_error,
								Toast.LENGTH_SHORT).show();
					}
					// Note, pass through here, don't add break!
				case MSG_LOCAL_ANCHOR:
					anchorAdapter = new AnchorListAdapter(hostList,
							getActivity());
					listView.setAdapter(anchorAdapter);
					loadMoreLayout.setVisibility(View.GONE);
					break;
				case MSG_VIEWPAGER_CIRCLE:
					// ViewPager circle message
					mCurrentPageIndex++;
					viewPager.setCurrentItem(mCurrentPageIndex
							% viewPager.getAdapter().getCount());
					break;
				case MSG_ANCHOR_PAGE:
					List<Anchor> tempList = (List<Anchor>) msg.obj;
					if (tempList == null || tempList.isEmpty()) {
						setNoMoreLayoutText(R.string.loading_end, false);
						return;
					}
					hostList.addAll((List<Anchor>) msg.obj);
					anchorAdapter.notifyDataSetChanged();
					loadMoreLayout.setVisibility(View.GONE);
					setRetryView(false);
					break;
				case NETWORK_ERROR:
					pullToRefreshListView.onRefreshComplete();
					setNoMoreLayoutText(R.string.loading_error, false);
					break;
				case UNKNOWN_HOST:
					setRetryView(true);
					break;
				default:
					break;
				}
			}
		};

		listView.addFooterView(loadMoreLayout);
		listView.addHeaderView(headerView);
		setLoadingView(true);
		loadVersion();
		loadAdsData();
		loadAnchorData();
		pullToRefreshListView.setOnRefreshListener(onRefreshListener);
		pullToRefreshListView.setOnScrollListener(new PauseOnScrollListener(
				ImageLoader.getInstance(), true, false, onScrollListener));
		isDataFirstLoaded = false;
		registerReceiver();
	}
	
	private void registerReceiver(){
	    installedReceiver = new MyInstalledReceiver(); 
	    IntentFilter filter = new IntentFilter(); 
	     
	    filter.addAction("android.intent.action.PACKAGE_ADDED"); 
	    filter.addAction("android.intent.action.PACKAGE_REMOVED"); 
	    filter.addDataScheme("package"); 
	     
	    getActivity().registerReceiver(installedReceiver, filter); 
	}

	private void setNoMoreLayoutText(int stringId, boolean isProgressbarShow) {
		loadMoreLayout.setVisibility(View.VISIBLE);
		ProgressBar progressBar = (ProgressBar) loadMoreLayout
				.findViewById(R.id.list_more_progressbar);
		progressBar.setVisibility(View.VISIBLE);
		if (!isProgressbarShow) {
			progressBar.setVisibility(View.GONE);
		}
		TextView textView = (TextView) loadMoreLayout
				.findViewById(R.id.list_more_textview);
		textView.setText(stringId);
	}

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == mRetryView) {
				setLoadingView(true);
				setRetryView(false);
				return;
			}
		}
	};

	private OnScrollListener onScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			int lastItem = firstVisibleItem + visibleItemCount;
			if (totalItemCount > 0
					&& (lastItem == totalItemCount - 2 || lastItem == totalItemCount)) {
				if (ThreadPoolWrap.getThreadPool().isThreadPoolActive()) {
					return;
				}
				loadAnchorDataByPage();
			}
		}
	};

	private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			mCurrentPageIndex = arg0;
			setAdPlanSelected(arg0);
			handler.removeMessages(MSG_VIEWPAGER_CIRCLE);
			handler.sendEmptyMessageDelayed(MSG_VIEWPAGER_CIRCLE,
					TIME_CIRCLE_DELAY);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};

	private void initAdPlan(LinearLayout linearLayout, int size) {
		linearLayout.removeAllViews();
		adPlans = new View[size];
		for (int i = 0; i < size; i++) {
			View view = getActivity().getLayoutInflater().inflate(
					R.layout.item_plan, null);
			adPlans[i] = view;
			linearLayout.addView(view);
		}
		setAdPlanSelected(0);
	}

	private void setAdPlanSelected(int position) {
		for (int i = 0; i < adPlans.length; i++) {
			if (i == position) {
				adPlans[i].setSelected(true);
			} else {
				adPlans[i].setSelected(false);
			}
		}
	}

	private String getLastUpdateTimeStamp() {
        if (lastRefreshTime == 0) {
            if (isDataFirstLoaded) {
                return mLoadingView.getContext().getString(R.string.never_refresh);
            } else {
                return "";
            }
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("M月d日   HH:mm");
            Date curDate = new Date(lastRefreshTime);// 获取当前时间
            String tmStr = formatter.format(curDate);
            return tmStr;
        }
	}

	private OnRefreshListener2 onRefreshListener = new OnRefreshListener2() {



		@Override
		public void onPullDownToRefresh(PullToRefreshBase refreshView) {
			if (ThreadPoolWrap.getThreadPool().isThreadPoolActive()) {
				return;
			}
			loadAnchorData();
			loadAdsData();
			setNoMoreLayoutText(R.string.loading_begin, true);
			
		}


		@Override
		public void onPullUpToRefresh(PullToRefreshBase refreshView) {
			// TODO Auto-generated method stub
			
		}

	};

	public void onDestroyView() {
		handler.removeCallbacksAndMessages(null);
		super.onDestroyView();
	};
	
	@Override
	public void onDestroy() {
	    if(installedReceiver != null) { 
	        getActivity().unregisterReceiver(installedReceiver); 
	    } 
		super.onDestroy();
	}

	private void setLoadingView(boolean isLoading) {
		if (isLoading) {
			mLoadingView.setVisibility(View.VISIBLE);
		} else {
			mLoadingView.setVisibility(View.GONE);
		}
	}

	private void setRetryView(boolean isRetry) {
		if (isRetry) {
			mRetryView.setVisibility(View.VISIBLE);
		} else {
			mRetryView.setVisibility(View.GONE);
		}
	}

	private void initViews() {
		mLoadingView = view.findViewById(R.id.l_loadingview);
		mRetryView = view.findViewById(R.id.l_retryview);

		loadMoreLayout = (RelativeLayout) LayoutInflater.from(getActivity())
				.inflate(R.layout.video_list_more, null);
		loadMoreLayout.setVisibility(View.GONE);

		pullToRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.lv_content);
		pullToRefreshListView.setMode(Mode.PULL_DOWN_TO_REFRESH);
		pullToRefreshListView.setDisableScrollingWhileRefreshing(true);
		listView = pullToRefreshListView.getRefreshableView();

		headerView = LayoutInflater.from(getActivity()).inflate(
				R.layout.layout_home_ad_viewpager, null);

		viewPager = (CustomViewpager) headerView.findViewById(R.id.pager);
		viewPager.setmPager(viewPager);
		viewPager.setOnPageChangeListener(onPageChangeListener);

		pagerDot = (LinearLayout) headerView.findViewById(R.id.plan);
        pullToRefreshListView.setLastUpdatedLabel(getLastUpdateTimeStamp());

	}

	/**
	 * banner host data
	 * 
	 * @param hostLists
	 *            host list
	 * @return random host
	 */
	public static Anchor getLiveHost() {
		Anchor randomHost = null;
		List<Anchor> playHostList = new ArrayList<Anchor>();
		for (Anchor host : hostList) {
			if (host.getIsPlay().equals("1")) {
				playHostList.add(host);
			}
		}
		if (playHostList.size() != 0) {
			randomHost = playHostList.get(new Random().nextInt(playHostList
					.size()));
		}
		return randomHost;
	}

	private String doHttpRequest(String URL) throws Exception {
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
		HttpGet httpGet = new HttpGet(URL);
		HttpResponse response = client.execute(httpGet);
		HttpEntity entity = response.getEntity();
		return EntityUtils.toString(entity, "UTF-8");
	}

	private void loadAdsData() {
		if(isNewUser(getActivity())){
			headerView.findViewById(R.id.advertising).setVisibility(View.GONE);
			return;
		}
		headerView.findViewById(R.id.advertising).setVisibility(View.VISIBLE);
		String adListStr = MySharedPrefs.read(getActivity(), "adList",
				"adListStr");
		adInfoList = parseAds(adListStr);
		if (adInfoList != null) {
			handler.sendEmptyMessage(MSG_VIEWPAGER);
		}

		Runnable runnable = new Runnable() {
			public void run() {
				try {
					String results = doHttpRequest(URL_AD);
					MySharedPrefs.write(getActivity(), "adList", "adListStr",
							results);
					adInfoList = parseAds(results);
					if (adInfoList == null) {
						handler.sendEmptyMessage(NETWORK_ERROR);
						return;
					}
					handler.sendEmptyMessage(MSG_VIEWPAGER);
				} catch (Exception e) {
					Log.e(TAG, "loadAdsData error!");
					handler.sendEmptyMessage(NETWORK_ERROR);
				}
			}
		};
		ThreadPoolWrap.getThreadPool().executeTask(runnable);
	}

	private void loadVersion() {
		String adListStr = MySharedPrefs.read(getActivity(), "version",
				"versionStr");
		version = fromJson(adListStr);

		Runnable runnable = new Runnable() {
			public void run() {
				try {
					String results = doHttpRequest(URL_VERSION);
					MySharedPrefs.write(getActivity(), "version", "versionStr",
							results);
					JSONObject jsonObject = new JSONObject(results);
					if(jsonObject.optInt("code") == 200){
						String dataString = jsonObject.optString("data");
						version = fromJson(dataString);
					}
				} catch (Exception e) {
					Log.e(TAG, "loadVersion error!");
				}
			}
		};
		ThreadPoolWrap.getThreadPool().executeTask(runnable);
	}

	private List<AdInfo> parseAds(String jsonString) {
		List<AdInfo> list = new ArrayList<AdInfo>();
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			String code = jsonObject.optString("code");
			if ("200".equals(code)) {
				JSONArray jaArray = jsonObject.getJSONArray("data");
				for (int i = 0; i < jaArray.length(); i++) {
					JSONObject jo = jaArray.getJSONObject(i);
					AdInfo info = new AdInfo();
					info.setImageUrl(jo.getString("focus_pic_url"));
					info.setAdType(jo.getString("focus_link_url"));
					list.add(info);
				}
			} 
		} catch (Exception e) {
			Log.e(TAG, "parseAds error!");
		}
		return list;
	}

	private static boolean isBlank(String text) {
		return null == text || "".equals(text.trim());
	}

	public static Version fromJson(String json) {
		if (isBlank(json)) {
			return null;
		}
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		Version ret = null;
		try {
			ret = gson.fromJson(json, Version.class);
		} catch (Exception ex) {
			// Log;
		}
		return ret;
	}

	private void loadAnchorData() {
		// load data from local file
		String anchorListStr = MySharedPrefs.read(getActivity(), "anchorList",
				"anchorListStr");
		hostList = parseAnchor(anchorListStr);
		if (hostList!=null && hostList.size() > 0) {
			handler.sendEmptyMessage(MSG_LOCAL_ANCHOR);
		}
		// load data from network
		Runnable runnable = new Runnable() {
			public void run() {
				try {
                    String results = doHttpRequest(getURL(0));
					MySharedPrefs.write(getActivity(), "anchorList",
							"anchorListStr", results);
					hostList = parseAnchor(results);
                    if (hostList == null || hostList.size() == 0) {
						handler.sendEmptyMessage(NETWORK_ERROR);
						return;
					}
					// handler.sendEmptyMessage(MSG_ANCHOR);
					handler.sendEmptyMessageDelayed(MSG_ANCHOR, 500);
                    lastRefreshTime = System.currentTimeMillis();
                    currentPageNum = 0;
				}catch(UnknownHostException e){
					Log.e(TAG, "loadAnchorData error! + " +e.toString());
					handler.sendEmptyMessage(UNKNOWN_HOST);
				} catch (Exception e) {
					Log.e(TAG, "loadAnchorData error! + ");
					handler.sendEmptyMessage(NETWORK_ERROR);
				}
			}
		};
		ThreadPoolWrap.getThreadPool().executeTask(runnable);
	}

	private void loadAnchorDataByPage() {
        if (currentPageNum < 0 || isLoadingNextPage) {
            return;
        }
        isLoadingNextPage = true;
        setNoMoreLayoutText(R.string.loading_begin, true);
		Runnable runnable = new Runnable() {
			public void run() {
				try {
                    String results = doHttpRequest(getURL(currentPageNum + 1));
					List<Anchor> tempList = parseAnchor(results);
                    if (tempList == null) {
                        handler.sendEmptyMessage(MSG_ANCHOR_PAGE);
                        isLoadingNextPage = false;
                        return;
                    }
					Message msg = new Message();
					msg.what = MSG_ANCHOR_PAGE;
					msg.obj = tempList;
                    currentPageNum++;
					handler.sendMessage(msg);
				} catch (Exception e) {
					Log.e(TAG, "loadAnchorDataByPage error!");
					handler.sendEmptyMessage(NETWORK_ERROR);
				}
                isLoadingNextPage = false;
			}
		};
		ThreadPoolWrap.getThreadPool().executeTask(runnable);
	}

    private String getURL(int pageNum) {
    	String url = "";																																																																																																																																					
		url = String.format(URL_ANCHOR + "page=%s"+"&sorttype=%s", pageNum,1);	
		
		return url;
	}

	private List<Anchor> parseAnchor(String jsonString) {
		JSONObject jsonObject;
		List<Anchor> list = new ArrayList<Anchor>();
		try {
			jsonObject = new JSONObject(jsonString);
			String code = jsonObject.optString("code");
			if ("200".equals(code)) {
				JSONArray array = jsonObject.optJSONArray("data");
				for (int i = 0; i < array.length(); i++) {
					JSONObject jsonObject2 = array.optJSONObject(i);
					Anchor host = new Anchor();
					host.setNickName(jsonObject2.optString("nickname"));
					host.setRoomId(jsonObject2.optString("rid"));
					host.setIsPlay(jsonObject2.optString("status"));
					host.setAvatar(jsonObject2.optString("headimage"));
					host.setWeath(jsonObject2.optString("wealth"));
					host.setAudice(jsonObject2.optString("usercount"));
					host.setHostImage(jsonObject2.optString("phonehallposter"));
					host.setRoomTag(jsonObject2.optString("video_line"));
					host.setUid(jsonObject2.optString("uid"));
					list.add(host);
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "parseAnchor error");
		}
		return list;
	}

	public static boolean isPluginApkInstalled(Context context) {
		int versionCode = 0;
		if (version != null) {
			versionCode = version.getVersion_code();
		}

		int state = FileUtil.doType(context.getPackageManager(),
				"com.ninexiu.plugin", versionCode);
		if (state == FileUtil.INSTALLED) {
			return true;
		} else if (FileUtil.isApkDownload && FileUtil.updateFile.exists()
				&& FileUtil.updateFile.length() > 10000) {
			UpdateService.installApk(context);
			return false;
		}

		if (state == FileUtil.UNINSTALLED && !UpdateService.isDownloading) {
			installApk(context, R.string.dialog_title_install, false);
			return false;
		} else if (state == FileUtil.INSTALLED_UPDATE
				&& !UpdateService.isDownloading) {
			installApk(context, R.string.dialog_title_update, true);
			if(isUpdate){
				return true;
			}
			return false;
		} else if (UpdateService.isDownloading) {
			Toast.makeText(context, "正在下载中，请稍等", Toast.LENGTH_SHORT).show();
			return false;
		}
		return false;
	}
	
	public static boolean isNewUser(Context context){
		int versionCode = 0;
		if (version != null) {
			versionCode = version.getVersion_code();
		}
		int state = FileUtil.doType(context.getPackageManager(),
				"com.ninexiu.live", versionCode);
		if(state == FileUtil.UNINSTALLED){
			return false;
		}else{
			return false;
		}
	}

	public static void installApk(final Context context, int dialogTitle, final boolean isUpdateApk) {
		if(isUpdate){
			return;
		}
		FileUtil.isApkDownload = false;
		Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(R.drawable.nineshow_ic_launcher);
		String message = context.getString(dialogTitle);
		if(isUpdateApk){
			message = version.getVersion_info()+"";
		}
		Dialog dialog = builder
				.setTitle(R.string.show_girl)
				.setMessage(message)
				.setPositiveButton(R.string.dialog_negative,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								if(isUpdateApk){
									isUpdate = true;
								}
							}
						})
				.setNegativeButton(R.string.dialog_positive,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(context,
										UpdateService.class);
								if (version != null) {
									intent.putExtra("KeyAppName",
											version.getApp_name());
									intent.putExtra("KeyDownUrl",
											version.getApp_download_url()+"?");
									Log.e("kikiki", version.getApp_name());
									Log.e("tytyty", version.getApp_download_url());
									context.startService(intent);
								} else {
									Toast.makeText(context, "请检查当前网络是否畅通，",
											Toast.LENGTH_SHORT).show();
								}

							}
						}).create();
		dialog.show();
	}

	public void startChatRoom(final Context context, Anchor host) {
		if (!isPluginApkInstalled(context)) {
			return;
		}

		Intent intent = new Intent();
		ComponentName componentName = new ComponentName("com.ninexiu.plugin",
				"com.ninexiu.plugin.activity.LiveRoomActivity");
		intent.putExtra("roomId", host.getRoomId());
		intent.putExtra("isPlay", host.getIsPlay());
		intent.putExtra("roomTag", host.getRoomTag());
		intent.putExtra("uid", host.getUid());
		intent.putExtra("nickName", host.getNickName());
		intent.putExtra("audice", host.getAudice());
		intent.putExtra("credit", host.getCredit());
		intent.putExtra("userNum", host.getUserNum());
		intent.putExtra("impress", host.getImpress());
		intent.putExtra("avatar", host.getAvatar());
		intent.setComponent(componentName);
		context.startActivity(intent);
	}

	class AdPagerAdapter extends PagerAdapter {
		private final List<AdInfo> mImages;
		private final LayoutInflater mInflater;
		private Context context;
		protected MemoryCache<String, Bitmap> mMemoryCache;
		protected DisplayImageOptions mOptions;
		protected ImageLoader mImageLoader = null;

		@SuppressWarnings("deprecation")
		public AdPagerAdapter(List<AdInfo> images, Context context) {
			mImages = images;
			this.context = context;
			mInflater = LayoutInflater.from(context);

			mImageLoader = ImageLoader.getInstance();
			mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
			mMemoryCache = mImageLoader.getMemoryCache();
			mOptions = new DisplayImageOptions.Builder()
					.showStubImage(R.drawable.nineshow_default_720x291)
					.cacheInMemory().cacheOnDisc()
					.imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();

		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			if (mImages != null) {
				return mImages.size();
			}
			return 0;
		}

		private void displayImage(ImageView imageView, String url) {
			Bitmap bitmap = mMemoryCache.get(url);
			if (bitmap != null && !bitmap.isRecycled()) {
				imageView.setImageBitmap(bitmap);
			} else {
				mImageLoader.displayImage(url, imageView, mOptions, null);
			}
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			View view = mInflater.inflate(R.layout.item_home_ad, container,
					false);
			final ImageView icon = (ImageView) view.findViewById(R.id.icon);

			displayImage(icon, mImages.get(position).imageUrl);
			icon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AdInfo info = mImages.get(position);
					if (info.adType != null && info.adType.length() > 6) {
						startWebView(info);
					} else {
						startChatRoom(context, LiveFragment.getLiveHost());
					}
				}

			});
			container.addView(view);
			return view;
		}

		private void startWebView(AdInfo info) {
			if (!LiveFragment.isPluginApkInstalled(context)) {
				return;
			}

			String targetUrl;
			if (info.adType.contains("&amp;")) {
				targetUrl = info.adType.replace("&amp;", "&");
			} else {
				targetUrl = info.adType;
			}
			ComponentName componentName = new ComponentName("com.ninexiu.plugin",
					"com.ninexiu.plugin.activity.AdvertiseActivity");
			Intent intent = new Intent();
			intent.putExtra("url", targetUrl);
			intent.setComponent(componentName);
			context.startActivity(intent);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

	}

	class AnchorListAdapter extends BaseAdapter {
		private List<Anchor> hostDatas = new ArrayList<Anchor>();
		private Context context;
		protected MemoryCache<String, Bitmap> mMemoryCache;
		protected DisplayImageOptions mOptions;
		protected ImageLoader mImageLoader = null;

		private float density = 1.5f;
		private float mWidth = 480f;

		private static final int ITEM_LEFT = 0;
		private static final int ITEM_RIGHT = 2;

		@SuppressWarnings("deprecation")
		public AnchorListAdapter(List<Anchor> hostDatas, Context context) {
			super();
			this.hostDatas = hostDatas;
			this.context = context;

			mImageLoader = ImageLoader.getInstance();
			mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
			mMemoryCache = mImageLoader.getMemoryCache();
			mOptions = new DisplayImageOptions.Builder()
					.showStubImage(R.drawable.nineshow_default_240x240)
					.cacheInMemory().cacheOnDisc()
					.imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();

			DisplayMetrics metrics = context.getResources().getDisplayMetrics();
			density = metrics.density;
			mWidth = metrics.widthPixels;
		}

		public void resetList(List<Anchor> hostDatas) {
			this.hostDatas = hostDatas;
		}

		public List<Anchor> getDatas() {
			return hostDatas;
		}

		@Override
		public int getCount() {
			if (hostDatas == null) {
				return 0;
			}
			if (hostDatas.size() % 2 == 0) {
				return hostDatas.size() / 2;
			} else {
				return hostDatas.size() / 2 + 1;
			}
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		private void displayImage(ImageView imageView, String url) {
			Bitmap bitmap = mMemoryCache.get(url);
			if (bitmap != null && !bitmap.isRecycled()) {
				imageView.setImageBitmap(bitmap);
			} else {
				mImageLoader.displayImage(url, imageView, mOptions, null);
			}
		}

		@Override
		public View getView(final int position, View view, ViewGroup arg2) {
			ItemHolder holder;
			if (view == null) {
				holder = new ItemHolder();
				view = LayoutInflater.from(context).inflate(
						R.layout.item_direct, null);
				initViews(holder.leftHolder, view.findViewById(R.id.item_left),
						getHost(position, ITEM_LEFT));
				initViews(holder.rightHolder,
						view.findViewById(R.id.item_right),
						getHost(position, ITEM_RIGHT));
				view.setTag(holder);
			} else {
				holder = (ItemHolder) view.getTag();
			}

			view.findViewById(R.id.item_left).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (getHost(position, ITEM_LEFT) == null) {
								return;
							}
							startChatRoom(context, getHost(position, ITEM_LEFT));
						}
					});
			view.findViewById(R.id.item_right).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (getHost(position, ITEM_RIGHT) == null) {
								return;
							}
							startChatRoom(context,
									getHost(position, ITEM_RIGHT));
						}
					});
			initItem(getHost(position, ITEM_LEFT), holder.leftHolder);
			initItem(getHost(position, ITEM_RIGHT), holder.rightHolder);
			return view;
		}

		private void initViews(ViewHolder holder, View view, final Anchor host) {
			if (host == null) {
				return;
			}
			holder.room_thumb = (ImageView) view.findViewById(R.id.icon);
			holder.room_name = (TextView) view.findViewById(R.id.name);
			holder.room_mem_count = (TextView) view.findViewById(R.id.watching);
			holder.room_play_icon = (ImageView) view.findViewById(R.id.play);
			holder.desc = (TextView) view.findViewById(R.id.desc);
		}

		private Anchor getHost(int position, int type) {
			Anchor host = null;
			switch (type) {
			case ITEM_LEFT:
				host = hostDatas.get(2 * position);
				break;
			case ITEM_RIGHT:
				if (hostDatas.size() > (2 * position + 1)) {
					host = hostDatas.get(2 * position + 1);
				}
				break;
			default:
				break;
			}
			return host;
		}

		private void initItem(Anchor host, final ViewHolder holder) {
			if (host == null) {
				return;
			}

			int width = (int) (mWidth / 2 - density * 4);// 228 / 302;
			int height = width * 231 / 308;
			setAvatarHeigit(holder.room_thumb, width, height);

			holder.room_mem_count.setText(String.format(
					context.getString(R.string.watching), host.getAudice()));
			holder.room_name.setText(host.getNickName());
			if(host.getDetail() !=null && !host.getDetail().equals("")){
				holder.desc.setText(host.getDetail());
			}else{
				((View) holder.desc.getParent()).setVisibility(View.GONE);
			}
			
			if (host.getIsPlay().equals("1")) {
				holder.room_play_icon.setVisibility(View.VISIBLE);
			} else {
				holder.room_play_icon.setVisibility(View.GONE);
			}
			displayImage(holder.room_thumb, host.getHostImage());
		}

		private void setAvatarHeigit(ImageView room_thumb, int width, int height) {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					width, height);
			room_thumb.setLayoutParams(params);
		}

		class ItemHolder {
			ViewHolder leftHolder = new ViewHolder();
			ViewHolder middleHolder = new ViewHolder();
			ViewHolder rightHolder = new ViewHolder();
		}

		class ViewHolder {
			public ImageView room_thumb;
			public TextView room_mem_count;
			public TextView room_name;
			public ImageView room_play_icon;
			public View parent;
			public TextView desc;
		}

	}
	
	class MyInstalledReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			 if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED") && 
					 intent.getDataString().contains("com.ninexiu.live")) {    
				loadAdsData();
				loadAnchorData();
		     }    
		}
		
	}

}
