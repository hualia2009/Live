package com.sixnine.live.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap.Config;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.leslie.gamevideo.AppConnect;
import com.leslie.gamevideo.UpdatePointsNotifier;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.sixnine.live.R;
import com.sixnine.live.bean.AnchorInfo;
import com.sixnine.live.fragment.LiveHallFragment;
import com.sixnine.live.install.FileUtil;
import com.sixnine.live.install.UpdateService;
import com.sixnine.live.util.SharePreferenceUtil;
import com.sixnine.live.util.Utils;
import com.sixnine.live.util.Utils.DialogListenner;



public class MainPageAdapter extends BaseExpandableListAdapter {
    private String[] typeNames={"风云人物","推荐","女神","好声音", "新秀","劲爆","搞笑","萌妹","热门主播"}; 
	private int[] typeImageId = { R.drawable.fengyun_title_logo,
			R.drawable.feilei_tuijian, R.drawable.fenlai_meinv,
			R.drawable.feilei_haoshengyin, R.drawable.feilei_xinxiu,
			R.drawable.fenlei_jingbao, R.drawable.fenlei_gaoxiao,
			R.drawable.fenlei_meng, R.drawable.fenlei_qita };
	
	private Context context;
	private static final int ITEM_LEFT = 0;
	private static final int ITEM_RIGHT = 2;
	
	private List<String> typeName;
	private List<Integer> typeImage;
	
	private List<AnchorInfo> windLists;
	private List<AnchorInfo> recommendLists;
	private List<AnchorInfo> newLists;
	private List<AnchorInfo> hotLists;
	private List<AnchorInfo> mengMeiZiLists;
	private List<AnchorInfo> jingBaoLists;
	private List<AnchorInfo> haoShengYinLists;
	private List<AnchorInfo> nvShenLists;
	private List<AnchorInfo> gaoXiaoLists;
	
	public static ImageLoader mImageLoader = null;
	public static DisplayImageOptions mOptions;
	
	public MainPageAdapter(Context context,List<AnchorInfo> windLists,
			List<AnchorInfo> recommendLists, List<AnchorInfo> newLists,
			List<AnchorInfo> hotLists, List<AnchorInfo> mengMeiZiLists,
			List<AnchorInfo> jingBaoLists, List<AnchorInfo> haoShengYinLists,
			List<AnchorInfo> nvShenLists, List<AnchorInfo> gaoXiaoLists) {
		super();
		typeName=new ArrayList<String>();
		typeImage=new ArrayList<Integer>();
		for (int i = 0; i < typeNames.length; i++) {
			typeName.add(typeNames[i]);
			typeImage.add(typeImageId[i]);
		}
		this.context=context;
		this.windLists = windLists;
		this.recommendLists = recommendLists;
		this.newLists = newLists;
		this.hotLists = hotLists;
		this.mengMeiZiLists = mengMeiZiLists;
		this.jingBaoLists = jingBaoLists;
		this.haoShengYinLists = haoShengYinLists;
		this.nvShenLists = nvShenLists;
		this.gaoXiaoLists = gaoXiaoLists;
		init();
	}
	
    private void init() {
		dealWithLists(windLists, typeNames[0],typeImageId[0]);
		dealWithLists(recommendLists, typeNames[1],typeImageId[1]);
		dealWithLists(nvShenLists, typeNames[2],typeImageId[2]);
		dealWithLists(haoShengYinLists, typeNames[3],typeImageId[3]);
		dealWithLists(newLists, typeNames[4],typeImageId[4]);
		dealWithLists(jingBaoLists, typeNames[5],typeImageId[5]);
		dealWithLists(gaoXiaoLists, typeNames[6],typeImageId[6]);
		dealWithLists(mengMeiZiLists, typeNames[7],typeImageId[7]);
		dealWithLists(hotLists, typeNames[8],typeImageId[8]);
		
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
	public Object getChild(int arg0, int arg1) {
		return null;
	}

	@Override
	public long getChildId(int arg0, int arg1) {
		return 0;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		ChildViewHolder holder;
		if (null==convertView) {
			holder=new ChildViewHolder();
			convertView=View.inflate(context, R.layout.ns_livehall_mainpage_list_childitem, null);
			initChildViews(holder.left, convertView.findViewById(R.id.item_left),
					getHost(groupPosition,childPosition, ITEM_LEFT));
			initChildViews(holder.right, convertView.findViewById(R.id.item_right),
					getHost(groupPosition,childPosition, ITEM_RIGHT));
			convertView.setTag(holder);
		} else {
            holder=(ChildViewHolder) convertView.getTag();
		}

		convertView.findViewById(R.id.item_left).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (getHost(groupPosition,childPosition, ITEM_LEFT) == null) {
					return;
				}
				if (!isPluginApkInstalled(context)) {
					return;
				}
				
				AnchorInfo anchorInfo=getHost(groupPosition,childPosition, ITEM_LEFT);
				//提示下载广告
				if(Integer.parseInt(anchorInfo.getUserCount()) > 1000 && 
				        SharePreferenceUtil.getInstance(context).getTotalPoint()<=50){
				    Utils.customDialog(context, "此房间人数过多，进入房间积分不够，去获取积分？", new DialogListenner(){

                        @Override
                        public void confirm() {
                            AppConnect.getInstance(context).showOffers(context);
                        }
                        
                    });
				    return;
				}
				
				if(Integer.parseInt(anchorInfo.getUserCount()) > 1000){
				    Utils.MakeToast(context, "由于房间人数过多，需要花费积分50");
				    AppConnect.getInstance(context).spendPoints(50, new UpdatePointsNotifier() {
                        
                        @Override
                        public void getUpdatePointsFailed(String arg0) {
                            
                        }
                        
                        @Override
                        public void getUpdatePoints(String arg0, int arg1) {
                            SharePreferenceUtil.getInstance(context).setTotalPoint(arg1);
                        }
                    });
				}
				
				
				Intent intent = new Intent();
				ComponentName componentName = new ComponentName("com.ninexiu.plugin",
						"com.ninexiu.plugin.activity.LiveRoomActivity");
				intent.putExtra("roomId", anchorInfo.getRid());
				intent.putExtra("isPlay", anchorInfo.getIsPlay());
				intent.putExtra("nickName", anchorInfo.getNickName());

				intent.setComponent(componentName);
				context.startActivity(intent);
				
			}
		});
		convertView.findViewById(R.id.item_right).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (getHost(groupPosition,childPosition, ITEM_RIGHT) == null) {
					return;
				}
				if (!isPluginApkInstalled(context)) {
					return;
				}
				
				AnchorInfo anchorInfo=getHost(groupPosition,childPosition, ITEM_RIGHT);
				//提示下载广告
                if(Integer.parseInt(anchorInfo.getUserCount()) > 1000 && 
                        SharePreferenceUtil.getInstance(context).getTotalPoint()<=50){
                    Utils.customDialog(context, "此房间人数过多，进入房间积分不够，去获取积分？", new DialogListenner(){

                        @Override
                        public void confirm() {
                            AppConnect.getInstance(context).showOffers(context);
                        }
                        
                    });
                    return;
                }
                
                if(Integer.parseInt(anchorInfo.getUserCount()) > 1000){
                    Utils.MakeToast(context, "由于房间人数过多，需要花费积分50");
                    AppConnect.getInstance(context).spendPoints(50, new UpdatePointsNotifier() {
                        
                        @Override
                        public void getUpdatePointsFailed(String arg0) {
                            
                        }
                        
                        @Override
                        public void getUpdatePoints(String arg0, int arg1) {
                            SharePreferenceUtil.getInstance(context).setTotalPoint(arg1);
                        }
                    });
                }
                
				Intent intent = new Intent();
				ComponentName componentName = new ComponentName("com.ninexiu.plugin",
						"com.ninexiu.plugin.activity.LiveRoomActivity");
				intent.putExtra("roomId", anchorInfo.getRid());
				intent.putExtra("isPlay", anchorInfo.getIsPlay());
				intent.putExtra("nickName", anchorInfo.getNickName());

				intent.setComponent(componentName);
				context.startActivity(intent);
				
			}
		});
		
		initItem(getHost(groupPosition,childPosition, ITEM_LEFT), holder.left);
		initItem(getHost(groupPosition,childPosition, ITEM_RIGHT), holder.right);
		return convertView;
	}
	
	
	public static void installApk(final Context context, int dialogTitle, final boolean isUpdateApk) {
		if(LiveHallFragment.isUpdate){
			return;
		}
		FileUtil.isApkDownload = false;
		Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(R.drawable.nineshow_ic_launcher);
		String message = context.getString(dialogTitle);
		if(isUpdateApk){
			message = LiveHallFragment.version.getVersion_info()+"";
		}
		Dialog dialog = builder
				.setTitle(R.string.show_girl)
				.setMessage(message)
				.setPositiveButton(R.string.dialog_negative,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								if(isUpdateApk){
									LiveHallFragment.isUpdate = true;
								}
							}
						})
				.setNegativeButton(R.string.dialog_positive,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(context,
										UpdateService.class);
								if (LiveHallFragment.version != null) {
									intent.putExtra("KeyAppName",
											LiveHallFragment.version.getApp_name());
									intent.putExtra("KeyDownUrl",
											LiveHallFragment.version.getApp_download_url()+"?");
									Log.e("kikiki", LiveHallFragment.version.getApp_name());
									Log.e("tytyty", LiveHallFragment.version.getApp_download_url());
									context.startService(intent);
								} else {
									Toast.makeText(context, "请检查当前网络是否畅通，",
											Toast.LENGTH_SHORT).show();
								}

							}
						}).create();
		dialog.show();
	}	
	
	public static boolean isPluginApkInstalled(Context context) {
		int versionCode = 0;
		if (LiveHallFragment.version != null) {
			versionCode = LiveHallFragment.version.getVersion_code();
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
			if(LiveHallFragment.isUpdate){
				return true;
			}
			return false;
		} else if (UpdateService.isDownloading) {
			Toast.makeText(context, "正在下载中，请稍等", Toast.LENGTH_SHORT).show();
			return false;
		}
		return false;
	}	
	
	

	@Override
	public int getChildrenCount(int groupPosition) {
		return getChildList(typeImage.get(groupPosition)).size()/2;
	}

	@Override
	public Object getGroup(int arg0) {
		return null;
	}

	@Override
	public int getGroupCount() {
		return typeName.size();
	}

	@Override
	public long getGroupId(int arg0) {
		return typeImage.get(arg0);
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		GroupViewHolder holder;
		if (null==convertView) {
			holder=new GroupViewHolder();
			convertView=View.inflate(context, R.layout.ns_livehall_mainpage_list_groupitem, null);
			holder.groupImage=(ImageView) convertView.findViewById(R.id.group_icon);
			holder.groupText=(TextView) convertView.findViewById(R.id.group_name);
			holder.groupEnter=(ImageView) convertView.findViewById(R.id.group_enter);
			convertView.setTag(holder);
		} else {
            holder=(GroupViewHolder) convertView.getTag();
		}
		if (typeName.get(groupPosition).equals("风云人物")) {
			holder.groupEnter.setVisibility(View.INVISIBLE);
		} else {
			holder.groupEnter.setVisibility(View.VISIBLE);
		}
		holder.groupImage.setImageResource(typeImage.get(groupPosition));
		holder.groupText.setText(typeName.get(groupPosition));
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		return false;
	}

	private void dealWithLists(List<AnchorInfo> list,String name,int imageId){
		if (list.size()<=1) {
			list.clear();
			typeName.remove(name);
			typeImage.remove(Integer.valueOf(imageId));
		}else {
			int size=list.size();
			if (size%2==1) {
				list.remove(size-1);
			}
		}
	}
	
	private  final class GroupViewHolder{
		public ImageView groupImage; //类型图标
		public TextView  groupText; //类型名字
		public ImageView groupEnter;
	}
	private  final class ChildViewHolder{
		public ChildSingleHolder left=new ChildSingleHolder(); //类型图标
		public ChildSingleHolder right=new ChildSingleHolder(); //类型图标
	}
	private class ChildSingleHolder{
		public ImageView icon;
		public TextView name;
		public TextView count;
		public View parent;
	}
	
	
	private List<AnchorInfo> getChildList(int groupImageId) {
		if (groupImageId == R.drawable.fengyun_title_logo) {
			return windLists;
		} else if (groupImageId == R.drawable.feilei_tuijian) {
			return recommendLists;
		} else if (groupImageId == R.drawable.fenlai_meinv) {
			return nvShenLists;
		} else if (groupImageId == R.drawable.feilei_haoshengyin) {
			return haoShengYinLists;
		} else if (groupImageId == R.drawable.feilei_xinxiu) {
			return newLists;
		} else if (groupImageId == R.drawable.fenlei_jingbao) {
			return jingBaoLists;
		} else if (groupImageId == R.drawable.fenlei_gaoxiao) {
			return gaoXiaoLists;
		} else if (groupImageId == R.drawable.fenlei_meng) {
			return mengMeiZiLists;
		} else if (groupImageId == R.drawable.fenlei_qita) {
			return hotLists;
		}
		return null;
	}
	
	public int getJumpToPageNum(int groupPosition){
		int groupImageId=typeImage.get(groupPosition);
		if (groupImageId == R.drawable.fengyun_title_logo) {
			return 0;
		} else if (groupImageId == R.drawable.feilei_tuijian) {
			return 1;
		} else if (groupImageId == R.drawable.fenlai_meinv) {
			return 2;
		} else if (groupImageId == R.drawable.feilei_haoshengyin) {
			return 3;
		} else if (groupImageId == R.drawable.feilei_xinxiu) {
			return 4;
		} else if (groupImageId == R.drawable.fenlei_jingbao) {
			return 5;
		} else if (groupImageId == R.drawable.fenlei_gaoxiao) {
			return 6;
		} else if (groupImageId == R.drawable.fenlei_meng) {
			return 7;
		} else if (groupImageId == R.drawable.fenlei_qita) {
			return 8;
		}
		return -1;
	}
	
	private void initChildViews(ChildSingleHolder holder, View view, AnchorInfo host) {
		holder.parent =  view;
		if (host == null) {
			return;
		}
		holder.icon = (ImageView) view.findViewById(R.id.icon);
		holder.name= (TextView) view.findViewById(R.id.anchor_name);
		holder.count = (TextView) view.findViewById(R.id.anchor_count);
	}
	
	private AnchorInfo getHost(int groupPosition,int childPosition, int type) {
		AnchorInfo host = null;
		List<AnchorInfo> temp=getChildList(typeImage.get(groupPosition));
		switch (type) {
		case ITEM_LEFT:
			host = temp.get(2 * childPosition);
			break;
		case ITEM_RIGHT:
			if (temp.size() > (2 * childPosition + 1)) {
				host = temp.get(2 * childPosition + 1);
			}
			break;
		default:
			break;
		}
		return host;
	}	
	
	private void initItem(AnchorInfo host, final ChildSingleHolder holder) {
		if (host == null) {
			holder.parent.setVisibility(View.INVISIBLE);
			return;
		}
		
		if("1".equals(host.getIsPlay())){
			holder.count.setCompoundDrawablesWithIntrinsicBounds(context.getResources().
					getDrawable(R.drawable.icon_status_play), null, null, null);
		}else{
			holder.count.setCompoundDrawablesWithIntrinsicBounds(context.getResources().
					getDrawable(R.drawable.icon_zaixianrenshu), null, null, null);
		}
		
		
		holder.parent.setVisibility(View.VISIBLE);
		holder.count.setText(host.getUserCount());
		holder.name.setText(host.getNickName());
		mImageLoader.displayImage(host.getPhoneHallPoster(), holder.icon, mOptions, null);
		RelativeLayout.LayoutParams layoutParams=(LayoutParams) holder.icon.getLayoutParams();
		layoutParams.height=getImageHeight();
		holder.icon.setLayoutParams(layoutParams);
		
	}	
	
	public void loadMoreHot(){
		if (hotLists!=null) {
			dealWithLists(hotLists, typeNames[8],typeImageId[8]);
			notifyDataSetChanged();
		}
	}
	
	private int getImageHeight(){
		int screenWidth=((Activity)context).getWindowManager().getDefaultDisplay().getWidth();
		int height=(screenWidth-Utils.dip2px(context, 15))*3/8;
		return height;
	}
	
}
