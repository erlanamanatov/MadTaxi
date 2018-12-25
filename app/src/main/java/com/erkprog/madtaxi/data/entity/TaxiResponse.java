
package com.erkprog.madtaxi.data.entity;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TaxiResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("companies")
    @Expose
    private List<Company> companies = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

}
