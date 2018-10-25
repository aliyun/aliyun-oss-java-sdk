package com.aliyun.oss.model;

import java.util.List;

/**
 * Created by jingdan on 2018/9/18.
 */
public class ListUserRegionsResult extends GenericResult {

    private List<Region> Regions;

    public List<Region> getRegions() {
        return Regions;
    }

    public void setRegions(List<Region> regions) {
        Regions = regions;
    }
}
