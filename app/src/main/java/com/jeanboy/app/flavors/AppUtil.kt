package com.jeanboy.app.flavors

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.text.TextUtils

/**
 * Created by jeanboy on 2020/9/22 15:46.
 */
object AppUtil {

    data class VersionInfo(val name: String, val code: Long)

    /**
     * 获取 App 版本信息
     */
    fun getVersionInfo(context: Context): VersionInfo {
        val packageManager = context.packageManager
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val versionName = packageInfo.versionName
        val versionCode =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) packageInfo.longVersionCode else packageInfo.versionCode
        return VersionInfo(versionName, versionCode.toLong())
    }

    /**
     * 获取 App 名称
     */
    fun getAppName(context: Context): String {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val applicationInfo = packageInfo.applicationInfo
        return context.resources.getString(applicationInfo.labelRes)
    }

    /**
     * 获取渠道名
     */
    fun getMetaData(context: Context, key: String): String? {
        val applicationInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )
        return applicationInfo.metaData.getString(key)
    }

    /**
     * 判断 App 是否在前台
     */
    fun isAppShowing(context: Context): Boolean {
        val packageName = context.packageName
        if (TextUtils.isEmpty(packageName)) return false

        val activityManager: ActivityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = activityManager.runningAppProcesses
        runningAppProcesses?.forEach { runningProcess ->
            if (packageName == runningProcess.processName
                && runningProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
            ) {
                return true
            }
        }
        return false
    }

    /**
     * 获取当前进程名
     */
    fun getCurrentProcessName(context: Context): String? {
        val pid = Process.myPid()
        val activityManager: ActivityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = activityManager.runningAppProcesses
        runningAppProcesses?.let {
            val iterator = it.iterator()
            var appProcess: ActivityManager.RunningAppProcessInfo
            do {
                if (!iterator.hasNext()) {
                    return null
                }
                appProcess = iterator.next()
            } while (appProcess.pid != pid)
            return appProcess.processName
        }
        return null
    }
}