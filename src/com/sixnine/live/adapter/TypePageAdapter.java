package com.sixnine.live.adapter;

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
import android.widget.BaseAdapter;
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

public class TypePageAdapter extends BaseAdapter {

    private Context context;
    private List<AnchorInfo> anchorList;

    private static final int ITEM_LEFT = 0;
    private static final int ITEM_RIGHT = 2;

    public static ImageLoader mImageLoader = null;
    public static DisplayImageOptions mOptions;

    public TypePageAdapter(Context context, List<AnchorInfo> anchorList) {
        super();
        this.context = context;
        this.anchorList = anchorList;
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
            .showImageOnLoading(R.drawable.mainpage_moren).cacheOnDisc()
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();

    }

    @Override
    public int getCount() {
        if (anchorList.size() % 2 == 0) {
            return anchorList.size() / 2;
        } else {
            return anchorList.size() / 2 + 1;
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ChildViewHolder holder;
        if (null == convertView) {
            holder = new ChildViewHolder();
            convertView = View.inflate(context,
                R.layout.ns_livehall_mainpage_list_childitem, null);
            initChildViews(holder.left,
                convertView.findViewById(R.id.item_left),
                getHost(position, ITEM_LEFT));
            initChildViews(holder.right,
                convertView.findViewById(R.id.item_right),
                getHost(position, ITEM_RIGHT));
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        convertView.findViewById(R.id.item_left).setOnClickListener(
            new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (getHost(position, ITEM_LEFT) == null) {
                        return;
                    }
                    if (!isPluginApkInstalled(context)) {
                        return;
                    }

                    AnchorInfo anchorInfo = getHost(position, ITEM_LEFT);
                    //提示下载广告
                    if (Integer.parseInt(anchorInfo.getUserCount()) > 1000 && 
                            SharePreferenceUtil.getInstance(context).getTotalPoint()<=50) {
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
                    ComponentName componentName = new ComponentName(
                        "com.ninexiu.plugin",
                        "com.ninexiu.plugin.activity.LiveRoomActivity");
                    intent.putExtra("roomId", anchorInfo.getRid());
                    intent.putExtra("isPlay", anchorInfo.getIsPlay());
                    intent.putExtra("nickName", anchorInfo.getNickName());

                    intent.setComponent(componentName);
                    context.startActivity(intent);

                }
            });
        convertView.findViewById(R.id.item_right).setOnClickListener(
            new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (getHost(position, ITEM_RIGHT) == null) {
                        return;
                    }
                    if (!isPluginApkInstalled(context)) {
                        return;
                    }
                    
                    AnchorInfo anchorInfo = getHost(position, ITEM_RIGHT);
                    if (Integer.parseInt(anchorInfo.getUserCount()) > 1000 && 
                            SharePreferenceUtil.getInstance(context).getTotalPoint()<=50) {
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
                    ComponentName componentName = new ComponentName(
                        "com.ninexiu.plugin",
                        "com.ninexiu.plugin.activity.LiveRoomActivity");
                    intent.putExtra("roomId", anchorInfo.getRid());
                    intent.putExtra("isPlay", anchorInfo.getIsPlay());
                    intent.putExtra("nickName", anchorInfo.getNickName());

                    intent.setComponent(componentName);
                    context.startActivity(intent);

                }
            });
        initItem(getHost(position, ITEM_LEFT), holder.left);
        initItem(getHost(position, ITEM_RIGHT), holder.right);
        return convertView;
    }

    private void startLoninActivity() {
//		BDAccountManager.getInstance().getAuthTokenAsync(new ITokenCallback() {
//			@Override
//			public void onResult(String res) {
//				if (!TextUtils.isEmpty(res)) {
//					handler.obtainMessage(MSG_LOGIN_FINISH, res).sendToTarget();
//				} else {
//
//				}
//			}
//		}, showLoginUI ? getActivity() : null);

//        Intent intent = new Intent(context, LoginActivity.class);
//        context.startActivity(intent);

    }

    public static void installApk(final Context context, int dialogTitle,
            final boolean isUpdateApk) {
        if (LiveHallFragment.isUpdate) {
            return;
        }
        FileUtil.isApkDownload = false;
        Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.nineshow_ic_launcher);
        String message = context.getString(dialogTitle);
        if (isUpdateApk) {
            message = LiveHallFragment.version.getVersion_info() + "";
        }
        Dialog dialog = builder
            .setTitle(R.string.show_girl)
            .setMessage(message)
            .setPositiveButton(R.string.dialog_negative,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (isUpdateApk) {
                            LiveHallFragment.isUpdate = true;
                        }
                    }
                })
            .setNegativeButton(R.string.dialog_positive,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, UpdateService.class);
                        if (LiveHallFragment.version != null) {
                            intent.putExtra("KeyAppName",
                                LiveHallFragment.version.getApp_name());
                            intent.putExtra("KeyDownUrl",
                                LiveHallFragment.version.getApp_download_url()
                                    + "?");
                            Log.e("kikiki",
                                LiveHallFragment.version.getApp_name());
                            Log.e("tytyty",
                                LiveHallFragment.version.getApp_download_url());
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
            if (LiveHallFragment.isUpdate) {
                return true;
            }
            return false;
        } else if (UpdateService.isDownloading) {
            Toast.makeText(context, "正在下载中，请稍等", Toast.LENGTH_SHORT).show();
            return false;
        }
        return false;
    }

    private void initItem(AnchorInfo host, final ChildSingleHolder holder) {
        if (host == null) {
            holder.parent.setVisibility(View.INVISIBLE);
            return;
        }

        if ("1".equals(host.getIsPlay())) {
            holder.count.setCompoundDrawablesWithIntrinsicBounds(context
                .getResources().getDrawable(R.drawable.icon_status_play), null,
                null, null);
        } else {
            holder.count.setCompoundDrawablesWithIntrinsicBounds(context
                .getResources().getDrawable(R.drawable.icon_zaixianrenshu),
                null, null, null);
        }

        holder.parent.setVisibility(View.VISIBLE);
        holder.count.setText(host.getUserCount());
        holder.name.setText(host.getNickName());
        mImageLoader.displayImage(host.getPhoneHallPoster(), holder.icon,
            mOptions, null);
        RelativeLayout.LayoutParams layoutParams = (LayoutParams) holder.icon
            .getLayoutParams();
        layoutParams.height = getImageHeight();
        holder.icon.setLayoutParams(layoutParams);
    }

    private void initChildViews(ChildSingleHolder holder, View view,
            AnchorInfo host) {
        holder.parent = view;
        if (host == null) {
            return;
        }
        holder.icon = (ImageView) view.findViewById(R.id.icon);
        holder.name = (TextView) view.findViewById(R.id.anchor_name);
        holder.count = (TextView) view.findViewById(R.id.anchor_count);
    }

    private AnchorInfo getHost(int position, int type) {
        AnchorInfo host = null;
        switch (type) {
            case ITEM_LEFT:
                host = anchorList.get(2 * position);
                break;
            case ITEM_RIGHT:
                if (anchorList.size() > (2 * position + 1)) {
                    host = anchorList.get(2 * position + 1);
                }
                break;
            default:
                break;
        }
        return host;
    }

    private final class ChildViewHolder {
        public ChildSingleHolder left = new ChildSingleHolder(); //类型图标
        public ChildSingleHolder right = new ChildSingleHolder(); //类型图标
    }

    private class ChildSingleHolder {
        public ImageView icon;
        public TextView name;
        public TextView count;
        public View parent;
    }

    private int getImageHeight() {
        int screenWidth = ((Activity) context).getWindowManager()
            .getDefaultDisplay().getWidth();
        int height = (screenWidth - Utils.dip2px(context, 15)) * 3 / 8;
        return height;
    }

}
