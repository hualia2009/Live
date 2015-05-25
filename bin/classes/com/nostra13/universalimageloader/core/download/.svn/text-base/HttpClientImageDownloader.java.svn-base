/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nostra13.universalimageloader.core.download;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Implementation of ImageDownloader which uses {@link HttpClient} for image stream retrieving.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.4.1
 * TODO:zhuchen 20140310 把原来游戏大全的实现拷贝到这的
 */
public class HttpClientImageDownloader extends BaseImageDownloader {

	/*private HttpClient httpClient;

	public HttpClientImageDownloader(Context context, HttpClient httpClient) {
		super(context);
		this.httpClient = httpClient;
	}*/
	
	private static final int MAX_CONN = 90;
	private static final int MAX_ROUTE = 20;
	private static final int CHECK_PERIOD = 120000;
	// for 2g network, adjust timeout to 12 second
	private static final int CONN_TIMEOUT = 12000;
	private static final int SOKET_TIMEOUT = 12000;
	private ThreadSafeClientConnManager mClientConnManager;
	private HttpParams mHttpParams = new BasicHttpParams();
	private long mClientCheckTime = System.currentTimeMillis();
	
	public HttpClientImageDownloader(Context context) {
		super(context);
		fitHttpParams(context, mHttpParams);

        // Create and initialize scheme registry
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new org.apache.http.conn.scheme.Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        schemeRegistry.register(new org.apache.http.conn.scheme.Scheme("https", SSLSocketFactory
                .getSocketFactory(), 443));

        mClientConnManager = new ThreadSafeClientConnManager(mHttpParams, schemeRegistry);
	}

	@Override
	protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {
		/*
		HttpGet httpRequest = new HttpGet(imageUri);
		HttpResponse response = httpClient.execute(httpRequest);
		HttpEntity entity = response.getEntity();
		BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
		return bufHttpEntity.getContent();
		*/
		
		long curTime = System.currentTimeMillis();
	    if (mClientCheckTime + CHECK_PERIOD < curTime) {
	        mClientConnManager.closeExpiredConnections();
	        mClientConnManager.closeIdleConnections(30, TimeUnit.SECONDS);
	        mClientCheckTime = curTime;
	    }
	    HttpClient httpClient = new DefaultHttpClient(mClientConnManager, mHttpParams);
		HttpGet httpRequest = new HttpGet(imageUri.toString());
		httpRequest.addHeader("Referer", "http://www.baidu.com");
		HttpResponse response = httpClient.execute(httpRequest);
		HttpEntity entity = response.getEntity();
		BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
		return bufHttpEntity.getContent();
	}
	
	public void fitHttpParams(final Context context, final HttpParams httpParams) {
	    ConnManagerParams.setMaxTotalConnections(httpParams, MAX_CONN);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRoute() {
            @Override
            public int getMaxForRoute(HttpRoute route) {
                return MAX_ROUTE;
            }
        });

        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
        HttpConnectionParams.setConnectionTimeout(httpParams, CONN_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParams, SOKET_TIMEOUT);

		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo networkInfo = connectivityManager
				.getActiveNetworkInfo();
		if (networkInfo == null || networkInfo.getExtraInfo() == null) {
			return;
		}

		// 3gnet/3gwap/uninet/uniwap/cmnet/cmwap/ctnet/ctwap
		String info = networkInfo.getExtraInfo().toLowerCase(Locale.ENGLISH);
		// 先根据网络apn信息判断,并进行 proxy 自动补齐
		if (info != null) {
			if (info.startsWith("cmwap") || info.startsWith("uniwap")
					|| info.startsWith("3gwap")) {
				HttpHost proxy = new HttpHost("10.0.0.172", 80);
				httpParams.setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
				return;
			} else if (info.startsWith("ctwap")) {
				HttpHost proxy = new HttpHost("10.0.0.200", 80);
				httpParams.setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
				return;
			} else if (info.startsWith("cmnet") || info.startsWith("uninet")
					|| info.startsWith("ctnet") || info.startsWith("3gnet")) {
				return;
			}
		}

		// 如果没有 apn 信息，则根据 proxy代理判断。
		// 由于android 4.2 对 "content://telephony/carriers/preferapn"
		// 读取进行了限制，我们通过系统接口获取。

		// 此两个方法是deprecated的，但在4.2下仍可用
		String defaultProxyHost = android.net.Proxy.getDefaultHost();
		int defaultProxyPort = android.net.Proxy.getDefaultPort();

		if (defaultProxyHost != null && defaultProxyHost.length() > 0) {
			/*
			 * 无法根据 proxy host 还原 apn 名字 这里不设置 mApn
			 */
			if ("10.0.0.172".equals(defaultProxyHost.trim())) {
				// 当前网络连接类型为cmwap || uniwap
				HttpHost proxy = new HttpHost("10.0.0.172", defaultProxyPort);
				httpParams.setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
			} else if ("10.0.0.200".equals(defaultProxyHost.trim())) {
				HttpHost proxy = new HttpHost("10.0.0.200", 80);
				httpParams.setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
			} else {
			}
		} else {
			// 其它网络都看作是net
		}
	}
}
