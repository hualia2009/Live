package com.sixnine.live.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sixnine.live.R;



public class Utils {
	public static final long expireTime=24*7*60*60*1000;//每七天更新一次
	public static  Toast toast;
	/**
	 * 判断字符串是否为空字符串
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isNotEmptyString(String input) {
		if (input!=null&&!input.equals("null")) {
			if (input.trim().length()>0) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	    public static void MakeToast(Context context, String message) {
	        if (context != null) {
	            LayoutInflater inflate = (LayoutInflater) context
	                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            View view = inflate.inflate(R.layout.custom_toast, null);
	            if (Utils.toast == null) {
	                Utils.toast = new Toast(context);
	                Utils.toast.setView(view);
	                Utils.toast.setText(message);
	                Utils.toast.setDuration(Toast.LENGTH_SHORT);
	            } else {
	                Utils.toast.setView(view);
	                Utils.toast.setText(message);
	            }
	            Utils.toast.show();
	        }
	    }

	

    

	

	

	
	

	

	
	/**
	 * 隐藏软键盘
	 * 
	 * @param activity
	 * @author by_wsc
	 * @email wscnydx@gmail.com
	 * @date 日期：2013-6-9 下午4:52:33
	 */
	public static void hiddenKeyBoard(Context context) {

		try {
			if(context == null) return;
			// 取消弹出的对话框
			InputMethodManager manager = (InputMethodManager) context.getSystemService(
							Context.INPUT_METHOD_SERVICE);
			manager.hideSoftInputFromWindow(((Activity)context).getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String convertNickname(String nickName,String compareNickName){
		if(nickName == null || compareNickName == null){
			return nickName;
		}
		if(nickName.equals(compareNickName.trim())){
			return "我";
		}
		return nickName;
	}
	

    

    
 
    
  //判断是否10富
  	public static boolean isRich(long wealth) {
  		if (wealth>=5742000) {
  			return true;
  		} else {
              return false;
  		}
  	}
  	
  	/*
     * 下载除了豪华礼物之外的其他礼物文件
     */
    public static void downloadFile(final File dir,final String url,boolean needUpdate){
    	if(dir!=null){
    	String fileString=url.substring(url.lastIndexOf("/")+1, url.length());
    	final String fileName = fileString.substring(0, fileString.lastIndexOf("."));
		final File targetFile=new File(dir,fileName);
		if(targetFile.length()==0||(System.currentTimeMillis()-targetFile.lastModified()>expireTime)||needUpdate){
			try {
				if(!targetFile.exists())
				targetFile.createNewFile();
//		    	new Thread(new Runnable() {
		    			InputStream inputStream=null;
		    			FileOutputStream fileOutputStream=null;
		    			HttpURLConnection urlConnection=null;
//					@Override
//					public void run() {  //api4.0以上下载文件必须开启线程 否则报异常 （这错误搞了好久才发现 艹）
						try {
							URL uri=new URL(url);
							urlConnection=(HttpURLConnection) uri.openConnection();
							urlConnection.setConnectTimeout(700000);
							urlConnection.setReadTimeout(50000);
							if(urlConnection.getResponseCode()==200){
								 inputStream=urlConnection.getInputStream();
									fileOutputStream=new FileOutputStream(targetFile);
									byte[] buffer=new byte[1024];
									int len;
									while((len=inputStream.read(buffer))!=-1){
										fileOutputStream.write(buffer, 0, len);
									}
							}
						}catch (Exception e) {
							e.printStackTrace();
						}
				    	finally{
				    		if(inputStream!=null){
				    			try {
									inputStream.close();
									inputStream=null;
								} catch (Exception e) {
									e.printStackTrace();
								}
				    		}
				    		if(fileOutputStream!=null){
				    			try {
									fileOutputStream.close();
									fileOutputStream=null;
								} catch (Exception e) {
									e.printStackTrace();
								}
				    		}
				    		if(urlConnection != null)
			    				urlConnection.disconnect();
				    	}
						
//					}
//				}).start();
		    	
			} catch (Exception e1) {
				e1.printStackTrace();
			}
	    }else{
	    	return;
	    }
		}
    }
    
    /*
     * 下载转盘需要的背景图片
     */
    public static void downloadLuckDrawFile(final File dir,final String url){
    	if(dir!=null){
    	String fileString=url.substring(url.lastIndexOf("/")+1, url.length());
    	final String fileName = fileString.substring(0, fileString.lastIndexOf("."));
		final File targetFile=new File(dir,fileName);
		if(targetFile.length()==0){
			try {
				if(!targetFile.exists())
					targetFile.createNewFile();
		    			InputStream inputStream=null;
		    			FileOutputStream fileOutputStream=null;
		    			HttpURLConnection urlConnection=null;
						try {
							URL uri=new URL(url);
							urlConnection=(HttpURLConnection) uri.openConnection();
							urlConnection.setConnectTimeout(700000);
							urlConnection.setReadTimeout(50000);
							if(urlConnection.getResponseCode()==200){
								 inputStream=urlConnection.getInputStream();
									fileOutputStream=new FileOutputStream(targetFile);
									byte[] buffer=new byte[1024];
									int len;
									while((len=inputStream.read(buffer))!=-1){
										fileOutputStream.write(buffer, 0, len);
									}
							}
						}catch (Exception e) {
							e.printStackTrace();
						}
				    	finally{
				    		if(inputStream!=null){
				    			try {
									inputStream.close();
									inputStream=null;
								} catch (Exception e) {
									e.printStackTrace();
								}
				    		}
				    		if(fileOutputStream!=null){
				    			try {
									fileOutputStream.close();
									fileOutputStream=null;
								} catch (Exception e) {
									e.printStackTrace();
								}
				    		}
				    		if(urlConnection != null)
			    				urlConnection.disconnect();
				    	}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
	    }else{
	    	return;
	    }
		}
    }
    
    /*
     * 下载转盘需要的背景图片
     */
    public static boolean downloadActivity(final File dir,final String url){
    	if(dir!=null){
	    	String fileString=url.substring(url.lastIndexOf("/")+1, url.length());
	    	final String fileName = fileString.substring(0, fileString.lastIndexOf("."));
			final File targetFile=new File(dir,fileName);
			if(targetFile.length()==0){
				try {
					if(!targetFile.exists())
						targetFile.createNewFile();
			    			InputStream inputStream=null;
			    			FileOutputStream fileOutputStream=null;
			    			HttpURLConnection urlConnection=null;
							try {
								URL uri=new URL(url);
								urlConnection=(HttpURLConnection) uri.openConnection();
								urlConnection.setConnectTimeout(700000);
								urlConnection.setReadTimeout(50000);
								if(urlConnection.getResponseCode()==200){
									 inputStream=urlConnection.getInputStream();
										fileOutputStream=new FileOutputStream(targetFile);
										byte[] buffer=new byte[1024];
										int len;
										while((len=inputStream.read(buffer))!=-1){
											fileOutputStream.write(buffer, 0, len);
										}
								}
							}catch (Exception e) {
								
							}finally{
					    		if(inputStream!=null){
					    			try {
										inputStream.close();
										inputStream=null;
									} catch (Exception e) {
										e.printStackTrace();
									}
					    		}
					    		if(fileOutputStream!=null){
					    			try {
										fileOutputStream.close();
										fileOutputStream=null;
									} catch (Exception e) {
										e.printStackTrace();
									}
					    		}
					    		if(urlConnection != null){
				    				urlConnection.disconnect();
					    		}
					    		if(targetFile.length()==0){
									return false;
								}
					    	}
				} catch (Exception e1) {
					return false;
				}
		    }
		}else{
			return false;
		}
    	return true;
    }
    
 
	

	

	
	public static final float[] credits = {
		0,50,200,500,1000,2000,4000,7500,
		13000,21000,33000,48000,68000,98000,
		148000,228000,348000,528000,778000,1108000,
		1518000,2018000,2618000,3318000,4118000,5018000,
		6018000,7118000,8368000,9868000
	};

	
	/**
	 * 读取字节流，根据网络地址	
	 * @param in：根据网络地址返回的流
	 * @return：返回数组
	 * @throws IOException：流异常
	 */
	public static String read(InputStream is) throws IOException {  
        byte[] data;  
        ByteArrayOutputStream bout = new ByteArrayOutputStream();  
        byte[]buf = new byte[1024];  
        int len = 0;  
        while((len = is.read(buf))!=-1){  
            bout.write(buf, 0, len);  
        }   
        data = bout.toByteArray();  
        bout.close();
        bout = null;
        return new String(data,"UTF-8");  
    }
	

	

    



	
	/**
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 * 传入Bitmap对象
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap,int width,int height,Context context) { 
		width = dip2px(context,width);
		height = dip2px(context,height);

		Bitmap output = Bitmap.createBitmap(width,height, Config.ARGB_8888); 
		Canvas canvas = new Canvas(output); 

		final int color = 0xff424242; 

		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()); 
		final Rect mRect = new Rect(0, 0, width,height); 

		final RectF rectF = new RectF(mRect); 
		final float roundPx = width/2; 

		paint.setAntiAlias(true); 
		canvas.drawARGB(0, 0, 0, 0); 
		paint.setColor(color); 
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint); 
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN)); 
		canvas.drawBitmap(bitmap, rect, mRect, paint); 

		return output; 
		} 
	
	/** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) { 
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    } 
    
	public static String getLineString(InputStream in){
		try {
			StringBuffer sb = new StringBuffer();
			BufferedReader read = new BufferedReader(new InputStreamReader(in));
			String temp;
			try {
				while ((temp=read.readLine())!=null) {
					sb.append(temp);
				}		
			} catch (IOException e) {
				e.printStackTrace();
			}
			return sb.toString();
		
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}catch (Error e) {
			return "";
		}finally{
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
    public interface LoginDialogListenner{
    	public void confirm();
    }
    
    public interface DialogListenner {
        public void confirm();
    }
    
    @SuppressLint("NewApi")
    public static void customDialog(Context context, String title,
            final DialogListenner dialogListenner) {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.show();
        Window window = dialog.getWindow();
        View view = LayoutInflater.from(context).inflate(R.layout.alert_dialog,
            null);
        window.setContentView(view);
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        LayoutParams params = window.getAttributes();
        Point point = new Point();
        display.getSize(point);
        params.width = (int) (point.x * 0.8);
        window.setAttributes(params);

        TextView mainContent = (TextView) window.findViewById(R.id.title);
        Button confirm = (Button) window.findViewById(R.id.confirm);
        Button cancel = (Button) window.findViewById(R.id.cancel);

        mainContent.setText(title);
        confirm.setText("确认");
        confirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                dialogListenner.confirm();
            }
        });
        cancel.setText("取消");
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.cancel();
            }
        });
    }
	
 
    
 
    
//    public static void loginDialog(Context context,String cancelStr,String mainTitle,String viceTitle,
//    		final LoginDialogListenner loginDialogListenner) {
//    	final AlertDialog dialog=new AlertDialog.Builder(context).create();
//    	if(((Activity)context).isFinishing()){
//    		return;
//    	}
//		dialog.show();
//		Window window = dialog.getWindow();
//		View view = LayoutInflater.from(context).inflate(R.layout.login_dialog, null);
//		window.setContentView(view);
//		TextView mainContent = (TextView) window.findViewById(R.id.txt_dialog_main_content_text);
//		TextView viceContent = (TextView) window.findViewById(R.id.txt_dialog_vice_content_text);
//		Button confirm = (Button) window.findViewById(R.id.txt_standard_dialog_confirm);
//		Button cancel = (Button) window.findViewById(R.id.txt_standard_dialog_cancel);
//		
//		mainContent.setText(mainTitle);
//		viceContent.setText(viceTitle);
//		viceContent.setVisibility(View.VISIBLE);
////		viceContent.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
//		confirm.setText("立即登录");
//		confirm.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				if(dialog != null){
//					dialog.dismiss();
//				}
//				loginDialogListenner.confirm();
//			}
//		});
//		cancel.setText(cancelStr);
//		cancel.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				dialog.cancel();
//			}
//		});
//	}
    
    /** 
     * bitmap转为base64 
     * @param bitmap 
     * @return 
     */  
    public static String bitmapToBase64(Bitmap bitmap) {  
      
        String result = null;  
        ByteArrayOutputStream baos = null;  
        try {  
            if (bitmap != null) {  
                baos = new ByteArrayOutputStream();  
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
      
                baos.flush();  
                baos.close();  
      
                byte[] bitmapBytes = baos.toByteArray();  
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                if (baos != null) {  
                    baos.flush();  
                    baos.close();  
                }  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return result;  
    }
    
    /** 
     * base64转为bitmap 
     * @param base64Data 
     * @return 
     */  
    public static Bitmap base64ToBitmap(String base64Data) {  
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);  
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);  
    } 
    
    /**
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		try {
			 
			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Config.ARGB_8888); 
			Canvas canvas = new Canvas(output); 

			final int color = 0xff424242; 

			final Paint paint = new Paint(); 
			final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()); 

			final RectF rectF = new RectF(rect); 
			final float roundPx = bitmap.getWidth()/2; 

			paint.setAntiAlias(true); 
			canvas.drawARGB(0, 0, 0, 0); 
			paint.setColor(color); 
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint); 
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN)); 
			canvas.drawBitmap(bitmap, rect, rect, paint); 
			return output; 
			
		} catch (Exception e) {
			e.printStackTrace();
		}catch (Error e) {
			e.printStackTrace();
			System.gc();
		}
		return null;
	} 
	
	/**
	 * 数据加密 不能有特殊字符
	 * @param text
	 * @return
	 * @author by_wsc
	 * @email wscnydx@gmail.com
	 * @date 日期：2014-1-15 下午3:15:58
	 */
	public static String stringEncode(String text){
		if(TextUtils.isEmpty(text)){
			return "";
		}
		String encode = Base64.encodeToString(text.getBytes(), Base64.DEFAULT);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < encode.length(); i++) {
			char oldChar = encode.charAt(i);
			char newChar =  (char) (oldChar * 2 + 1);
			sb.append(newChar);
		}
		return sb.toString();
	}
	
	/**
	 * 数据解密 不能有特殊字符
	 * @param text
	 * @return
	 * @author by_wsc
	 * @email wscnydx@gmail.com
	 * @date 日期：2014-1-15 下午3:18:56
	 */
	public static String stringDecode(String text){
		if(TextUtils.isEmpty(text)){
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < text.toString().length(); i++) {
			char oldChar = text.toString().charAt(i);
			char newChar =  (char) ((oldChar -1) /2);
			sb.append(newChar);
		}
		
		return new String(Base64.decode(sb.toString(), Base64.DEFAULT));
		
	}
	
	/**
	 * 判断网络是否可用
	 * @param context
	 * @return
	 */
	public static boolean isNetwokAvailable(Context context){
		if(context != null){
			    ConnectivityManager connMgr  = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);  
			    NetworkInfo wifiInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
    	        NetworkInfo mobileInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);  
    	        if(wifiInfo.isAvailable()){  
    	            return true;  
    	        }
    	        if(mobileInfo.isAvailable()){  
    	            return true;  
    	        }
    	}    	
		return false;    	     
	}
	
	
	public static void dial(String number,Context context) {
        Class<TelephonyManager> c = TelephonyManager.class;
        Method getITelephonyMethod = null;
        try {
            getITelephonyMethod = c.getDeclaredMethod("getITelephony",
                    (Class[]) null);
            getITelephonyMethod.setAccessible(true);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Object iTelephony;
            iTelephony = (Object) getITelephonyMethod.invoke(tManager,(Object[]) null);
            Method dial = iTelephony.getClass().getDeclaredMethod("dial", String.class);
            dial.invoke(iTelephony, number);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public static void call(String number,Context context) {
        Class<TelephonyManager> c = TelephonyManager.class;
        Method getITelephonyMethod = null;
        try {
            getITelephonyMethod = c.getDeclaredMethod("getITelephony",
                    (Class[]) null);
            getITelephonyMethod.setAccessible(true);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Object iTelephony;
            iTelephony = (Object) getITelephonyMethod.invoke(tManager,(Object[]) null);
            Method dial = iTelephony.getClass().getDeclaredMethod("call", String.class);
            dial.invoke(iTelephony, number);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
	public static boolean isMobileCardOk(String cardType,String cardNumber,String cardPwd){
		//移动充值卡类型判断
		if(cardType.equals("22")){
			if(cardNumber.length()==17||cardPwd.length()==18){
				if(isDigital(cardPwd)){
    				return true;
    			}else{
    				return false;
    			}
			}
			
			if(cardNumber.length()==10||cardPwd.length()==8){
				return true;
			}
			
			if(cardNumber.length()==16||cardPwd.length()==17){
				return true;
			}
			
			return false;
		}
		
		if(cardType.equals("23")){
			if(cardNumber.length()==15||cardPwd.length()==19){
				if(isDigital(cardNumber)&&isDigital(cardPwd)){
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}
		}
		
		if(cardType.equals("TELECOM")){
			if(cardNumber.length()==19||cardPwd.length()==18){
				if(isDigital(cardPwd)){
					return true;
				}
			}
			return false;
		}
		return false;
	}
	
	public static boolean isDigital(String number){
		char[] ar=number.toCharArray();
		for (int i = 0; i < ar.length; i++) {
			if(!Character.isDigit(ar[i])){
				return false;
			}
		}
		return true;
	}    
	
}