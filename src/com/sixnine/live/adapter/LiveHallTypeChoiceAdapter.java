package com.sixnine.live.adapter;



import com.sixnine.live.R;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LiveHallTypeChoiceAdapter extends BaseAdapter {

	private Context mContext;
	private final int TYPE_COUNT=9;
	private String[] typeName={"直播","推荐","女神","好声音", "新秀","劲爆","搞笑","萌妹","热门主播"};
	private int[] typeImageId = { R.drawable.typeicon_zhibo,
			R.drawable.typeicon_tuijian, R.drawable.typeicon_nvshen,
			R.drawable.typeicon_haoshengyin, R.drawable.typeicon_xinxiu,
			R.drawable.typeicon_jingbao, R.drawable.typeicon_gaoxiao,
			R.drawable.typeicon_mengmei, 
			R.drawable.typeicon_remen};
	private int[] typeCheckedImageId = { R.drawable.typeicon_zhibo_checked,
			R.drawable.typeicon_tuijian_checked,
			R.drawable.typeicon_nvshen_checked,
			R.drawable.typeicon_haoshengyin_checked,
			R.drawable.typeicon_xinxiu_checked,
			R.drawable.typeicon_jingbao_checked,
			R.drawable.typeicon_gaoxiao_checked,
			R.drawable.typeicon_mengmei_checked,
			R.drawable.typeicon_remen_checked};
	
	private int checked;
	
	
	public LiveHallTypeChoiceAdapter(Context mContext,int checkedId) {
		super();
		this.mContext = mContext;
		this.checked=checkedId;
	}

	@Override
	public int getCount() {
		return TYPE_COUNT;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TypeViewHold hold;
		if (null==convertView) {
			hold=new TypeViewHold();
			convertView=View.inflate(mContext, R.layout.ns_type_choice_pop_item, null);
			hold.layout=(LinearLayout) convertView.findViewById(R.id.type_layout);
			hold.typeImage=(ImageView) convertView.findViewById(R.id.type_image);
			hold.typeText=(TextView) convertView.findViewById(R.id.type_name);
			convertView.setTag(hold);
		} else {
            hold=(TypeViewHold) convertView.getTag();
		}
		if (position==checked) {
			hold.layout.setBackgroundColor(mContext.getResources().getColor(R.color.livehall_pop_item_checked));
			hold.typeText.setTextColor(mContext.getResources().getColor(android.R.color.white));
			hold.typeImage.setImageResource(typeCheckedImageId[position]);
		} else {
			hold.layout.setBackgroundResource(R.drawable.ns_livehall_type_choice_pop_item_bg);
			hold.typeText.setTextColor(mContext.getResources().getColor(R.color.text_color_title));
			hold.typeImage.setImageResource(typeImageId[position]);
		}
		hold.typeText.setText(typeName[position]);
		return convertView;
	}

	private  final class TypeViewHold{
		public LinearLayout layout;
		public ImageView typeImage; //类型图标
		public TextView  typeText; //类型名字
	}
	
	public void refreshGrid(int checkedId) {
		Log.e("refreshGrid", ""+checkedId);
		this.checked=checkedId;
		notifyDataSetChanged();
	}
	
}
