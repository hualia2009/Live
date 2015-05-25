package com.sixnine.live.install;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.sixnine.live.R;
import com.sixnine.live.fragment.LiveFragment;

public class UpdateService extends Service {

	private static final int down_step_custom = 3;

	private static final int TIMEOUT = 25 * 1000;
	private static String downUrl;
	private static String appName;
	private static int appVersion;
	private static final int DOWN_OK = 1;
	private static final int DOWN_ERROR = 0;
	private static final int NOTIFICATION_ID = 1000;
	public static boolean isDownloading = false;

	private NotificationManager notificationManager;
	private Notification notification;
	private PendingIntent pendingIntent;
	private RemoteViews contentView;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null)
			return super.onStartCommand(intent, flags, startId);
		appName = intent.getStringExtra("KeyAppName");
		downUrl = intent.getStringExtra("KeyDownUrl");
		appVersion = intent.getIntExtra("KeyVersion", 0);

		FileUtil.createFile(appName);

		if (FileUtil.isCreateFileSucess == true) {
			createNotification();
			createThread();
		} else {
			Toast.makeText(this, R.string.insert_card, Toast.LENGTH_SHORT)
					.show();
			stopSelf();

		}
		return super.onStartCommand(intent, flags, startId);
	}

	/********* update UI ******/
	@SuppressLint("HandlerLeak")
	private final Handler handler = new Handler() {
		@SuppressWarnings("deprecation")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_OK:
				isDownloading = false;
				if (FileUtil.updateFile.exists()) {
					Uri uri = Uri.fromFile(FileUtil.updateFile);
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(uri,
							"application/vnd.android.package-archive");
					pendingIntent = PendingIntent.getActivity(
							UpdateService.this, NOTIFICATION_ID, intent, 0);

					notification.flags = Notification.FLAG_AUTO_CANCEL;
					notification.setLatestEventInfo(UpdateService.this,
							appName, getString(R.string.down_sucess),
							pendingIntent);
					notificationManager.notify(R.layout.notification_item,
							notification);
					FileUtil.isApkDownload = true;
					installApk(UpdateService.this);
				} else {
					Toast.makeText(getBaseContext(), "文件已丢失，请重新下载!",
							Toast.LENGTH_SHORT).show();
				}
				stopSelf();
				break;

			case DOWN_ERROR:
				isDownloading = false;
				notification.flags = Notification.FLAG_AUTO_CANCEL;
				notification.setLatestEventInfo(UpdateService.this, appName,
						getString(R.string.down_fail), PendingIntent.getActivity(
		                    UpdateService.this, NOTIFICATION_ID, new Intent(), 0));
				notificationManager.notify(
				    R.layout.notification_item, notification);
				stopSelf();
				break;

			default:
				break;
			}
		}
	};

	public static void installApk(Context context) {

		if (FileUtil.updateFile.exists()) {
			Uri uri = Uri.fromFile(FileUtil.updateFile);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(uri,
					"application/vnd.android.package-archive");
			context.startActivity(intent);
		} else {
			Toast.makeText(context, "文件已丢失，请重新下载!", Toast.LENGTH_SHORT).show();
		}
	}

	public void createThread() {
		if (!isDownloading) {
			new DownLoadThread().start();
		}
	}

	private class DownLoadThread extends Thread {
		@Override
		public void run() {
			isDownloading = true;
			Message message = new Message();
			try {
				long downloadSize = downloadUpdateFile(downUrl,
						FileUtil.updateFile.toString());
				if (downloadSize > 10000) {
					message.what = DOWN_OK;
					handler.sendMessage(message);
				} else {
					message.what = DOWN_ERROR;
					handler.sendMessage(message);
				}
			} catch (Exception e) {
				e.printStackTrace();
				message.what = DOWN_ERROR;
				handler.sendMessage(message);
			}
		}
	}

	public void createNotification() {
		if (isDownloading) {
			return;
		}
		notification = new Notification();
		notification.icon = android.R.drawable.stat_sys_download;
		notification.tickerText = appName + getString(R.string.is_downing);
		notification.when = System.currentTimeMillis();
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		Intent intent = new Intent();
		notification.contentIntent = PendingIntent.getActivity(
				UpdateService.this, NOTIFICATION_ID, intent, 0);

		contentView = new RemoteViews(getPackageName(),
				R.layout.notification_item);
		contentView.setTextViewText(R.id.notificationTitle, appName
				+ getString(R.string.is_downing));
		contentView.setTextViewText(R.id.notificationPercent, "0%");
		contentView.setProgressBar(R.id.notificationProgress, 100, 0, false);
		notification.contentView = contentView;

		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(R.layout.notification_item, notification);
	}

	/***
	 * down file
	 * 
	 * @return
	 * @throws MalformedURLException
	 */
	public long downloadUpdateFile(String down_url, String file)
			throws Exception {

		int down_step = down_step_custom;
		int totalSize;
		int downloadCount = 0;
		int updateCount = 0;

		InputStream inputStream;
		OutputStream outputStream;

		URL url = new URL(down_url);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url
				.openConnection();
		httpURLConnection.setConnectTimeout(TIMEOUT);
		httpURLConnection.setReadTimeout(TIMEOUT);
		totalSize = httpURLConnection.getContentLength();

		if (httpURLConnection.getResponseCode() != 200) {
			throw new Exception("fail!");
		}

		inputStream = httpURLConnection.getInputStream();
		outputStream = new FileOutputStream(file, false);

		byte buffer[] = new byte[1024];
		int readsize = 0;

		while ((readsize = inputStream.read(buffer)) != -1) {

			outputStream.write(buffer, 0, readsize);
			downloadCount += readsize;
			if (updateCount == 0
					|| (downloadCount * 100 / totalSize - down_step) >= updateCount) {
				updateCount += down_step;
				contentView.setTextViewText(R.id.notificationPercent,
						updateCount + "%");
				contentView.setProgressBar(R.id.notificationProgress, 100,
						updateCount, false);
				notification.contentView = contentView;
				notificationManager.notify(R.layout.notification_item,
						notification);
			}
		}
		if (httpURLConnection != null) {
			httpURLConnection.disconnect();
		}
		inputStream.close();
		outputStream.close();

		return downloadCount;
	}
}