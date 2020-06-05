package com.summer.helper.web;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;

public class ActivitysManager {
	private static Map<String, Activity> activityMap = new HashMap<String,Activity>();
	
	public static void  Add(String activityName,Activity activity){
		activityMap.put(activityName, activity);
	}

	public static void finish(String activityName){
		try {
			Activity activity = activityMap.remove(activityName);
			if (activity !=null) {
				activity.finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void remove(String activityName){
		try {
			 activityMap.remove(activityName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    public static Activity getActivity(String id) {
        Activity r = activityMap.get(id);
        return r != null ? r : null;
    }
	
	public static void finishAllActivity(){
		Iterator<String>  iter  = activityMap.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			Activity activity = activityMap.get(key);
			if (activity!=null) {
				activity.finish();
			}
		}
		activityMap.clear();
	}
	
	
	public static void finishAllActivity(final Map<String, Activity> activityMap){
		if(null == activityMap)return;
		Iterator<String>  iter  = activityMap.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			Activity activity = activityMap.get(key);
			if (activity!=null) {
				activity.finish();
			}
		}
		activityMap.clear();
	}
}
