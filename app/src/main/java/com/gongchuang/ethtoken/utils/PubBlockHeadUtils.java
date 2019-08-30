package com.gongchuang.ethtoken.utils;

import android.database.Cursor;

import com.gongchuang.ethtoken.TokenApplication;
import com.gongchuang.ethtoken.domain.PerBlockHeadInfo;
import com.gongchuang.ethtoken.domain.PerBlockHeadInfoDao;
import com.gongchuang.ethtoken.domain.PubBlockHeadInfo;
import com.gongchuang.ethtoken.domain.PubBlockHeadInfoDao;

import java.util.List;

public class PubBlockHeadUtils {
    public static PubBlockHeadInfoDao pubBlockHeadInfos = TokenApplication.getsInstance().getDaoSession().getPubBlockHeadInfoDao();

    /**
     * 插入一条新j记录
     *
     */
    public static void insertNewPubBlockHead(PubBlockHeadInfo data) {
        pubBlockHeadInfos.insert(data);
    }


    /**
     * 插入多条新j记录
     *
     */
    public static void insertNewPubBlockHeads(List<PubBlockHeadInfo> datas) {
        for(int i=0;i<datas.size();i++){
            pubBlockHeadInfos.insert(datas.get(i));
        }
    }

    /**
     * 查询所有公共链头
     */
    public static List<PubBlockHeadInfo> loadAll() {
        return pubBlockHeadInfos.loadAll();
    }
    /**
     * 查询本地数据库中最新一条链头高度，用于请求更新记录
     */
    public static Long loadRecentPubHeight() {
        Long maxHeight = new Long((long)0);
        String SQL_RECENT_RECORD = "SELECT MAX( "+PubBlockHeadInfoDao.Properties.PubBlockHeight.columnName+") FROM "+PubBlockHeadInfoDao.TABLENAME;
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

    /**
     * 查询本地数据库中最新一条链头序列号
     */
    public static Long loadRecentpubBlockHeight() {
        Long maxHeight = new Long((long)0);
        String SQL_RECENT_RECORD = "SELECT MAX( "+PubBlockHeadInfoDao.Properties.PubBlockHeight.columnName+") FROM "+PubBlockHeadInfoDao.TABLENAME;
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
