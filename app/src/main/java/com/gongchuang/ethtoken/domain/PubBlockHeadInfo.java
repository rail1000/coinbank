package com.gongchuang.ethtoken.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class PubBlockHeadInfo {
    @Id(autoincrement = true)
    private Long pubBlockHeight;

    private String pubBlockHash;
    private String pubMerkleRoot;
    @Generated(hash = 403486399)
    public PubBlockHeadInfo(Long pubBlockHeight, String pubBlockHash,
            String pubMerkleRoot) {
        this.pubBlockHeight = pubBlockHeight;
        this.pubBlockHash = pubBlockHash;
        this.pubMerkleRoot = pubMerkleRoot;
    }
    @Generated(hash = 746067981)
    public PubBlockHeadInfo() {
    }
    public Long getPubBlockHeight() {
        return this.pubBlockHeight;
    }
    public void setPubBlockHeight(Long pubBlockHeight) {
        this.pubBlockHeight = pubBlockHeight;
    }
    public String getPubBlockHash() {
        return this.pubBlockHash;
    }
    public void setPubBlockHash(String pubBlockHash) {
        this.pubBlockHash = pubBlockHash;
    }
    public String getPubMerkleRoot() {
        return this.pubMerkleRoot;
    }
    public void setPubMerkleRoot(String pubMerkleRoot) {
        this.pubMerkleRoot = pubMerkleRoot;
    }
}
