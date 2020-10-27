package com.example.administrator.markercluster;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

/**
 * Created by Administrator on 2018/4/8.
 */

public class Util {

    private static Activity mcontext;

    public Util(Activity context) {
        mcontext=context;
    }


    public static int getWidth(){
        WindowManager wm = mcontext.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static int getHeight(){
        WindowManager wm = mcontext.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /*是否在中心点*/
    public static boolean compareLat(BaiduMap mBaiduMap,LatLng mlatlng){
        if (mcontext==null)
            return false;
        /*百度地图转换的的左上角经纬度*/
        Point pt = new Point();
        pt.x = 0;
        pt.y = 0;
        LatLng ll = mBaiduMap.getProjection().fromScreenLocation(pt);

        /*百度地图转换的右下角经纬度*/
        Point ptr = new Point();
        ptr.x = getWidth();
        ptr.y = getHeight();
        LatLng llr = mBaiduMap.getProjection().fromScreenLocation(ptr);
        /*如果经度大于左上*/
        if(llr.latitude<mlatlng.latitude&&mlatlng.latitude<ll.latitude&&ll.longitude<mlatlng.longitude&&mlatlng.longitude<llr.longitude){
           return true;
        }
        return false;
    }

}
