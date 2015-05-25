package com.sixnine.live.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sixnine.live.bean.AdvertiseInfo;
import com.sixnine.live.bean.AnchorInfo;


public class JSONUtil {
    
	public static AnchorInfo parseAnchor(JSONObject object) {
		AnchorInfo anchorInfo=new AnchorInfo();
		anchorInfo.setRid(object.optString("rid"));
		anchorInfo.setUid(object.optString("uid"));
		anchorInfo.setNickName(object.optString("nickname"));
		anchorInfo.setUserCount(object.optString("usercount"));
		anchorInfo.setHeadImage(object.optString("headimage"));
		anchorInfo.setImage(object.optString("image"));
		anchorInfo.setPhoneHallPoster(object.optString("phonehallposter"));
		anchorInfo.setIsPlay(object.optString("status"));
		return anchorInfo;
	}
	
	public static List<AnchorInfo> parseAnchorArray(List<AnchorInfo> list,JSONArray array) throws JSONException {
		if (null==list) {
			list=new ArrayList<AnchorInfo>();
		} else {
            list.clear();
            for (int i = 0; i < array.length(); i++) {
    			JSONObject object=array.getJSONObject(i);
    			AnchorInfo anchorInfo=new AnchorInfo();
    			anchorInfo.setRid(object.optString("rid"));
    			anchorInfo.setUid(object.optString("uid"));
    			anchorInfo.setNickName(object.optString("nickname"));
    			anchorInfo.setUserCount(object.optString("usercount"));
    			anchorInfo.setHeadImage(object.optString("headimage"));
    			anchorInfo.setImage(object.optString("image"));
    			anchorInfo.setPhoneHallPoster(object.optString("phonehallposter"));
    			anchorInfo.setIsPlay(object.optString("status"));
    			list.add(anchorInfo);
    		}
		}
		return list;
	}
	
	public static void addAnchorArray(List<AnchorInfo> list,JSONArray array) throws JSONException {
		if (list!=null) {
            for (int i = 0; i < array.length(); i++) {
    			JSONObject object=array.getJSONObject(i);
    			AnchorInfo anchorInfo=new AnchorInfo();
    			anchorInfo.setRid(object.optString("rid"));
    			anchorInfo.setUid(object.optString("uid"));
    			anchorInfo.setNickName(object.optString("nickname"));
    			anchorInfo.setUserCount(object.optString("usercount"));
    			anchorInfo.setHeadImage(object.optString("headimage"));
    			anchorInfo.setImage(object.optString("image"));
    			anchorInfo.setPhoneHallPoster(object.optString("phonehallposter"));
    			anchorInfo.setIsPlay(object.optString("status"));
    			list.add(anchorInfo);
    		}
		}
	}
	
	public static List<AdvertiseInfo> parseAdvertiseArray(List<AdvertiseInfo> list,JSONArray array) throws JSONException {
		if (null==list) {
			list=new ArrayList<AdvertiseInfo>();
		} else {
            list.clear();
    		for (int i = 0; i < array.length(); i++) {
    			JSONObject object=array.getJSONObject(i);
    			AdvertiseInfo advertiseInfo=new AdvertiseInfo();
    			advertiseInfo.setId(object.optString("id"));
    			advertiseInfo.setFocusTitle(object.optString("focus_title"));
    			advertiseInfo.setFocusLinkUrl(object.optString("focus_link_url"));
    			advertiseInfo.setFocusPicUrl(object.optString("focus_pic_url"));
    			list.add(advertiseInfo);
    		}
		}
		return list;
	}
	
}
