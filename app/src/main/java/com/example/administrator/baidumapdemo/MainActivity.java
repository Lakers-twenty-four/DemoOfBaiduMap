package com.example.administrator.baidumapdemo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;

public class MainActivity extends AppCompatActivity {



    private MyLocationListener myLocationListener;
    private LocationClient mlocationClient;
    private TextureMapView mapView;
    private boolean isFirstLocate=true;
    private BaiduMap baiduMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        mapView= (TextureMapView) findViewById(R.id.mapView);
        //BaiduMap类，它是地图的总控制器，调用MapView的getmap()方法获取BaiduMap的实例
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        //创建LocationClient实例
        mlocationClient = new LocationClient(getApplicationContext());
        //注册定位监听器
        myLocationListener = new MyLocationListener();
        initLocation();
        mlocationClient.registerLocationListener(myLocationListener);
        //开始定位
        mlocationClient.start();


    }

    private void initLocation() {
        LocationClientOption option= new LocationClientOption();
        option.setScanSpan(5000);
        //setIsNeedAddress(true);--表示我们需要获取当前位置详细地地址信息
        option.setIsNeedAddress(true);
        mlocationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            System.out.println(bdLocation.getLatitude()+"   "+bdLocation.getLongitude()+"    "+bdLocation.getCity());
            navigateTo(bdLocation);
        }
        @Override
        public void onConnectHotSpotMessage(String s, int i) {
        }
    }

    private void navigateTo(BDLocation bdLocation){
        //其实LatLng并没有什么太多的用法，主要就是用来存放经纬度
        if(isFirstLocate){
            LatLng currentLoc = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
            MapStatusUpdate update= MapStatusUpdateFactory.newLatLng(currentLoc);
            baiduMap.animateMapStatus(update);
            update=MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
            myDialog(bdLocation);
            isFirstLocate=false;
        }
        MyLocationData.Builder locationBuilder=new MyLocationData.Builder();
        locationBuilder.latitude(bdLocation.getLatitude());
        locationBuilder.longitude(bdLocation.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
       mapView.onDestroy();
       mlocationClient.stop();
        baiduMap.setMyLocationEnabled(false);
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapView.onPause();
    }

    public void myDialog(BDLocation bdLocation){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("当前地理位置");
        dialog.setMessage("经度："+bdLocation.getLatitude()+"\n"+
                "纬度："+bdLocation.getLongitude()+"\n"+
                "国家："+bdLocation.getCountry()+"\n"+
                "省："+bdLocation.getProvince()+"\n"+
                "市："+bdLocation.getCity()+"\n"+
                "区："+bdLocation.getDistrict()+"\n"+
                "街道："+bdLocation.getStreet()+"\n"
        );
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this,"进入科蜜丁小晨地图",Toast.LENGTH_SHORT).show();
            }
        });

        dialog.setNegativeButton("取消",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onDestroy();
            }
        });

        //最后一步，一定要记住  和Toast一样一定要show()出来
        dialog.show();
    }

}


