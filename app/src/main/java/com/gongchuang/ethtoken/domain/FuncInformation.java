package com.gongchuang.ethtoken.domain;

public class FuncInformation {
    private String eTitle;
    private String eTime;
    private String eContents;

    public String geteTitle() {
        return eTitle;
    }

    public void seteTitle(String eTitle) {
        this.eTitle = eTitle;
    }

    public String geteTime() {
        return eTime;
    }

    public void seteTime(String eTime) {
        this.eTime = eTime;
    }
    public String geteContents() {
        return eContents;
    }

    public void seteContents(String eContents) {
        this.eContents = eContents;
    }
}