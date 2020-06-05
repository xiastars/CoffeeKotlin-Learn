package com.summer.helper.permission;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.summer.helper.utils.Logs;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;

import java.util.List;

/**
 * Created by xiastars on 2017/6/13.
 */

public class PermissionUtils {
    /**
     * 检查有没有读取权限
     *
     * @param context
     * @return
     */
    public static boolean checkReadPermission(
            final Context context) {
        return checkPermmision(context, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public static boolean checkLocationPermission(
            final Context context) {
        return checkPermmision(context, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public static boolean checkInstallPermission(
            final Context context) {

        return checkPermmision(context, Manifest.permission.REQUEST_INSTALL_PACKAGES);
    }

    public static boolean checkWritePermission(
            final Context context) {
        return checkPermmision(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public static void showPermissionDialog(Context context, String... permissions) {
        if (!checkPermission(context, permissions)) {
            PermissionDialog permissionDialog = new PermissionDialog(context, permissions);
            permissionDialog.show();
        }
    }

    /**
     * 检测权限是否打开
     */
    private static boolean checkPermission(Context context, String... permissions) {
        boolean isChecked = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String p : permissions) {
                if (ContextCompat.checkSelfPermission(context,
                        p) != PackageManager.PERMISSION_GRANTED) {
                    isChecked = false;
                }
            }

        }
        return isChecked;
    }

    /**
     * 检查有没有联系人读取权限
     *
     * @param context
     * @return
     */
    public static boolean checkPhonePermission(
            final Context context) {
        return checkPermmision(context, Manifest.permission.READ_PHONE_STATE);
    }

    /**
     * 检查有没有联系人读取权限
     *
     * @param context
     * @return
     */
    public static boolean checkAlertPermission(
            final Context context) {
        return checkPermmision(context, Manifest.permission.SYSTEM_ALERT_WINDOW);
    }

    /**
     * 检查有没有相机权限
     *
     * @param context
     * @return
     */
    public static boolean checkCameraPermission(
            final Context context) {
        return checkPermmision(context, Manifest.permission.CAMERA);
    }

    /**
     * 检查有没有录音权限
     *
     * @param context
     * @return
     */
    public static boolean checkRecordPermission(
            final Context context) {
        return checkPermmision(context, Manifest.permission.RECORD_AUDIO);
    }

    /**
     * 请求权限，拒绝时再次提醒
     *
     * @param activity
     * @param args
     */
    public static void rationRequestPermission(final Activity activity, String... args) {
        Rationale rationale = new DefaultRationale();
        Context context = activity;
        final PermissionSetting permissionSetting = new PermissionSetting(activity);
        AndPermission.with(activity)
                .permission(args)
                .rationale(rationale)//大家讲道理
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {

                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        if (AndPermission.hasAlwaysDeniedPermission(activity, permissions)) {
                            //总是拒绝
                            permissionSetting.showSetting(permissions);
                        }
                    }
                })
                .start();
    }

    private static boolean checkPermmision(final Context context, final String permission) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        Logs.i("当前版本号:"+currentAPIVersion);
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    permission) != PackageManager.PERMISSION_GRANTED) {
                Logs.i("当前版本号:"+currentAPIVersion);
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        permission)) {
                    Logs.i("当前版本号:"+currentAPIVersion);
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("请开启以下权限");
                    //alertBuilder.setMessage("读写权限");
                    alertBuilder.setPositiveButton(android.R.string.yes,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, 123);
                                }
                            });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                    Logs.i("请求权限");
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, 123);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}
