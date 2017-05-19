package com.dulikaifa.zhitianweather.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

import java.util.List;

@SuppressWarnings("ALL")
public class ServiceStateUtils {
	
	public static boolean isRunningService(Context context , String serviceName){
		//  用来管理四大组建的
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		
		// 得到最新的正在运行的服务
		List<RunningServiceInfo> runningServices = am.getRunningServices(100);
		for (RunningServiceInfo runningServiceInfo : runningServices) {
			String className = runningServiceInfo.service.getClassName();
			
			if(serviceName.equals(className)){
				// 服务正在运行
				return true;
			}
		}
		
		return false;
	}

}
