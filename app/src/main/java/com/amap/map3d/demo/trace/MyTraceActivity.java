package com.amap.map3d.demo.trace;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceListener;
import com.amap.api.trace.TraceLocation;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.util.Rxbus;

import io.reactivex.functions.Consumer;

public class MyTraceActivity extends Activity implements View.OnClickListener {

    private Button mGraspButton, mCleanFinishOverlay;

    private MapView mMapView;
    private AMap mAMap;

    PolylineOptions polylineOptions = new PolylineOptions();
    private static String mDistanceString, mStopTimeString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trace);
        mGraspButton = (Button) findViewById(R.id.grasp_button);
        mCleanFinishOverlay = (Button) findViewById(R.id.clean_finish_overlay_button);
        mCleanFinishOverlay.setOnClickListener(this);
        mGraspButton.setOnClickListener(this);
        mMapView = (MapView) findViewById(R.id.my_map);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
//        mLowSpeedShow = (TextView) findViewById(R.id.show_low_speed);
////        mRecordChoose = (Spinner) findViewById(R.id.record_choose);
//        mDistanceString = getResources().getString(R.string.distance);
//        mStopTimeString = getResources().getString(R.string.stop_time);
////        mCoordinateTypeGroup = (RadioGroup) findViewById(R.id.coordinate_type_group);
////        mCoordinateTypeGroup.setOnCheckedChangeListener(this);
        init();
        startService(new Intent(this,MyTraceService.class));
        Rxbus.getDefault().toObservable(AMapLocation.class)
                .subscribe(new Consumer<AMapLocation>() {
                    @Override
                    public void accept(AMapLocation aMapLocation) throws Exception {
                        TraceLocation traceLocation = new TraceLocation();
                        traceLocation.setLatitude(aMapLocation.getLatitude());
                        traceLocation.setLongitude(aMapLocation.getLongitude());
                        traceLocation.setTime(aMapLocation.getTime());
                        traceLocation.setSpeed(aMapLocation.getSpeed());

                        LatLng startLocation_gps = new LatLng(traceLocation.getLatitude(), traceLocation.getLongitude());

                        polylineOptions.add(startLocation_gps);
                        mAMap.addPolyline(polylineOptions);

//                        MarkerOptions markerOption = new MarkerOptions();
//                        markerOption.position(startLocation_gps);
//                        markerOption.draggable(false);//设置Marker可拖动
//                        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
//                                .decodeResource(getResources(), R.drawable.marker_blue)));
//                        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
//                        markerOption.setFlat(true);//设置marker平贴地图效果
//                        mAMap.addMarker(markerOption);
                    }
                });
    }

    /**
     * 初始化
     */
    private void init() {
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            setUpMap();
        }
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 如果要设置定位的默认状态，可以在此处进行设置
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        mAMap.setMyLocationStyle(myLocationStyle);
        mAMap.getUiSettings().setRotateGesturesEnabled(false);
        mAMap.getUiSettings().setZoomControlsEnabled(false);
        mAMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        polylineOptions.width(10)
                .color(Color.argb(255, 0, 255, 0));
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }
}
