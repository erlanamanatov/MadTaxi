package com.erkprog.madtaxi.data.entity;

public class TaxiCab {

  private String companyName;
  private String smsNum;
  private String phoneNum;
  private Double lat;
  private Double lng;

  public TaxiCab(String companyName, String smsNum, String phoneNum, Double lat, Double lng) {
    this.companyName = companyName;
    this.smsNum = smsNum;
    this.phoneNum = phoneNum;
    this.lat = lat;
    this.lng = lng;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getSmsNum() {
    return smsNum;
  }

  public void setSmsNum(String smsNum) {
    this.smsNum = smsNum;
  }

  public String getPhoneNum() {
    return phoneNum;
  }

  public void setPhoneNum(String phoneNum) {
    this.phoneNum = phoneNum;
  }

  public Double getLat() {
    return lat;
  }

  public void setLat(Double lat) {
    this.lat = lat;
  }

  public Double getLng() {
    return lng;
  }

  public void setLng(Double lng) {
    this.lng = lng;
  }

  @Override
  public String toString() {
    return "TaxiCab{" +
        "companyName='" + companyName + '\'' +
        ", smsNum='" + smsNum + '\'' +
        ", phoneNum='" + phoneNum + '\'' +
        ", lat=" + lat +
        ", lng=" + lng +
        '}';
  }
}
