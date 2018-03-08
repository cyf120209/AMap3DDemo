package com.amap.map3d.demo.trace;

import com.amap.api.trace.TraceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/2/26.
 */

public class TraceEntity {

    private List<TraceLocation> traceLocationList=new ArrayList<>();

    public List<TraceLocation> getTraceLocationList() {
        return traceLocationList;
    }

    public void setTraceLocationList(List<TraceLocation> traceLocationList) {
        this.traceLocationList = traceLocationList;
    }
}
