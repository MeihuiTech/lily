package com.mei.hui.config;

import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.MyException;
import com.mei.hui.util.SystemConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class RSAUtil {
    private static Map<Integer, String> keyMap = new HashMap<>();  //用于封装随机产生的公钥与私钥

    private static String privateKey = "MIIBUwIBADANBgkqhkiG9w0BAQEFAASCAT0wggE5AgEAAkEAoMI8nS8TWcp6kPHiglEO6mSi5JApNtXnVIVSBAsPMXIsf1ich9N2LL2iJnxdqiF8eSUUtRj7ZusfJ/0iSKNfYQIDAQABAkAUIj+VMCAsTr+rB6Bm0eAcWx5LogKHa6LdYxUyjHAKsR8NWyeTttSDVOany8Ilm5gcEmgCLNVAjwIvpr6nK3MBAiEA2mp4mgkIkb6Ms5F67v5l0cPDAPuPfHuQrFaPSphhqTUCIQC8a+LZnEjjnU4QHqlLMopVopdYcoZFHR96WGLqt21O/QIgTox13OecxlVh4n5SWLrywTKG4caWfq0thV0H4UrV7HUCIF8uWK/4xNFGW3LFKlv0MfFEaxOg9rbfGVGiaIWNF0i5AiB6xMSBo8pvB1wou8xCUCvKoRMXGHBB/QnsS4dl6kLQnQ==";

    private static String publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKDCPJ0vE1nKepDx4oJRDupkouSQKTbV51SFUgQLDzFyLH9YnIfTdiy9oiZ8XaohfHklFLUY+2brHyf9IkijX2ECAwEAAQ==";

    public static void main(String[] args) throws Exception {
        //生成公钥和私钥
        genKeyPair();
        //加密字符串
        String message = "美辉科技有限公司";
        System.out.println("随机生成的公钥为:" + keyMap.get(0));
        System.out.println("随机生成的私钥为:" + keyMap.get(1));
      /*  String messageEn = encrypt(message,keyMap.get(0));
        System.out.println(message + "\t加密后的字符串为:" + messageEn);
        String messageDe = decrypt(messageEn,keyMap.get(1));
        System.out.println("还原后的字符串为:" + messageDe);*/

    }

    /**
     * 随机生成密钥对
     */
    public static void genKeyPair() throws NoSuchAlgorithmException {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(SystemConstants.RSA);
        // 初始化密钥对生成器，密钥大小为96-1024位
        keyPairGen.initialize(1024,new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
        String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
        // 得到私钥字符串
        String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));
        // 将公钥和私钥保存到Map
        keyMap.put(0,publicKeyString);  //0表示公钥
        keyMap.put(1,privateKeyString);  //1表示私钥
    }
    /**
     * RSA公钥加密
     *
     * @param str
     *            加密字符串
     * @return 密文
     * @throws Exception
     *             加密过程中的异常信息
     */
    public static String encrypt(String str,String publicKey) throws Exception{
        //base64编码的公钥
        byte[] decoded = Base64.decodeBase64(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance(SystemConstants.RSA).generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance(SystemConstants.RSA);
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
        return outStr;
    }

    /**
     * RSA私钥解密
     *
     * @param str
     *            加密字符串
     * @return 铭文
     * @throws Exception
     *             解密过程中的异常信息
     */
    public static String decrypt(String str){
        try {
            //64位解码加密后的字符串
            byte[] inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
            //base64编码的私钥
            byte[] decoded = Base64.decodeBase64(privateKey);
            RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance(SystemConstants.RSA).generatePrivate(new PKCS8EncodedKeySpec(decoded));
            //RSA解密
            Cipher cipher = Cipher.getInstance(SystemConstants.RSA);
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            String outStr = new String(cipher.doFinal(inputByte));
            return outStr;
        } catch (Exception e) {
            log.error("解析api包体失败:",e);
            throw MyException.fail(ErrorCode.MYB_111111.getCode(),"解密失败");
        }
    }

}