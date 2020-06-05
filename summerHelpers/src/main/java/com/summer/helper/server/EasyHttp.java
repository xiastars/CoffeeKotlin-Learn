package com.summer.helper.server;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.CookiePersistor;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.summer.helper.downloader.DownloadManager;
import com.summer.helper.downloader.DownloadStatus;
import com.summer.helper.downloader.DownloadTask;
import com.summer.helper.downloader.DownloadTaskListener;
import com.summer.helper.listener.OnResponseListener;
import com.summer.helper.utils.Logs;
import com.summer.helper.utils.SFileUtils;
import com.summer.helper.utils.STextUtils;
import com.summer.helper.utils.SThread;
import com.summer.helper.utils.SUtils;
import com.summer.helper.utils.TLSSocketFactory;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.builder.OtherRequestBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.SocketTimeoutException;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public final class EasyHttp {

	public static final String METHOD = "POST";
	public static final String METHOD_GET = "GET";
	static OkHttpClient okHttpClient;
	static OkHttpClient.Builder mBuilder;
	static CookiePersistor persistor;
	static ClearableCookieJar cookieJar1;

	public static void init(Context context) {
		PostData.getVersionInfo(context);
		if (okHttpClient == null) {
			persistor = new SharedPrefsCookiePersistor(context);
			cookieJar1 = new PersistentCookieJar(new SetCookieCache(), persistor);
			mBuilder = new OkHttpClient.Builder()
					.connectTimeout(10000, TimeUnit.MILLISECONDS)
					.readTimeout(10000, TimeUnit.MILLISECONDS)
					.writeTimeout(10000, TimeUnit.MILLISECONDS)
					.retryOnConnectionFailure(false)
					.cookieJar(cookieJar1)
					.hostnameVerifier(new HostnameVerifier() {
						@Override
						public boolean verify(String hostname, SSLSession session) {
							return true;
						}
					});
			okHttpClient = mBuilder.build();
			//设置最大并发量
			okHttpClient.dispatcher().setMaxRequestsPerHost(10);
			//其他配置
			OkHttpUtils.getInstance(okHttpClient);
		}
	}

	public static void clearCookies() {
		if (cookieJar1 != null) {
			cookieJar1.clear();
		}
	}

	/**
	 * POST请求，数据注入到类里
	 *
	 * @param context
	 * @param url        请求链接
	 * @param clazz      注入的类
	 * @param parameters 参数对
	 * @param callBack
	 * @param <T>
	 */
	public static <T> void post(Context context, String token, String url, final Class<T> clazz,
								final SummerParameter parameters, final RequestCallback<T> callBack) {
		if (!SUtils.isNetworkAvailable(context)) {
			if (callBack != null) {
				callBack.onError(ErrorCode.ERR_CONENCCT, "网络未连接,请连接后重试!");
			}
			return;
		}
		requestPost(context, token, url, clazz, parameters, callBack, METHOD);
	}

	/**
	 * POST请求，数据注入到类里
	 *
	 * @param context
	 * @param url        请求链接
	 * @param clazz      注入的类
	 * @param parameters 参数对
	 * @param callBack
	 * @param <T>
	 */
	public static <T> void get(Context context, String token, String url, final Class<T> clazz,
							   final SummerParameter parameters, final RequestCallback<T> callBack) {
		if (!SUtils.isNetworkAvailable(context)) {
			callBack.onError(ErrorCode.ERR_CONENCCT, "网络未连接,请连接后重试!");
			return;
		}
		requestGet(context, token, url, clazz, parameters, callBack, METHOD_GET);
	}

	public static <T> void put(Context context, String token, String url, final Class<T> clazz,
							   final SummerParameter parameters, final RequestCallback<T> callBack) {
		if (!SUtils.isNetworkAvailable(context)) {
			callBack.onError(ErrorCode.ERR_CONENCCT, "网络未连接,请连接后重试!");
			return;
		}
		requestPut(context, token, url, clazz, parameters, callBack);
	}

	public static <T> void delete(Context context, String token, String url, final Class<T> clazz,
								  final SummerParameter parameters, final RequestCallback<T> callBack) {
		if (!SUtils.isNetworkAvailable(context)) {
			callBack.onError(ErrorCode.ERR_CONENCCT, "网络未连接,请连接后重试!");
			return;
		}
		requestDelete(context, token, url, clazz, parameters, callBack);
	}

	/**
	 * 不处理数据
	 *
	 * @param context
	 * @param url        请求链接
	 * @param parameters
	 * @param listener
	 */
	public static void get(Context context, String url, final SummerParameter parameters, RequestListener listener) {
		if (!SUtils.isNetworkAvailable(context)) {
			listener.onErrorException(new SummerException());
			return;
		}
		request(url, parameters, listener);
	}

	public static <T> void get(Context context, String token, String action, String url, final Class<T> clazz,
							   final SummerParameter parameters, final RequestCallback<T> callBack) {
		if (null != parameters) parameters.putLog(action);
		CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
		requestGet(context, token, url, clazz, parameters, callBack, METHOD_GET);
	}

	private static <T> void requestPost(final Context context, String token, String url, final Class<T> clazz, final SummerParameter parameters, final RequestCallback<T> callBack, String methodGet) {
		setCoockies(context);
		FormBody.Builder mBuidler = new FormBody.Builder();
		RequestBody requestBody;
		Set<String> params = parameters.keySet();
		String logInfo = "";
		Request.Builder formBody = new Request.Builder().url(url);
		for (String key : params) {
			if (key.equals("requestType")) {
				logInfo = (String) parameters.get(key);
			} else {
				Object value = parameters.get(key);
				if (value != null) {
					mBuidler.add(key, value + "");
				}
			}
		}
		formBody.header("User-Agent", PostData.getUserAgent()).header("Connection", "close");
		formBody.header("appVersion", PostData.VERSION_PRO);
		if (!TextUtils.isEmpty(PostData.MANUFACTURER) && STextUtils.isChineseStr(PostData.MANUFACTURER)) {
			formBody.header("manufacturer", STextUtils.getPinYin(PostData.MANUFACTURER));
		} else {
			formBody.header("manufacturer", PostData.MANUFACTURER);
		}
		formBody.header("model", PostData.MODEL);
		if(token != null){
			formBody.header("token", token);
		}
		formBody.header("os", PostData.OS + "_" + PostData.VERSION_OS);
		formBody.header("language", "cn");
		formBody.header("channel", PostData.CHANNEL);

		if (PostData.MAC_INFO == null) {
			PostData.MAC_INFO = PostData.getMac();
		}
		PostData.MAC_INFO = PostData.MAC_INFO == null ? "" : PostData.MAC_INFO;
		PostData.IMEI = PostData.getIMIE(context);
		String uniquecode = STextUtils.md5(PostData.getMac() + "_" + PostData.IMEI + "_hxq");
		PostData.UNIC_CODE = uniquecode;
		formBody.header("uniquecode", uniquecode);
		formBody.header("mac", PostData.MAC_INFO);
		requestBody = mBuidler.build();
		if (Logs.isDebug) {
			parameters.encodeUrlAndLog(url);
		}
		formBody.post(requestBody);

		final String finalLogInfo = logInfo;
		try {
			if (okHttpClient == null) {
				init(context);
			}
			okHttpClient.newCall(formBody.build()).enqueue(new Callback() {

				@Override
				public void onResponse(Call arg0, Response arg1) throws IOException {
					ResponseBody body = arg1.body();
					if (body == null/* || (context instanceof Activity && ((Activity) context).isFinishing())*/) {
						Logs.i("body is null");
						return;
					}
					final String response = body.string();
					SThread.getIntances().runOnUIThreadIfNeed(context, new Runnable() {
						@Override
						public void run() {
							try {
								if (clazz == String.class) {
									@SuppressWarnings("unchecked")
									T t = (T) response;
									if (callBack != null) {
										callBack.done(t);
									}
									return;
								}
								T t = JSON.parseObject(response, clazz);
								if (callBack != null) {
									callBack.done(t);
								}
							} catch (Exception e) {
								Logs.i( "exception:" + e.toString());
								if (callBack != null) {
									callBack.onError(ErrorCode.INVALID_JSON, "无效的数据格式");
								}
//                                e.printStackTrace();
							}
						}
					});
					Logs.i("请求结果:" + finalLogInfo + response);
				}

				@Override
				public void onFailure(final Call arg0, final IOException arg1) {
					SThread.getIntances().runOnUIThreadIfNeed(context, new Runnable() {
						@Override
						public void run() {
							if (arg1 instanceof SocketTimeoutException) {
								if (callBack != null) {
									callBack.onError(ErrorCode.ERR_TIMEOUT, "请求超时");
								}
							} else {
								if (callBack != null) {
									callBack.onError(ErrorCode.ERR_OTHER, "其它错误" + arg1.getMessage() + ",,," + arg0.toString());
								}
								if (Logs.isDebug) {
									//SUtils.makeToast(context, "请求失败,请稍后重试!");
								}
							}
						}
					});
				}
			});
		} catch (OutOfMemoryError e) {
			callBack.onError(ErrorCode.ERR_LOWMEMORY, "内存不足");
			e.printStackTrace();
		}
	}

	/**
	 * 处理GET
	 *
	 * @param context
	 * @param url
	 * @param clazz
	 * @param parameters
	 * @param callBack
	 * @param methodGet
	 * @param <T>
	 */
	private static <T> void requestGet(final Context context, String token, String url, final Class<T> clazz, final SummerParameter parameters, final RequestCallback<T> callBack, String methodGet) {
		GetBuilder utils = OkHttpUtils.get().url(url);
		Set<String> params = parameters.keySet();
		String logInfo = "";
		for (String key : params) {
			if (!key.equals("requestType")) {
				Object value = parameters.get(key);
				if (value != null) {
					utils.addParams(key, value + "");
				}
			} else {
				logInfo = (String) parameters.get(key);
			}
		}
		if (Logs.isDebug) {
			parameters.encodeUrlAndLog(url);
		}
		final String finalLogInfo = logInfo;
		utils.addHeader("User-Agent", PostData.getUserAgent());
		utils.addHeader("Content-Type", "application/json");
		utils.addHeader("User-Agent", PostData.getUserAgent());
		utils.addHeader("Connection", "close");
		utils.addHeader("appVersion", PostData.VERSION_PRO);
		utils.addHeader("manufacturer", PostData.MANUFACTURER);
		utils.addHeader("model", PostData.MODEL);
		if(token != null){
			utils.addHeader("token", token);
		}
		utils.addHeader("os", PostData.OS + "_" + PostData.VERSION_OS);
		utils.addHeader("language", "cn");
		utils.addHeader("channel", PostData.CHANNEL);

		if (PostData.MAC_INFO == null) {
			PostData.MAC_INFO = PostData.getMac();
		}
		PostData.MAC_INFO = PostData.MAC_INFO == null ? "" : PostData.MAC_INFO;
		PostData.IMEI = PostData.getIMIE(context);
		String uniquecode = STextUtils.md5(PostData.getMac() + "_" + PostData.IMEI + "_nf");
		PostData.UNIC_CODE = uniquecode;
		utils.addHeader("uniquecode", uniquecode);
		utils.addHeader("mac", PostData.MAC_INFO);
		utils.build().execute(new StringCallback() {
			@Override
			public void onResponse(String response) {
				Logs.i("请求结果:" + finalLogInfo + response);
				if (clazz == String.class) {
					@SuppressWarnings("unchecked")
					T t = (T) response;
					callBack.done(t);
					return;
				}
				try {
					T t = JSON.parseObject(response, clazz);
					callBack.done(t);
				} catch (Exception e) {
					Logs.i("exception:" + e.toString());
					callBack.onError(ErrorCode.INVALID_JSON, "无效的数据格式");
//                    e.printStackTrace();
				}

			}

			@Override
			public void onError(Call arg0, Exception arg1) {
				if (arg1 instanceof SocketTimeoutException) {
					if (Logs.isDebug) {
						// SUtils.makeToast(context, "请求超时");
					}
					callBack.onError(ErrorCode.ERR_TIMEOUT, "请求超时");
				} else {
					callBack.onError(ErrorCode.ERR_OTHER, "请求错误" + arg1);
					if (Logs.isDebug) {
						//SUtils.makeToast(context, "请求失败,请稍后重试!");
					}
				}
			}
		});
	}

	/**
	 * 处理GET
	 *
	 * @param context
	 * @param url
	 * @param clazz
	 * @param parameters
	 * @param callBack
	 * @param <T>
	 */
	private static <T> void requestPut(final Context context, String token, String url, final Class<T> clazz, final SummerParameter parameters, final RequestCallback<T> callBack) {
		OtherRequestBuilder utils = OkHttpUtils.put().url(url);
		requestOther(context, token, url, clazz, parameters, callBack, utils);
	}

	/**
	 * 删除操作
	 *
	 * @param context
	 * @param token
	 * @param url
	 * @param clazz
	 * @param parameters
	 * @param callBack
	 * @param <T>
	 */
	private static <T> void requestDelete(final Context context, String token, String url, final Class<T> clazz, final SummerParameter parameters, final RequestCallback<T> callBack) {
		OtherRequestBuilder utils = OkHttpUtils.delete().url(url);
		requestOther(context, token, url, clazz, parameters, callBack, utils);
	}

	private static <T> void requestOther(final Context context, String token, String url, final Class<T> clazz, final SummerParameter parameters, final RequestCallback<T> callBack, OtherRequestBuilder utils) {

		if (Logs.isDebug) {
			parameters.encodeUrlAndLog(url);
		}
		FormBody.Builder mBuidler = new FormBody.Builder();
		RequestBody requestBody;
		Set<String> params = parameters.keySet();
		String logInfo = "";
		for (String key : params) {
			if (key.equals("requestType")) {
				logInfo = (String) parameters.get(key);
			} else {
				Object value = parameters.get(key);
				if (value != null) {
					mBuidler.add(key, value + "");
				}
			}
		}
		requestBody = mBuidler.build();
		if (Logs.isDebug) {
			parameters.encodeUrlAndLog(url);
		}
		utils.requestBody(requestBody);
		Logs.i("re2questBOdy:" + requestBody);

		final String finalLogInfo = logInfo;
		utils.addHeader("User-Agent", PostData.getUserAgent());
		utils.addHeader("Content-Type", "application/x-www-form-urlencoded");
		utils.addHeader("User-Agent", PostData.getUserAgent());
		utils.addHeader("Connection", "close");
		utils.addHeader("appVersion", PostData.VERSION_PRO);
		utils.addHeader("manufacturer", PostData.MANUFACTURER);
		utils.addHeader("model", PostData.MODEL);
		utils.addHeader("token", token);
		utils.addHeader("os", PostData.OS + "_" + PostData.VERSION_OS);
		utils.addHeader("language", "cn");
		utils.addHeader("channel", PostData.CHANNEL);
		Logs.i("utils:" + utils);
		if (PostData.MAC_INFO == null) {
			PostData.MAC_INFO = PostData.getMac();
		}
		PostData.MAC_INFO = PostData.MAC_INFO == null ? "" : PostData.MAC_INFO;
		PostData.IMEI = PostData.getIMIE(context);
		String uniquecode = STextUtils.md5(PostData.getMac() + "_" + PostData.IMEI + "_hxq");
		PostData.UNIC_CODE = uniquecode;
		utils.addHeader("uniquecode", uniquecode);
		utils.addHeader("mac", PostData.MAC_INFO);
		utils.build().execute(new StringCallback() {
			@Override
			public void onResponse(String response) {
				Logs.i("请求结果:" + finalLogInfo + response);
				if (clazz == String.class) {
					@SuppressWarnings("unchecked")
					T t = (T) response;
					callBack.done(t);
					return;
				}
				try {
					T t = JSON.parseObject(response, clazz);
					callBack.done(t);
				} catch (Exception e) {
					Logs.i("exception:" + e.toString());
					callBack.onError(ErrorCode.INVALID_JSON, "无效的数据格式");
//                    e.printStackTrace();
				}

			}

			@Override
			public void onError(Call arg0, Exception arg1) {
				if (arg1 instanceof SocketTimeoutException) {
					if (Logs.isDebug) {
						// SUtils.makeToast(context, "请求超时");
					}
					callBack.onError(ErrorCode.ERR_TIMEOUT, "请求超时");
				} else {
					callBack.onError(ErrorCode.ERR_OTHER, "请求错误" + arg1);
					if (Logs.isDebug) {
						//SUtils.makeToast(context, "请求失败,请稍后重试!");
					}
				}
			}
		});
	}

	private static void setCoockies(final Context context) {
		if (!TextUtils.isEmpty(PostData.TOKEN)) {
			return;
		}
       /* if (persistor == null) {
            return;
        }
        List<Cookie> cookies = persistor.loadAll();
        if (cookies == null) {
            return;
        }
        int cookiesSize = cookies.size();
        for (int i = 0; i < cookiesSize; i++) {
            Cookie cookie = cookies.get(i);
            String name = cookie.name();
            String value = cookie.value();
            if (!TextUtils.isEmpty(name)) {
                if (name.equals("TOKEN")) {
                    PostData.TOKEN = value;
                    SUtils.saveStringData(context, "TOKEN", PostData.TOKEN);
                }
            }
        }*/
	}

	/**
	 * 上传文件
	 *
	 * @param context
	 * @param fileDirectory 上传到阿里云对应的文件位置与名称
	 * @param fileType      文件类型
	 * @param filePath      文件本地路径
	 * @param listener
	 */
	public static void upLoadFile(Context context, String fileDirectory, String fileType, String filePath, final OnResponseListener listener) {
		try {
			//setCoockies(context);
			MultipartBody.Builder builder = new MultipartBody.Builder();
			final String key = fileDirectory + "/" + System.nanoTime() + fileType;
			builder.addFormDataPart("key", key);
			builder.addFormDataPart("token", PostData.ALI_POLICY);
			builder.addFormDataPart("OSSAccessKeyId", PostData.ALI_KEY);
			builder.addFormDataPart("success_action_status", "200");
			//设置类型
			builder.setType(MultipartBody.FORM);
			//追加参数
			File file = new File(filePath);
			byte[] sendDatas = null;
			Bitmap mBitmap = null;
			if (fileType.endsWith(SFileUtils.FileType.FILE_MP4)) {
				builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file));
			} else {
				int degree = getExifOrientation(file.getPath());
				Bitmap lastBitmap = SUtils.createScaleBitmap(file.getPath(), 600, 600);

				if (degree != 0) {
					Matrix matrix = new Matrix();
					matrix.postRotate(degree);

					lastBitmap = Bitmap.createBitmap(lastBitmap, 0, 0, lastBitmap.getWidth(), lastBitmap.getHeight(), matrix, true);
				}
				mBitmap = lastBitmap;
				sendDatas = SUtils.getBitmapArrays(lastBitmap);

			}
			if (sendDatas != null) {
				builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/png"), sendDatas));
			}
			//创建RequestBody
			RequestBody body = builder.build();
			//创建Request
			final Request request = new Request.Builder().url(PostData.ALI_URL).post(body).build();

			//单独设置参数 比如读取超时时间
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
					TrustManagerFactory.getDefaultAlgorithm());
			KeyStore s = null;
			trustManagerFactory.init(s);
			TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
			if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
				throw new IllegalStateException("Unexpected default trust managers:"
						+ Arrays.toString(trustManagers));
			}

			final Call call = mBuilder.sslSocketFactory(new TLSSocketFactory())
					.writeTimeout(50, TimeUnit.SECONDS)
					.readTimeout(50, TimeUnit.SECONDS)
					.build().newCall(request);
			final Bitmap finalMBitmap = mBitmap;
			call.enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					Logs.i("文件上传失败:" + e + "," + call);
					if (finalMBitmap != null) {
						finalMBitmap.recycle();
					}
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					if (finalMBitmap != null) {
						finalMBitmap.recycle();
					}
					if (response.isSuccessful()) {
						int code = response.code();
						if (code == 200) {
							listener.succeed(PostData.ALI_PRE + key);
							return;
						}
					}
					listener.failure();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			listener.failure();
			Logs.i("文件上传失败:" + e);
		}
	}

	private static int getExifOrientation(String filepath) {
		int degree = 0;
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(filepath);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		if (exif != null) {
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
			if (orientation != -1) {
				switch (orientation) {
					case ExifInterface.ORIENTATION_ROTATE_90:
						degree = 90;
						break;
					case ExifInterface.ORIENTATION_ROTATE_180:
						degree = 180;
						break;
					case ExifInterface.ORIENTATION_ROTATE_270:
						degree = 270;
						break;
					default:
						break;
				}
			}
		}
		Logs.i("degress:" + degree);

		return degree;

	}

	private static void request(String url, final SummerParameter parameters, final RequestListener listener) {
		GetBuilder utils = OkHttpUtils.get().url(url);
		Set<String> params = parameters.keySet();
		for (String key : params) {
			utils.addParams(key, parameters.get(key) + "");
		}
		parameters.encodeUrl(url);
		utils.build().execute(new StringCallback() {
			@Override
			public void onResponse(String response) {
				listener.onComplete(response);
			}

			@Override
			public void onError(Call arg0, Exception arg1) {
			}
		});
	}

	/**
	 * 下载文件,使用URL作为ID
	 *
	 * @param url
	 * @param path
	 * @param fileName
	 */
	public static void download(Context context, String url, String path, String fileName, DownloadTaskListener callBack) {
		DownloadManager manager = DownloadManager.getInstance(context);
		DownloadTask task = new DownloadTask(url, path, fileName);
		manager.addDownloadTask(task, callBack);
	}

	/**
	 * 下载文件,使用给定ID
	 *
	 * @param url
	 * @param path
	 * @param fileName
	 */
	public static void download(Context context, String id, String url, String path, String fileName, DownloadTaskListener callBack) {
		DownloadManager manager = DownloadManager.getInstance(context);
		DownloadTask task = new DownloadTask(id, url, path, fileName);
		manager.addDownloadTask(task, callBack);
	}

	/**
	 * 检查是否在下载
	 *
	 * @param context
	 * @param url
	 */
	public static boolean existDownload(Context context, String url) {
		DownloadManager manager = DownloadManager.getInstance(context);
		return manager.getCurrentTaskById(url) != null;
	}

	/**
	 * 删除下载
	 *
	 * @param context
	 * @param url
	 */
	public static void deleteDownload(Context context, String url) {
		DownloadManager manager = DownloadManager.getInstance(context);
		if (manager.getCurrentTaskById(url) != null) {
			manager.cancel(url);
		}
	}

	/**
	 * 暂停下载
	 *
	 * @param context
	 * @param url
	 */
	public static void pauseDownload(Context context, String url) {
		DownloadManager manager = DownloadManager.getInstance(context);
		if (manager.getCurrentTaskById(url) != null) {
			manager.pause(url);
		}
	}

	/**
	 * 继续下载
	 *
	 * @param context
	 * @param url
	 */
	public static void resumeDownload(Context context, String url) {
		DownloadManager manager = DownloadManager.getInstance(context);
		if (manager.getCurrentTaskById(url) != null) {
			manager.resume(url);
		}
	}

	/**
	 * 检查是否暂停,返回下载进度值
	 */
	public static float checkPaused(Context context, String url) {
		DownloadManager manager = DownloadManager.getInstance(context);
		DownloadTask task = manager.getCurrentTaskById(url);
		if (task != null) {
			if (task.getPercent() != 0 && task.getDownloadStatus() == DownloadStatus.DOWNLOAD_STATUS_PAUSE) {
				return task.getPercent();
			}
		}
		return 0;
	}

}
