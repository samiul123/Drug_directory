package com.example.samiu.drug_directory;

/**
 * Created by samiu on 11/10/2017.
 */

public class Drug {
    private String image, tradeName, genericName,id,companyName;

    public Drug() {
    }

    public Drug(String tradeName, String genericName, String id, String companyName) {
        this.tradeName = tradeName;
        this.genericName = genericName;
        this.id = id;
        this.companyName = companyName;
    }


    public Drug(String image, String tradeName, String genericName, String id, String companyName) {
        this.image = image;
        this.tradeName = tradeName;
        this.genericName = genericName;
        this.id = id;
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public String getGenericName() {
        return genericName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }
}
