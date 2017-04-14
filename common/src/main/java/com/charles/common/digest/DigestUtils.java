package com.charles.common.digest;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by lq on 17-4-13.
 */
public class DigestUtils extends org.apache.commons.codec.digest.DigestUtils {

    public static String sha1Hex(byte[] data,int offset,int len){
        return Hex.encodeHexString(sha1(data,offset,len));
    }

    private static byte[] sha1(byte[] data,int offset,int len){
        getDigest(MessageDigestAlgorithms.SHA_1).digest (data,offset,len);
    }




    private static MessageDigest getDigest(final String algorithm){
        try {
            return MessageDigest.getInstance(algorithm);
        } catch ( NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
