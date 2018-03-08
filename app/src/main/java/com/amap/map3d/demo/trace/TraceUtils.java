package com.amap.map3d.demo.trace;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.trace.TraceLocation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2018/2/25.
 */

public class TraceUtils {

    private String filterString;
    private boolean isFirst=true;
    private TraceLocation weight1 = new TraceLocation();
    private TraceLocation weight2;
    private List<TraceLocation> w1TempList = new LinkedList<>();
    private List<TraceLocation> w2TempList = new LinkedList<>();
    private List<TraceLocation> mListPoint = new LinkedList<>();
    private int w1Count;
    private long CAR_MAX_SPEED=100;

    /**
     *
     * @param aMapLocation
     * @return 是否获得有效点，需要存储和计算距离
     */
    public boolean filterPos(AMapLocation aMapLocation){
        SimpleDateFormat df= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Date date= new Date(aMapLocation.getTime());
        String time= df.format(date);//定位时间
        filterString =time + "开始虑点"+ "\r\n";

        // 获取的第一个定位点不进行过滤
        if(isFirst){
            isFirst=false;
            weight1.setLatitude(aMapLocation.getLatitude());
            weight1.setLongitude(aMapLocation.getLongitude());
            weight1.setTime(aMapLocation.getTime());
            weight1.setSpeed(aMapLocation.getSpeed());

            filterString +="第一次定位"+ "\r\n";

            // 将得到的第一个点存储入w1的缓存集合
            TraceLocation traceLocation = new TraceLocation();
            traceLocation.setLatitude(aMapLocation.getLatitude());
            traceLocation.setLongitude(aMapLocation.getLongitude());
            traceLocation.setTime(aMapLocation.getTime());
            w1TempList.add(traceLocation);
            w1Count++;
            return true;
        }else{
            filterString +="非第一次定位"+ "\r\n";
            // 过滤静止时的偏点，在静止时速度小于1米就算做静止状态
            if (aMapLocation.getSpeed() <1) {
                return false;
            }
            if(weight2==null){
                // 计算w1与当前定位点p1的时间差并得到最大偏移距离D
                long offsetTimeMils=aMapLocation.getTime()-weight1.getTime();
                long offsetTimes=offsetTimeMils/1000;
                long MaxDistance=offsetTimes*CAR_MAX_SPEED;
                float distance= AMapUtils.calculateLineDistance(
                        new LatLng(weight1.getLatitude(),weight1.getLongitude()),
                        new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude())
                );
                if(distance>MaxDistance){
                    // 将设置w2位新的点，并存储入w2临时缓存
                    weight2=new TraceLocation();
                    weight2.setLatitude(aMapLocation.getLatitude());
                    weight2.setLongitude(aMapLocation.getLongitude());
                    weight2.setTime(aMapLocation.getTime());
                    w2TempList.add(weight2);
                    return false;
                }else {
                    // 将p1加入到做坐标集合w1TempList
                    TraceLocation traceLocation = new TraceLocation();
                    traceLocation.setLatitude(aMapLocation.getLatitude());
                    traceLocation.setLongitude(aMapLocation.getLongitude());
                    traceLocation.setTime(aMapLocation.getTime());
                    w1TempList.add(traceLocation);
                    w1Count++;

                    // 更新w1权值点
                    weight1.setLatitude(weight1.getLatitude() * 0.2 + aMapLocation.getLatitude() * 0.8);
                    weight1.setLongitude(weight1.getLongitude() * 0.2 + aMapLocation.getLongitude() * 0.8);
                    weight1.setTime(aMapLocation.getTime());
                    weight1.setSpeed(aMapLocation.getSpeed());

                    if(w1TempList.size()>3){
                        // 将w1TempList中的数据放入finalList，并将w1TempList清空
                        mListPoint.addAll(w1TempList);
                        w1TempList.clear();
                        return true;
                    }else {
                        return false;
                    }

                }

            }else {
                // 计算w2与当前定位点p1的时间差并得到最大偏移距离D
                long offsetTimeMils=aMapLocation.getTime()-weight2.getTime();
                long offsetTimes=offsetTimeMils/1000;
                long MaxDistance=offsetTimes*16;
                float distance= AMapUtils.calculateLineDistance(
                        new LatLng(weight2.getLatitude(),weight2.getLongitude()),
                        new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude())
                );
                if(distance>MaxDistance){
                    w2TempList.clear();
                    // 将设置w2位新的点，并存储入w2临时缓存
                    weight2 =new TraceLocation();
                    weight2.setLatitude(aMapLocation.getLatitude());
                    weight2.setLongitude(aMapLocation.getLongitude());
                    weight2.setTime(aMapLocation.getTime());
                    weight2.setSpeed(aMapLocation.getSpeed());

                    w2TempList.add(weight2);

                    return false;

                }else {
                    // 将p1加入到做坐标集合w2TempList
                    TraceLocation traceLocation=new TraceLocation();
                    traceLocation.setLatitude(aMapLocation.getLatitude());
                    traceLocation.setLongitude(aMapLocation.getLongitude());
                    traceLocation.setTime(aMapLocation.getTime());
                    traceLocation.setSpeed(aMapLocation.getSpeed());
                    w2TempList.add(traceLocation);

                    // 更新w2权值点
                    weight2.setLatitude(weight2.getLatitude() * 0.2 + aMapLocation.getLatitude() *0.8);
                    weight2.setLongitude(weight2.getLongitude() * 0.2 + aMapLocation.getLongitude() *0.8);
                    weight2.setTime(aMapLocation.getTime());
                    weight2.setSpeed(aMapLocation.getSpeed());

                    if(w2TempList.size()>4){
                        // 判断w1所代表的定位点数是否>4,小于说明w1之前的点为从一开始就有偏移的点
                        if(w1Count>4){
                            mListPoint.addAll(w1TempList);
                        }else {
                            w1TempList.clear();
                        }

                        mListPoint.addAll(w2TempList);

                        // 1、清空w2TempList集合 2、更新w1的权值点为w2的值 3、将w2置为null
                        w2TempList.clear();
                        weight1 =weight2;
                        weight2 =null;
                        return true;
                    }else {
                        return false;
                    }
                }


            }



        }
    }
}
