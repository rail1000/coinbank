package com.gongchuang.ethtoken.utils;

import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;
import java.security.SignatureException;
import java.util.List;

public class SignUtil {

    public static String getSign(String privateKey, List<String> message) {
        ECKeyPair ek = ECKeyPair.create(Numeric.toBigInt(privateKey));
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0;i<message.size();i++){
            stringBuilder.append(message.get(i));
        }
        byte[] transtr = stringBuilder.toString().getBytes();
        Sign.SignatureData signatureData = Sign.signMessage(transtr,ek);
        String sig = Numeric.toHexString(signatureData.getR())+Numeric.toHexString(signatureData.getS());

        return sig;
    }

    public static Boolean verify(String prePublicKey, List<String> message,Sign.SignatureData signatureData) {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0;i<message.size();i++){
            stringBuilder.append(message.get(i));
        }
        byte[] transtr = stringBuilder.toString().getBytes();

        String newPublicKey = null;
        try {
            newPublicKey = Numeric.toHexStringNoPrefixZeroPadded(Sign.signedMessageToKey(transtr,signatureData),128);
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        if(newPublicKey.equals(prePublicKey)){
            return  true;
        }else{
            return  false;
        }
    }
}
