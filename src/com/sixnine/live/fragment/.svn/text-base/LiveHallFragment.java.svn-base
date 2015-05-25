package com.sixnine.live.fragment;



import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ninexiu.sixninexiu.lib.indicator.PagerSlidingTabStrip;
import com.sixnine.live.R;
import com.sixnine.live.adapter.LiveHallTypeChoiceAdapter;
import com.sixnine.live.bean.Version;
import com.sixnine.live.data.MySharedPrefs;
import com.sixnine.live.thread.ThreadPoolWrap;
import com.sixnine.live.util.Constants;



public class LiveHallFragment extends Fragment implements OnClickListener{
	
	private View rootView;
	private PagerSlidingTabStrip indicator;
	private ViewPager viewPager;
	private ImageView popBtn;
	private TextView chooseTypeText;
	private ImageView back;
	
	private TypePagerAdapter adapter;
	
	private ViewStub typeChoicePop;
	private View typeChoiceView;
	private GridView typeChoiceGrid;
	private LiveHallTypeChoiceAdapter choiceAdapter;
	
	private int popCheckedId=0;

	private Animation showAnim,goneAnim;
	
	private String[] types = { Constants.LIVEHALL_TYPE_RECOMMEND,
			Constants.LIVEHALL_TYPE_NVSHEN,
			Constants.LIVEHALL_TYPE_HAOSHENGYIN, Constants.LIVEHALL_TYPE_NEW,
			Constants.LIVEHALL_TYPE_JINGBAO, Constants.LIVEHALL_TYPE_GAOXIAO,
			Constants.LIVEHALL_TYPE_MENGMEIZI, Constants.LIVEHALL_TYPE_DIQU,
			Constants.LIVEHALL_TYPE_HOT, Constants.LIVEHALL_TYPE_LEVEL,
			Constants.LIVEHALL_TYPE_TIME };
	
	private boolean isTypeChoicePopShown=false;

	public static Version version;
	
	public static boolean isUpdate;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(rootView==null){  
            rootView=inflater.inflate(R.layout.ns_livehall, null);  
            back =(ImageView) rootView.findViewById(R.id.left_btn);
            back.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
				}
			});
    		viewPager = (ViewPager) rootView.findViewById(R.id.moretab_viewPager);
    		indicator = (PagerSlidingTabStrip) rootView.findViewById(R.id.moretab_indicator);
    		popBtn=(ImageView) rootView.findViewById(R.id.livehall_pop_btn);
    		chooseTypeText=(TextView) rootView.findViewById(R.id.livehall_choose_pop_txt);
    		chooseTypeText.setOnClickListener(null);
    		popBtn.setOnClickListener(this);
    		adapter=new TypePagerAdapter(getChildFragmentManager());
    		viewPager.setAdapter(adapter);
    		indicator.setViewPager(viewPager);
    		indicator.setTextColorResource(R.color.livehall_tab_text_selected, R.color.livehall_tab_text_unselected);
    		indicator.setTextSize(getResources().getDimensionPixelSize(R.dimen.livehall_tab_textsize));
    		indicator.setOnPageChangeListener(new OnPageChangeListener() {
				
				@Override
				public void onPageSelected(int arg0) {
					popCheckedId=arg0;
				}
				
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					
				}
				
				@Override
				public void onPageScrollStateChanged(int arg0) {
					
				}
			});
    		loadVersion();
    		
        }  
		return rootView;		
	}

	private void loadVersion() {
		String adListStr = MySharedPrefs.read(getActivity(), "version",
				"versionStr");
		version = fromJson(adListStr);

		Runnable runnable = new Runnable() {
			public void run() {
				try {
					String results = doHttpRequest(Constants.URL_VERSION);
					MySharedPrefs.write(getActivity(), "version", "versionStr",
							results);
					JSONObject jsonObject = new JSONObject(results);
					if(jsonObject.optInt("code") == 200){
						String dataString = jsonObject.optString("data");
						version = fromJson(dataString);
					}
				} catch (Exception e) {
				}
			}
		};
		ThreadPoolWrap.getThreadPool().executeTask(runnable);
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
	

	@Override
	public void onDestroyView() {
		super.onDestroyView();
        ViewGroup parent = (ViewGroup) rootView.getParent();  
        if (parent != null) {  
            parent.removeView(rootView);  
        }   
	}

	public class TypePagerAdapter extends FragmentStatePagerAdapter {

		private String[] TITLES = {"直播", "推荐","女神","好声音", "新秀","劲爆","搞笑","萌妹","热门主播","等级","开播时间"};

		public String[] getTITLES() {
			return TITLES;
		}

		public TypePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}


		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment;
			if (position==0) {
				fragment=new MainPageFragment();
			} else {
				fragment = new TypePageFragment();
				Bundle bundle = new Bundle();
				bundle.putString("type", types[position-1]);

				fragment.setArguments(bundle);
			}
			
			return fragment;
		}

	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.livehall_pop_btn) {
			if (isTypeChoicePopShown) {
				outTypeChoicePopView();
			} else {
				inTypeChoicePopView();
			}
		} 
	}


	public void outTypeChoicePopView() {
		if (null==goneAnim) {
			goneAnim=AnimationUtils.loadAnimation(getActivity(), R.anim.ns_livehall_type_choice_pop_close);
			goneAnim.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation arg0) {
                      typeChoiceGrid.setOnItemClickListener(null);
				}

				@Override
				public void onAnimationRepeat(Animation arg0) {

				}

				@Override
				public void onAnimationEnd(Animation arg0) {
					typeChoiceGrid.setVisibility(View.GONE);
					typeChoiceGrid.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							outTypeChoicePopView();
							viewPager.setCurrentItem(arg2);
						}
					});
				}
			});
		}

		typeChoiceGrid.startAnimation(goneAnim);
		
		typeChoiceView.setVisibility(View.GONE);
		
		isTypeChoicePopShown=false;
		chooseTypeText.setVisibility(View.INVISIBLE);
	}

	private void inTypeChoicePopView() {
		initPop();
		if (null==showAnim) {
			showAnim=AnimationUtils.loadAnimation(getActivity(), R.anim.ns_livehall_type_choice_pop_show);
		}
		typeChoiceGrid.startAnimation(showAnim);
		typeChoiceGrid.setVisibility(View.VISIBLE);
		typeChoiceView.setVisibility(View.VISIBLE);
		
		choiceAdapter.refreshGrid(popCheckedId);
		
		isTypeChoicePopShown=true;
		chooseTypeText.setVisibility(View.VISIBLE);
	}
	
	private void initPop() {
		if (null==typeChoiceView) {
			typeChoicePop=(ViewStub) getActivity().findViewById(R.id.type_choice_pop_stub);
			typeChoicePop.setLayoutResource(R.layout.ns_type_choice_pop);
			View view=typeChoicePop.inflate();
			typeChoiceView=view.findViewById(R.id.typegrid_layout);
			typeChoiceGrid=(GridView) view.findViewById(R.id.typegird);
			choiceAdapter=new LiveHallTypeChoiceAdapter(getActivity(),popCheckedId);
			typeChoiceGrid.setAdapter(choiceAdapter);
			typeChoiceGrid.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					viewPager.setCurrentItem(arg2);
					outTypeChoicePopView();
				}
			});
			typeChoiceView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					outTypeChoicePopView();
				}
			});
		}
	}
	
	public void changePage(int page){
		if (viewPager!=null) {
			viewPager.setCurrentItem(page);
		}
	}
	
	
}
