package com.summer.helper.server;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.webkit.WebSettings;

import com.summer.helper.permission.PermissionUtils;
import com.summer.helper.utils.ILGChannel;
import com.summer.helper.utils.Logs;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * Created by xiaqiliang on 2017/4/21.
 */

public class PostData {

    // 手机品牌
    public static String MANUFACTURER = Build.MANUFACTURER;
    // 手机型号
    public static String MODEL = Build.MODEL;
    // 操作系统
    public static String OS = "Android";
    // 操作系统版本号
    public static String VERSION_OS = Build.VERSION.SDK;
    // 手机唯一标识码
    public static String IMEI = "";
    // 项目版本号：
    public static String VERSION_PRO = null;
    // 标识是哪个应用市场
    public static String CHANNEL = ILGChannel.SELF;
    // 上下文
    public static Context context;
    // 返回当前手机语言 en or cn
    public static String language = null;
    //应用版本号
    public static int VERSIONCODE;
    //请求Token
    public static String TOKEN = "";
    //用户id
    public static long USER_ID = 0;
    //MAC地址
    public static String MAC_INFO = null;

    //阿里云照片头
    public static String OOSHEAD = "http";

    //阿里云的KEY
    public static String ALI_KEY;

    //阿里云的上传地址
    public static String ALI_URL;

    //阿里云的访问策略
    public static String ALI_POLICY;

    //阿里云的上传签名
    public static String ALI_SIGNATURE;

    //访问阿里文件的位置
    public static String ALI_PRE;

    //登录时，把上一个界面的类名传过来
    public static String USER_URL;

    public static String SESSION;
    public static String MAC;

    public static String UNIC_CODE;

    //登录时传的参数
    public static SummerParameter getLoginParameters(Context context) {
        if (VERSION_PRO == null) {
            getVersionInfo(context);
        }
        SummerParameter params = new SummerParameter();
        if (!TextUtils.isEmpty(USER_URL)) {
            params.put("userUrl", USER_URL);
            USER_URL = null;
        }
        return params;
    }

    public static void getVersionInfo(Context context) {
        PackageManager manager;
        PackageInfo info = null;
        manager = context.getPackageManager();
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
            VERSIONCODE = info.versionCode;
            VERSION_PRO = info.versionName;
            Logs.i("VERSION_INFO:" + VERSIONCODE + "," + VERSION_PRO);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getMac() {
        if(MAC != null){
            return MAC;
        }
        String mac = null;
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    mac = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
        MAC = mac;
        return MAC;
    }

    // 获取必须的基本参数
    public static SummerParameter getPostParameters(Context context) {
        if (VERSION_PRO == null) {
            getVersionInfo(context);
        }
        SummerParameter params = new SummerParameter();
        return params;
    }

    /**
     * 获取手机厂商唯一识别号(IMEI)
     *
     * @return
     */
    public static String getIMIE(Context context) {
        {
            if (TextUtils.isEmpty(PostData.IMEI)) {
                try {
                    TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    if(PermissionUtils.checkPhonePermission(context)){
                        PostData.IMEI = telManager.getDeviceId();
                    }

                    Logs.i("IMEI" + PostData.IMEI);
                } catch (Exception e) {

                    Logs.i("IMEI" + e.toString());
                }

            }
            return PostData.IMEI;
        }

    }

    public static String getUserAgent() {
        String userAgent = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(context);
            } catch (Exception e) {
                userAgent = System.getProperty("http.agent");
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
