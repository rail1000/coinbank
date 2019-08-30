package com.gongchuang.ethtoken.domain;


/**
 * 钱包预存款信息的对象
 *
 */

public class PreDeposit {

    private String hash;
    private String preaddress_from;
    private String preaddress_to;
    private String preamount;
    private String extractime;
    private String predemo;

    public PreDeposit(String hash,String preaddress_from,String preaddress_to, String preamount,String extractime, String predemo){

        this.hash = hash;
        this.preaddress_from = preaddress_from;
        this.preaddress_to = preaddress_to;
        this.preamount = preamount;
        this.extractime = extractime;
        this.predemo = predemo;

    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPreaddress_from() {
        return preaddress_from;
    }

    public void setPreaddress_from(String preaddress_from) {
        this.preaddress_from = preaddress_from;
    }

    public String getPreaddress_to() {
        return preaddress_to;
    }

    public void setPreaddress_to(String preaddress_to) {
        this.preaddress_to = preaddress_to;
    }

    public String getExtractime() {
        return extractime;
    }

    public void setExtractime(String extractime) {
        this.extractime = extractime;
    }

    public String getPreamount() {
        return preamount;
    }

    public void setPreamount(String preamount) {
        this.preamount = preamount;
    }

    public String getPredemo() {
        return predemo;
    }

    public void setPredemo(String predemo) {
        this.predemo = predemo;
    }

}
