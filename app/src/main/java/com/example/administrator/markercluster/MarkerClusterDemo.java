/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.example.administrator.markercluster;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.com.baidu.mapapi.clusterutil.clustering.Cluster;
import com.com.baidu.mapapi.clusterutil.clustering.ClusterItem;
import com.com.baidu.mapapi.clusterutil.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 此Demo用来说明点聚合功能
 */
public class MarkerClusterDemo extends Activity implements BaiduMap.OnMapLoadedCallback {

    MapView mMapView;
    BaiduMap mBaiduMap;
    MapStatus ms;
    private ClusterManager<MyItem> mClusterManager;

    //is_List是否是列表数据
    boolean m_stop = true;
    private ReadThread mReadThread;
    /*中间偏上的视图*/
    LinearLayout lertop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_cluster_demo);
        new Util(this);
        mMapView = (MapView) findViewById(R.id.bmapView);
        lertop = findViewById(R.id.lerview);
        ms = new MapStatus.Builder().target(new LatLng(39.914935, 116.403119)).zoom(6).build();
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setOnMapLoadedCallback(this);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
        // 定义点聚合管理类ClusterManager
        mClusterManager = new ClusterManager<>(this, mBaiduMap);
        // 添加Marker点
        addMarkers();
        // 设置地图监听，当地图状态发生改变时，进行点聚合运算
        mBaiduMap.setOnMapStatusChangeListener(mClusterManager);
        // 设置maker点击时的响应
        mBaiduMap.setOnMarkerClickListener(mClusterManager);

        /*拖动时隐藏视图*/
        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                lertop.setVisibility(View.GONE);
            }
        });

        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItem>() {
            @Override
            public boolean onClusterClick(Cluster<MyItem> cluster) {
                Toast.makeText(MarkerClusterDemo.this,
                        "有" + cluster.getSize() + "个点", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
            @Override
            public boolean onClusterItemClick(MyItem item) {
                Toast.makeText(MarkerClusterDemo.this,
                        "点击单个Item", Toast.LENGTH_SHORT).show();

                //点击时把这个点移动到屏幕中间
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory
                        .newMapStatus(new MapStatus.Builder()
                                .zoom(mBaiduMap.getMapStatus().zoom + 1)
                                .target(item.getPosition())
                                .build()));
                lertop.setVisibility(View.VISIBLE);
                return false;
            }
        });

        mReadThread = new ReadThread();
        mReadThread.start();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        m_stop=false;
        super.onDestroy();
    }

    /**
     * 向地图添加Marker点
     */
    public void addMarkers() {

        mClusterManager.clearItems();
        // 添加Marker点
//        LatLng llA = new LatLng(39.963175, 116.400244);
//        LatLng llB = new LatLng(39.942821, 116.369199);
//        LatLng llC = new LatLng(39.939723, 116.425541);
//        LatLng llD = new LatLng(39.906965, 116.401394);
//        LatLng llE = new LatLng(39.956965, 116.331394);
//        LatLng llF = new LatLng(39.886965, 116.441394);
        LatLng llG = new LatLng(39.996965, 116.411394);


        List<MyItem> items = new ArrayList<MyItem>();
//        items.add(new MyItem(llA));
//        items.add(new MyItem(llB));
//        items.add(new MyItem(llC));
//        items.add(new MyItem(llD));
//        items.add(new MyItem(llE));
//
//        items.add(new MyItem(llF));
        items.add(new MyItem(llG));
        int[] rint = produceNum(1,30000,10000);
        for (int i : rint) {
            llG=new LatLng(39.996965+i*0.0001,116.411394+i*0.0001);
            items.add(new MyItem(llG));
        }
        mClusterManager.addItems(items);

    }

    /**
     * 每个Marker点，包含Marker点坐标以及图标
     */
    public class MyItem implements ClusterItem {
        private final LatLng mPosition;

        private final String carnum="";

        public MyItem(LatLng latLng) {
            mPosition = latLng;
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        @Override
        public BitmapDescriptor getBitmapDescriptor() {
            /*更改样式参考*/
            //https://blog.csdn.net/u010635353/article/details/52386097
            View view = LayoutInflater.from(MarkerClusterDemo.this).inflate(R.layout.test_mark,null);
            return BitmapDescriptorFactory.fromView(view);
//                    .fromResource();
        }
    }

    @Override
    public void onMapLoaded() {
        // TODO Auto-generated method stub
        ms = new MapStatus.Builder().zoom(9).build();
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
    }

    /*线程15秒刷新*/
    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (m_stop) {
                try {
                    sleep(15 * 1000);
//                    List<MyItem> items = new ArrayList<>();
//                    LatLng llE = new LatLng(39.943175, 116.410244);
//                    items.add(new MyItem(llE));
//                    mClusterManager.addItems(items);
//                    mClusterManager.getItems().re;
                    addMarkers();
                    mClusterManager.cluster();
                    Log.e("tag--","refresh--");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (!m_stop){
                mReadThread = null;
            }
        }

    }

    /**
     * 产生随机数字
     * @param minNum 最小数字
     * @param maxNum 最大数字
     * @param numCount 产生的数字个数
     * @return 结果数组
     */
    public int[] produceNum(int minNum, int maxNum, int numCount) {

        // 入参校验
        // 如果随机数的个数大于产生随机数的范围；或最大数小于最小数
        // 直接返回null，说明入参不符合要求
        if (numCount > (maxNum - minNum + 1) || maxNum < minNum) {
            return null;
        }

        // 存放结果的数组
        int[] resultArr = new int[numCount];

        // count 记录已产生的随机数的个数
        int count = 0;

        while(count < numCount) {

            // 产生随机数
            int num = (int) (Math.random() * (maxNum - minNum)) + minNum;

            // flag 定义本次产生的随机数是否已在数组中
            boolean flag = true;

            // 遍历数组中已产生的随机数
            for (int i=0; i<count; i++) {

                // 同本次产生的随机数最比较
                if (num == resultArr[i]) {

                    // 如果已存在相同的值，则跳出for循环，继续外层的while循环，产生下一个随机数
                    flag = false;
                    break;
                }
            }

            // 如果本次产生的随机数在数组中不存在，则将该随机数存放在数组中
            if (flag) {
                resultArr[count] = num;

                // 数组中已产生的随机数个数加1
                count++;
            }
        }

        return resultArr;
    }


}
