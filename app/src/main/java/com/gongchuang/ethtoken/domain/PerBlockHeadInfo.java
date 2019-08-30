package com.gongchuang.ethtoken.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class PerBlockHeadInfo {
    @Id(autoincrement = true)
    private Long perBlockHeight;
    private String perBlockHash;
    private String perBlockdate;
    @Generated(hash = 490662618)
    public PerBlockHeadInfo(Long perBlockHeight, String perBlockHash,
            String perBlockdate) {
        this.perBlockHeight = perBlockHeight;
        this.perBlockHash = perBlockHash;
        this.perBlockdate = perBlockdate;
    }
    @Generated(hash = 1985541512)
    public PerBlockHeadInfo() {
    }
    public Long getPerBlockHeight() {
        return this.perBlockHeight;
    }
    public void setPerBlockHeight(Long perBlockHeight) {
        this.perBlockHeight = perBlockHeight;
    }
    public String getPerBlockHash() {
        return this.perBlockHash;
    }
    public void setPerBlockHash(String perBlockHash) {
        this.perBlockHash = perBlockHash;
    }
    public String getPerBlockdate() {
        return this.perBlockdate;
    }
    public void setPerBlockdate(String perBlockdate) {
        this.perBlockdate = perBlockdate;
    }
}
