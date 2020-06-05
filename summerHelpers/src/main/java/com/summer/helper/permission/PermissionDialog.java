package com.summer.helper.permission;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.malata.summer.helper.R;
import com.summer.helper.dialog.BaseCenterDialog;

import java.lang.ref.WeakReference;

/**
 * @Description: 权限提示
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/6/19 15:42
 */
public class PermissionDialog extends BaseCenterDialog {

    TextView tvTitle;
    LinearLayout llContainer;
    String[] permissions;
    MyHandler myHandler;

    public PermissionDialog(@NonNull Context context, String... permissions) {
        super(context);
        this.permissions = permissions;
        myHandler = new MyHandler(this);
    }

    @Override
    public int setContainerView() {
        return R.layout.dialog_permission;
    }

    @Override
    public void initView(View view) {
        tvTitle = view.findViewById(R.id.tv_title);
        llContainer = view.findViewById(R.id.ll_container);
        myHandler.sendEmptyMessageDelayed(0, 1000);
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                addPermissionView();
            }
        }, 400);

    }

    private void addPermissionView() {
        for (String p : permissions) {
            if (p.equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                addPermissionView(p, R.string.permission_name_location, R.string.permission_location_detail);

            } else if (p.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                addPermissionView(p, R.string.permission_name_write_storage, R.string.permission_storage_detail);
            }
        }
    }

    private void addPermissionView(final String permission, int titleRes, int detail) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_permission, null);
        llContainer.addView(view);
        TextView tvTitle = view.findViewById(R.id.tv_permission);
        tvTitle.setText(titleRes);
        TextView tvDetail = view.findViewById(R.id.tv_detail);
        tvDetail.setText(detail);
        Switch vSwitch = view.findViewById(R.id.view_switch);
        vSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    Intent gpsIntent = new Intent();
                    gpsIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
                    gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
                    gpsIntent.setData(Uri.parse("custom:3"));
                    try {
                        PendingIntent.getBroadcast(context, 0, gpsIntent, 0).send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }
                ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, 123);
            }
        });

    }


    class MyHandler extends Handler {
        private final WeakReference<PermissionDialog> mActivity;

        public MyHandler(PermissionDialog activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            PermissionDialog activity = mActivity.get();
            if (null != activity) {
                switch (msg.what) {
                    default:
                        activity.handleMsg(msg.what, msg.obj);
                }
            }
        }
    }

    /**
     * 检测权限是否打开
     */
    private boolean checkPermission() {
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

    private void handleMsg(int msg, Object obj) {
        switch (msg) {
            case 0:
                if (checkPermission()) {
                    myHandler.removeMessages(0);
                    cancelDialog();
                } else {
                    myHandler.sendEmptyMessageDelayed(0, 1000);
                }
                break;
        }
    }


}
