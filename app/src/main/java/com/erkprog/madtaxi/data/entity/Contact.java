
package com.erkprog.madtaxi.data.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Contact {

  public static final String TYPE_SMS = "sms";
  public static final String TYPE_PHONE = "phone";

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("contact")
    @Expose
    private String contact;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

}
