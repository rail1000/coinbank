package com.gongchuang.ethtoken.utils;

import com.gongchuang.ethtoken.domain.FuncInformation;

import java.util.ArrayList;
import java.util.List;

public class FuncInfosReceive {
    private static List<FuncInformation> funcInfos = new ArrayList<>();;

    public List<FuncInformation> getInfos(){
        //这里应该写从中心传来的消息部分，但此处先人为添加。
        FuncInformation funcInfo1 = new FuncInformation();
        FuncInformation funcInfo2 = new FuncInformation();
        FuncInformation funcInfo3 = new FuncInformation();
        FuncInformation funcInfo4 = new FuncInformation();
        FuncInformation funcInfo5 = new FuncInformation();

        funcInfo1.seteTitle("预付工资的管理");
        funcInfo1.seteTime("2018/8/18");
        funcInfo1.seteContents("预付工资管理应用可有效解决工资发放问题，防止工资拖欠、克扣等现象。");

        funcInfo2.seteTitle("外汇买卖");
        funcInfo2.seteTime("2018/8/18");
        funcInfo2.seteContents("外汇买卖应用提供了外汇买卖的渠道，省去银行跨境手续费，更加安全、便捷、公平。");
        funcInfo3.seteTitle("预存款");
        funcInfo3.seteTime("2018/8/18");
        funcInfo3.seteContents("预存款应用可推广至会员制消费平台，降低消费风险。");

        funcInfo4.seteTitle("押金管理");
        funcInfo4.seteTime("2018/8/18");
        funcInfo4.seteContents("押金管理功能可推广至，如共享单车押金管理、金融租赁交易管理等方面");

        funcInfo5.seteTitle("离线支付");
        funcInfo5.seteTime("2018/8/18");
        funcInfo5.seteContents("在一定时限内、一定金额可不通过银行系统的登记就可完成支付，减少生活中很多不便.");
        funcInfos.add(funcInfo1);
        funcInfos.add(funcInfo2);
        funcInfos.add(funcInfo3);
        funcInfos.add(funcInfo4);
        funcInfos.add(funcInfo5);
        return funcInfos;
    }
}
