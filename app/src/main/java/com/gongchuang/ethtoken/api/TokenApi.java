package com.gongchuang.ethtoken.api;

import android.util.Log;

import com.gongchuang.ethtoken.base.Constants;
import com.gongchuang.ethtoken.domain.ETHWallet;
import com.gongchuang.ethtoken.domain.PerBlockHeadInfo;
import com.gongchuang.ethtoken.domain.PreDeposit;
import com.gongchuang.ethtoken.domain.PubBlockHeadInfo;
import com.gongchuang.ethtoken.domain.TransactionRecord;
import com.gongchuang.ethtoken.utils.ETHWalletUtils;
import com.gongchuang.ethtoken.utils.PubBlockHeadUtils;
import com.gongchuang.ethtoken.utils.ToastUtils;
import com.gongchuang.ethtoken.utils.WalletDaoUtils;
import com.gongchuang.ethtoken.utils.bip44.Sha256Hash;
import com.gongchuang.ethtoken.utils.bip44.Signature;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 统一管理网络请求的API类
 */

public class TokenApi {

    private static final String TAG = "TokenApi";













    //预付工资转账
    public static boolean pay_in_advance(String walletAddress,String transfer_address,String transfer_amount,String transfer_remark,String sigdatas) {
        //进行预付工资，返回凭证是否成功。
        return true;
    }

    //停止预付工资交易
    public static boolean stop_pay_in_advance(String walletAddress,String hash) {
        //进行预付工资，返回凭证是否成功。
        return true;
    }

    //预付工资发起记录
    public static List<PreDeposit> pay_in_advanc_record(String walletAddress) {
        //进行预付工资记录查看，是否有转到这个地址的未提取工资，
        PreDeposit preDeposit1 = new PreDeposit("8ac87d21f564c6a7dd98bd05dc944f15ad0cad995d5e94e665acae2991f232c9","0xEc44776790Cb7C3C3037fBb1D92f78CC2959d49f","0x465B2F47f613d92380f9B37690EBB30bE5849550","10000.00","2018/04/21","4月预付工资");
        PreDeposit preDeposit2 = new PreDeposit("331229f4a256d798f9b82a30e2813cc6554343c90ba98c146c32cb615f1937bf","0xEc44776790Cb7C3C3037fBb1D92f78CC2959d49f","0x1a311F0092CD808eBad1B4359253a13562298C71","10000.00","2018/03/31","3月预付工资");
        List<PreDeposit> preDeposits = new ArrayList<>();
        preDeposits.add(preDeposit1);
        preDeposits.add(preDeposit2);
        return preDeposits;
    }

    //预付工资未收款记录
    public static List<PreDeposit> extract_money_record(String walletAddress) {
        //进行预付工资记录查看，是否有转到这个地址的未提取工资，
        PreDeposit preDeposit1 = new PreDeposit("206b2c4063994429f057cbeb336c777b068f51d6745297d1700093c5c21b26bb","0xDe262aFff208f7155aeaa672c7256D051f91411C","0xEc44776790Cb7C3C3037fBb1D92f78CC2959d49f","50000.00","2018/04/21","4月预付工资");
    PreDeposit preDeposit2 = new PreDeposit("77fa29c3f36729d6b757f76d9c715a3ff04ac93761d0c2f10fd952eb267ba9f8","0xEc44776790Cb7C3C3037fBb1D92f78CC2959d49f","0xEc44776790Cb7C3C3037fBb1D92f78CC2959d49f","100","2018/03/31","1月预付工资");
    List<PreDeposit> preDeposits = new ArrayList<>();
        preDeposits.add(preDeposit1);
        preDeposits.add(preDeposit2);
        return preDeposits;
}


    public void getSig(List<String> s) throws SignatureException {
        ECKeyPair ek = ETHWalletUtils.deriveECKeyPair(WalletDaoUtils.getCurrent().getId(),WalletDaoUtils.getCurrent().getPassword());
        String pubKeu =  Numeric.toHexStringNoPrefixZeroPadded(ek.getPublicKey(),64);
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0;i<s.size();i++){
            stringBuilder.append(s.get(i));
        }
        byte[] transtr = stringBuilder.toString().getBytes();
        Sign.SignatureData signatureData = Sign.signMessage(transtr,ek);

        String publicKey = Numeric.toHexStringNoPrefixZeroPadded(Sign.signedMessageToKey(transtr,signatureData),64);

        if(pubKeu.equals(publicKey)){
            ToastUtils.showToast("Sig is True");
        }else{
            ToastUtils.showToast("Sig is false");
        }
    }

}
