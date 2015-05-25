package com.sixnine.live.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import com.ninexiu.sixninexiu.lib.http.AsyncHttpClient;
import com.ninexiu.sixninexiu.lib.http.BaseJsonHttpResponseHandler;
import com.ninexiu.sixninexiu.lib.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.sixnine.live.R;
import com.sixnine.live.adapter.AdvertiseAdapter;
import com.sixnine.live.adapter.MainPageAdapter;
import com.sixnine.live.bean.AdvertiseInfo;
import com.sixnine.live.bean.AnchorInfo;
import com.sixnine.live.bean.MainPageResult;
import com.sixnine.live.util.Constants;
import com.sixnine.live.util.JSONUtil;
import com.sixnine.live.view.CustomViewpager;



public class MainPageFragment extends Fragment {

	private static final int DEFAULT_COREPOOLSIZE = 2;
	private static final long DEFAULT_KEEP_ALIVE_TIME = 30L;
	private static final int DEFAULT_MAXIMUM_POOLSIZE = 20;
	
	private List<AnchorInfo> windLists;
	private List<AnchorInfo> recommendLists;
	private List<AnchorInfo> newLists;
	private List<AnchorInfo> hotLists;
	private List<AnchorInfo> mengMeiZiLists;
	private List<AnchorInfo> jingBaoLists;
	private List<AnchorInfo> haoShengYinLists;
	private List<AnchorInfo> nvShenLists;
	private List<AnchorInfo> gaoXiaoLists;
	private List<AdvertiseInfo> adsLists;	
	
	private View rootView;
	private View headView;
	private CustomViewpager headViewPager;
	private LinearLayout headPagerDot;
	private View[] dots;
	private View loadingView;
	private PullToRefreshExpandableListView pullToRefreshExpandableListView;
	private ExpandableListView expandableListView;	
	private View loadingMorePageLayout;
	private MainPageAdapter mainPageAdapter;
	
	private int pageNum=0;
	
	private boolean isLastRow=false;
	private boolean pageSwitch=true;
	
	private AsyncHttpClient asyncHttpClient;
	private int currentItem=0;
	private ScheduledExecutorService scheduledExecutorService;
	private class ViewPagerTask implements Runnable{  
		  
	    public void run() {  
	    	if (currentItem<Integer.MAX_VALUE) {
				currentItem++;
			} else {
				currentItem=0;  
			}
	        handler.obtainMessage().sendToTarget();  
	    }  
	}  
	private Handler handler = new Handler(){  
		  
	    @Override  
	    public void handleMessage(Message msg) {  
	        headViewPager.setCurrentItem(currentItem,true);  
	    }  
	};
	
	private OnScrollListener onScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (isLastRow&&scrollState==SCROLL_STATE_IDLE) {
				
				ThreadPoolExecutor executor=(ThreadPoolExecutor) asyncHttpClient.getThreadPool();
            	if (executor.getActiveCount()>=1) {
					return;
				}
            	if (pageSwitch) {
            		getDataByPage();
				}
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			int lastItem = firstVisibleItem + visibleItemCount;
            if (totalItemCount > 0 && lastItem == totalItemCount)
            {
            	isLastRow=true;
            	
            }
		}
	};
	
	private OnRefreshListener2 onRefreshListener = new OnRefreshListener2() {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase refreshView) {
			ThreadPoolExecutor executor=(ThreadPoolExecutor) asyncHttpClient.getThreadPool();
        	if (executor.getActiveCount()>=1) {
				return;
			}
			getMainPageData(true);
			
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase refreshView) {
			
		}

	};	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		asyncHttpClient= new AsyncHttpClient();
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(); 
		BlockingQueue<Runnable> bq= new ArrayBlockingQueue<Runnable>(50);
		ThreadPoolExecutor executor = new ThreadPoolExecutor(DEFAULT_COREPOOLSIZE,
				DEFAULT_MAXIMUM_POOLSIZE, DEFAULT_KEEP_ALIVE_TIME,
				TimeUnit.SECONDS, bq);
		asyncHttpClient.setThreadPool(executor);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView==null) {
			
			rootView=inflater.inflate(R.layout.ns_livehall_mainpage_list, container,false);
			pullToRefreshExpandableListView=(PullToRefreshExpandableListView) rootView.findViewById(R.id.lv_content);
			loadingView=rootView.findViewById(R.id.loading_layout);
			loadingMorePageLayout=rootView.findViewById(R.id.loading_morepage_layout);
			expandableListView=pullToRefreshExpandableListView.getRefreshableView();
			
			headView=inflater.inflate(R.layout.ns_livehall_ad_viewpager, null);
			headViewPager=(CustomViewpager) headView.findViewById(R.id.pager);
			headViewPager.setmPager(headViewPager);
			RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) headViewPager.getLayoutParams();
			layoutParams.height = (int)(((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth()/24f*5);
			headViewPager.setLayoutParams(layoutParams);
			
			headPagerDot=(LinearLayout) headView.findViewById(R.id.plan);
			
			expandableListView.addHeaderView(headView);
			pullToRefreshExpandableListView.setMode(Mode.PULL_DOWN_TO_REFRESH);
			pullToRefreshExpandableListView.setDisableScrollingWhileRefreshing(true);
			pullToRefreshExpandableListView.setOnRefreshListener(onRefreshListener);
			pullToRefreshExpandableListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, false, onScrollListener));
			getMainPageData(false);
		}

		return rootView;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
        ViewGroup parent = (ViewGroup) rootView.getParent();  
        if (parent != null) {  
            parent.removeView(rootView);  
        }   
	}	
	
	private void getMainPageData(final boolean pullToRefresh){
		
		//请求链接不进行转码
		asyncHttpClient.setURLEncodingEnabled(false);
		RequestParams params = new RequestParams();
		asyncHttpClient.get(Constants.URL_MAINPAGE_ARTISTS, params, new BaseJsonHttpResponseHandler<MainPageResult>() {

			@Override
			public void onStart() {
				super.onStart();
				if (pullToRefresh) {
					loadingView.setVisibility(View.GONE);
				} else {
					loadingView.setVisibility(View.VISIBLE);
				}
				if (null==windLists) {
					windLists=new ArrayList<AnchorInfo>();
				}
				if (null==recommendLists) {
					recommendLists=new ArrayList<AnchorInfo>();
				}
				if (null==newLists) {
					newLists=new ArrayList<AnchorInfo>();
				}
				if (null==hotLists) {
					hotLists=new ArrayList<AnchorInfo>();
				}
				if (null==mengMeiZiLists) {
					mengMeiZiLists=new ArrayList<AnchorInfo>();
				}
				if (null==jingBaoLists) {
					jingBaoLists=new ArrayList<AnchorInfo>();
				}
				if (null==haoShengYinLists) {
					haoShengYinLists=new ArrayList<AnchorInfo>();
				}
				if (null==nvShenLists) {
					nvShenLists=new ArrayList<AnchorInfo>();
				}
				if (null==gaoXiaoLists) {
					gaoXiaoLists=new ArrayList<AnchorInfo>();
				}
				if (null==adsLists) {
					adsLists=new ArrayList<AdvertiseInfo>();
				}
				
			}

			@Override
            protected MainPageResult parseResponse(String rawJsonData, boolean isFailure)  {
				JSONObject jsonObject,data,tags;
				try {
	               	jsonObject=new JSONObject(rawJsonData);
	               	data=jsonObject.getJSONObject("data");
				} catch (Exception e) {
					return null;
				}
				try {
                	JSONArray windArray=data.getJSONArray("wind");
                	JSONUtil.parseAnchorArray(windLists,windArray);
				} catch (Exception e) {
					e.printStackTrace();
				}
                try {
                	JSONArray recommendArray=data.getJSONArray("recommend");
                	JSONUtil.parseAnchorArray(recommendLists,recommendArray);
				} catch (Exception e) {
					e.printStackTrace();
				}
                try {
                	JSONArray newArray=data.getJSONArray("new");
                	JSONUtil.parseAnchorArray(newLists,newArray);
				} catch (Exception e) {
					e.printStackTrace();
				}
                try {
                	JSONArray hotArray=data.getJSONArray("hot");
                	JSONUtil.parseAnchorArray(hotLists,hotArray);
				} catch (Exception e) {
					e.printStackTrace();
				}

                try {
                	JSONArray adsArray=data.getJSONArray("ads");
                	JSONUtil.parseAdvertiseArray(adsLists,adsArray);
				} catch (Exception e) {
					e.printStackTrace();
				}
                
                try {
                	tags=data.getJSONObject("tags");
				} catch (Exception e) {
					return null;
				}	
                try {
                	JSONArray mengMeiZiArray=tags.getJSONArray("1");
                	JSONUtil.parseAnchorArray(mengMeiZiLists,mengMeiZiArray);
				} catch (Exception e) {
					e.printStackTrace();
				}	
                try {
                	JSONArray jingBaoArray=tags.getJSONArray("2");
                	JSONUtil.parseAnchorArray(jingBaoLists,jingBaoArray);
				} catch (Exception e) {
					e.printStackTrace();
				}
                try {
                	JSONArray haoShengYinArray=tags.getJSONArray("3");
                	JSONUtil.parseAnchorArray(haoShengYinLists,haoShengYinArray);
				} catch (Exception e) {
					e.printStackTrace();
				}
                try {
                	JSONArray nvShenArray=tags.getJSONArray("4");
                	JSONUtil.parseAnchorArray(nvShenLists,nvShenArray);
				} catch (Exception e) {
					e.printStackTrace();
				}
                try {
                	JSONArray gaoXiaoArray=tags.getJSONArray("15");
                	JSONUtil.parseAnchorArray(gaoXiaoLists,gaoXiaoArray);
				} catch (Exception e) {
					e.printStackTrace();
				}

//            	GsonBuilder builder = new GsonBuilder();
//        		Gson gson = builder.create();
//        		MainPageResult mainPageResult = null;
//        		try {
//        			mainPageResult = gson.fromJson(rawJsonData, MainPageResult.class);
//        		} catch (JsonSyntaxException e) {
//        			e.printStackTrace();
//        			mainPageResult = null;
//        		}
                return null;
            }

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					String rawJsonResponse, MainPageResult response) {
				pageNum = 0;
				if (pullToRefresh) {
					pullToRefreshExpandableListView.onRefreshComplete();
				} else {
					loadingView.setVisibility(View.GONE);
					if (adsLists!=null&&adsLists.size()!=0) {
						headViewPager.setAdapter(new AdvertiseAdapter(adsLists, getParentFragment().getActivity()));
						initAdvertiseDot(headPagerDot, adsLists.size());
						headViewPager.setOnPageChangeListener(new OnPageChangeListener() {
							
							@Override
							public void onPageSelected(int arg0) {
								currentItem=arg0;
								if (adsLists.size()!=0) {
									refreshAdvertiseDot(arg0%adsLists.size());
								}
							}
							
							@Override
							public void onPageScrolled(int arg0, float arg1, int arg2) {
								
							}
							
							@Override
							public void onPageScrollStateChanged(int arg0) {
								
							}
						});
						 scheduledExecutorService.scheduleAtFixedRate(new ViewPagerTask(), 2, 5, TimeUnit.SECONDS); 
					}
					
				}
				mainPageAdapter=new MainPageAdapter(getParentFragment().getActivity(), windLists, recommendLists, newLists, hotLists, mengMeiZiLists, jingBaoLists, haoShengYinLists, nvShenLists, gaoXiaoLists);
				expandableListView.setAdapter(mainPageAdapter);
				expandableListView.setGroupIndicator(null);
				for (int i = 0; i < mainPageAdapter.getGroupCount(); i++) {
					expandableListView.expandGroup(i);
				}
				expandableListView.setOnGroupClickListener(new OnGroupClickListener() {
					
					@Override
					public boolean onGroupClick(ExpandableListView arg0, View arg1, int arg2,
							long arg3) {
						int jumpToTag=mainPageAdapter.getJumpToPageNum(arg2);
						if (jumpToTag>0) {
							ViewPager pager=(ViewPager) getParentFragment().getActivity().findViewById(R.id.moretab_viewPager);
								pager.setCurrentItem(jumpToTag);
						}
						return true;
					}
				});
				
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, String rawJsonData,
					MainPageResult errorResponse) {
				loadingView.setVisibility(View.GONE);
			}

		});
	}	
	
	private void initAdvertiseDot(LinearLayout linearLayout, int size) {
		if (0==size) {
			return;
		}
		linearLayout.removeAllViews();
		dots = new View[size];
		for (int i = 0; i < size; i++) {
			View view = getParentFragment().getActivity().getLayoutInflater().inflate(
					R.layout.ns_livehall_ad_dot, null);
			dots[i] = view;
			linearLayout.addView(view);
		}
		refreshAdvertiseDot(0);
	}

	private void refreshAdvertiseDot(int position) {
		for (int i = 0; i < dots.length; i++) {
			if (i == position) {
				dots[i].setSelected(true);
			} else {
				dots[i].setSelected(false);
			}
		}
	}	
	
	private void getDataByPage() {
		asyncHttpClient.setURLEncodingEnabled(false);
		RequestParams params = new RequestParams();
		params.put("sorttype", 1);
		params.put("page", pageNum);
		pageSwitch=false;
		asyncHttpClient.get(Constants.URL_MAINPAGE_ARTISTS, params, new BaseJsonHttpResponseHandler<MainPageResult>() {

            @Override
			public void onStart() {
				super.onStart();
				loadingMorePageLayout.setVisibility(View.VISIBLE);
			}

			@Override
            protected MainPageResult parseResponse(String rawJsonData, boolean isFailure)  {
            	JSONObject jsonObject;
            	JSONArray data;
				try {
	               	jsonObject=new JSONObject(rawJsonData);
	               	if (200==jsonObject.getInt("code")) {
	               		data=jsonObject.getJSONArray("data");
	               		if (0==data.length()) {
							return null;
						}
					} else {
					    return null;	
					}
				} catch (Exception e) {
					return null;
				}
					try {
						JSONUtil.addAnchorArray(hotLists,data);
						return new MainPageResult();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
                return null;
            }

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					String rawJsonResponse, MainPageResult response) {
				loadingMorePageLayout.setVisibility(View.GONE);
				if (response!=null) {
					mainPageAdapter.loadMoreHot();
					pageNum++;
					isLastRow=false;
					pageSwitch=true;
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, String rawJsonData,
					MainPageResult errorResponse) {
				throwable.printStackTrace();
				loadingMorePageLayout.setVisibility(View.GONE);
			}

		});
	}	
	
}
