package com.aliyun.oss.model;

import java.util.ArrayList;
import java.util.List;

public class DescribeRegionsResult extends GenericResult {

    private List<RegionInfo> regionInfoList = new ArrayList<RegionInfo>();

    public List<RegionInfo> getRegionInfoList() {
        return regionInfoList;
    }

    public void setRegionInfoList(List<RegionInfo> regionInfoList) {
        this.regionInfoList = regionInfoList;
    }
}
