package com.sixnine.live.fragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;














import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ninexiu.sixninexiu.lib.http.AsyncHttpClient;
import com.ninexiu.sixninexiu.lib.http.BaseJsonHttpResponseHandler;
import com.ninexiu.sixninexiu.lib.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.sixnine.live.R;
import com.sixnine.live.adapter.TypePageAdapter;
import com.sixnine.live.bean.AnchorInfo;
import com.sixnine.live.bean.MainPageResult;
import com.sixnine.live.util.Constants;
import com.sixnine.live.util.JSONUtil;


public class TypePageFragment extends Fragment {

	private static final int DEFAULT_COREPOOLSIZE = 2;
	private static final long DEFAULT_KEEP_ALIVE_TIME = 30L;
	private static final int DEFAULT_MAXIMUM_POOLSIZE = 20;
	
	private View rootView;
	private View loadingView;
	private PullToRefreshListView pullToRefreshListView;
	private ListView listView;	
	private View loadingMorePageLayout;
	private TypePageAdapter typePageAdapter;
	
	private int pageNum=0;
	private String type;
	private int province=0;
	private boolean isLastRow=false;
	private boolean pageSwitch=true;
	
	private List<AnchorInfo> anchorList;
	
	private AsyncHttpClient asyncHttpClient;
	
	private OnScrollListener onScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (isLastRow&&scrollState==SCROLL_STATE_IDLE) {
				
				ThreadPoolExecutor executor=(ThreadPoolExecutor) asyncHttpClient.getThreadPool();
            	if (executor.getActiveCount()>=1) {
					return;
				}
            	if (pageSwitch) {
                	getTypePageData();
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
            }else {
				isLastRow=false;
			}
		}
	};
	
	public TypePageAdapter getTypePageAdapter() {
        return typePageAdapter;
    }

    public void setTypePageAdapter(TypePageAdapter typePageAdapter) {
        this.typePageAdapter = typePageAdapter;
    }

    private OnRefreshListener2 onRefreshListener = new OnRefreshListener2() {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase refreshView) {
			ThreadPoolExecutor executor=(ThreadPoolExecutor) asyncHttpClient.getThreadPool();
        	if (executor.getActiveCount()>=1) {
				return;
			}
			initTypePageData(true);
			
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase refreshView) {
			
		}

	};		
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		asyncHttpClient= new AsyncHttpClient();
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
			
			type = getArguments().getString("type");
			province=getArguments().getInt("province");
			
			rootView=inflater.inflate(R.layout.ns_livehall_typepage_list, container,false);
			pullToRefreshListView=(PullToRefreshListView) rootView.findViewById(R.id.lv_content);
			loadingView=rootView.findViewById(R.id.loading_layout);
			loadingMorePageLayout=rootView.findViewById(R.id.loading_morepage_layout);
			listView=pullToRefreshListView.getRefreshableView();
			
			pullToRefreshListView.setMode(Mode.PULL_DOWN_TO_REFRESH);
			pullToRefreshListView.setDisableScrollingWhileRefreshing(true);
			pullToRefreshListView.setOnRefreshListener(onRefreshListener);
			pullToRefreshListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, false, onScrollListener));
			initTypePageData(false);
		}

		return rootView;
	}
	
	
	private void initTypePageData(final boolean pullToRefresh){
		String url;
		asyncHttpClient.setURLEncodingEnabled(false);
		RequestParams params = new RequestParams();
		if (type.equals(Constants.LIVEHALL_TYPE_DIQU)) {
			url=Constants.URL_PROVINCEPAGE_ARTISTS;
			params.put("sorttype", province);
		} else {
            url=Constants.URL_TYPEPAGE_ARTISTS;
            params.put("sorttype", type);
		}
		params.put("page", 0);
		asyncHttpClient.get(url, params, new BaseJsonHttpResponseHandler<MainPageResult>() {

            @Override
			public void onStart() {
				super.onStart();
				if (pullToRefresh) {
					loadingView.setVisibility(View.GONE);
					if (anchorList!=null) {
						anchorList.clear();
					}
				} else {
					loadingView.setVisibility(View.VISIBLE);
				}
				if (null==anchorList) {
					anchorList=new ArrayList<AnchorInfo>();
				}
			}

			@Override
            protected MainPageResult parseResponse(String rawJsonData, boolean isFailure)  {
            	JSONObject jsonObject;
            	JSONArray data;
				try {
	               	jsonObject=new JSONObject(rawJsonData);
	               	if (200==jsonObject.getInt("code")) {
	               		data=jsonObject.getJSONArray("data");
					} else {
					    return null;	
					}
				} catch (Exception e) {
					return null;
				}
					try {
						JSONUtil.parseAnchorArray(anchorList,data);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
                return null;
            }

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					String rawJsonResponse, MainPageResult response) {
				if (pullToRefresh) {
					pullToRefreshListView.onRefreshComplete();
				} else {
					loadingView.setVisibility(View.GONE);
				}
				pageNum++;
				Log.e("wrongT", ""+(getParentFragment().getActivity()==null));
				typePageAdapter=new TypePageAdapter(getParentFragment().getActivity(), anchorList);
            	listView.setAdapter(typePageAdapter);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, String rawJsonData,
					MainPageResult errorResponse) {
				throwable.printStackTrace();
					loadingView.setVisibility(View.GONE);
			}

		});
	}
	
	
	private void getTypePageData() {
		String url;
		asyncHttpClient.setURLEncodingEnabled(false);
		RequestParams params = new RequestParams();
		if (type.equals(Constants.LIVEHALL_TYPE_DIQU)) {
			url=Constants.URL_PROVINCEPAGE_ARTISTS;
			params.put("sorttype", province);
		} else {
            url=Constants.URL_TYPEPAGE_ARTISTS;
            params.put("sorttype", type);
		}
		params.put("page", pageNum);
		pageSwitch=false;
		asyncHttpClient.get(url, params, new BaseJsonHttpResponseHandler<MainPageResult>() {

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
						JSONUtil.addAnchorArray(anchorList,data);
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
					typePageAdapter.notifyDataSetChanged();
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

	@Override
	public void onDestroyView() {
		super.onDestroyView();
        ViewGroup parent = (ViewGroup) rootView.getParent();  
        if (parent != null) {  
            parent.removeView(rootView);  
        }   
	}	
	
}
