package com.companies.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class CompanyDTO {
    private String companyName;
    private String companyNumber;
    private String companyAddress;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public CompanyDTO(String companyName, String companyNumber, String companyAddress) {
        this.companyName = companyName;
        this.companyNumber = companyNumber;
        this.companyAddress = companyAddress;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }
}
