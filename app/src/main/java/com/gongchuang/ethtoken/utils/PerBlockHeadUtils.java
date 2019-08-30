package com.gongchuang.ethtoken.utils;

import android.database.Cursor;

import com.gongchuang.ethtoken.TokenApplication;
import com.gongchuang.ethtoken.domain.PerBlockHeadInfo;
import com.gongchuang.ethtoken.domain.PerBlockHeadInfoDao;

import java.util.ArrayList;
import java.util.List;

public class PerBlockHeadUtils {
    public static PerBlockHeadInfoDao perBlockHeadInfos = TokenApplication.getsInstance().getDaoSession().getPerBlockHeadInfoDao();

    /**
     * 插入一条新j记录
     *
     */
    public static void insertNewPerBlockHead(PerBlockHeadInfo data) {
        perBlockHeadInfos.insert(data);
    }


    /**
     * 插入多条新j记录
     *
     */
    public static void insertNewPerBlockHeads(List<PerBlockHeadInfo> datas) {
        for(int i=0;i<datas.size();i++){
            perBlockHeadInfos.insert(datas.get(i));
        }
    }

    /**
     * 查询所有个人链头
     */
    public static List<PerBlockHeadInfo> loadAll() {
        return perBlockHeadInfos.loadAll();
    }

    /**
     * 查询本地数据库中最新一条链头高度，用于请求更新记录
     */
    public static Long loadRecentPerHeight() {
        Long maxHeight = new Long((long)0);
        String SQL_RECENT_RECORD = "SELECT MAX( "+PerBlockHeadInfoDao.Properties.PerBlockHeight.columnName+") FROM "+PerBlockHeadInfoDao.TABLENAME;
        Cursor c = TokenApplication.getsInstance().getDaoSession().getDatabase().rawQuery(SQL_RECENT_RECORD, null);
        try{
            if (c.moveToFirst()) {
                do {
                    maxHeight = c.getLong(0);
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return maxHeight;
    }
}
