package com.aliyun.oss.model;

public class MetaQueryAddress {
    private String addressLine;
    private String city;
    private String country;
    private String district;
    private String language;
    private String province;
    private String township;

    public MetaQueryAddress() {}

    public MetaQueryAddress(String addressLine, String city, String country, String district, String language, String province, String township) {
        this.addressLine = addressLine;
        this.city = city;
        this.country = country;
        this.district = district;
        this.language = language;
        this.province = province;
        this.township = township;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getTownship() {
        return township;
    }

    public void setTownship(String township) {
        this.township = township;
    }

}
