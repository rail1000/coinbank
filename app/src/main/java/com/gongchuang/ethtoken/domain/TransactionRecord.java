package com.gongchuang.ethtoken.domain;

public class TransactionRecord {

    private String txDate;
    private String txHash;
    private String type;
    private String addressFrom;
    private String addressTo;
    private String amount;
    private String note;//备注

    public TransactionRecord(String txDate,String txHash,String type,String addressFrom,String addressTo,String amount,String note){
        this.txDate = txDate;
        this.txHash = txHash;
        this.type = type;
        this.addressFrom = addressFrom;
        this.addressTo = addressTo;
        this.amount = amount;
        this.note = note;
    }

    public String getTxDate() {
        return txDate;
    }

    public void setTxDate(String txDate) {
        this.txDate = txDate;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddressFrom() {
        return addressFrom;
    }

    public void setAddressFrom(String addressFrom) {
        this.addressFrom = addressFrom;
    }

    public String getAddressTo() {
        return addressTo;
    }

    public void setAddressTo(String addressTo) {
        this.addressTo = addressTo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


}
