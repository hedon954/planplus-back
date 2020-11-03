package common.util;

import org.apache.tomcat.util.codec.binary.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.util.Arrays;
/**
 * 通过百度小程序中的 SessionKey 来解密用户信息
 *
 * @author Hedon Wang
 * @create 2020-11-03 22:20
 */
public class GetUserInfoFromSessionKey {

    private static Charset CHARSET = Charset.forName("utf-8");

    /**
     * 对密文进行解密
     *
     * @param text 需要解密的密文
     *
     * @return 解密得到的明文
     *
     * @throws Exception 异常错误信息
     */
    public String decrypt(String text, String sessionKey,String ivStr) throws Exception {
        byte[] aesKey = Base64.decodeBase64(sessionKey + "=");
        byte[] original;
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
            byte[] ivBytes = Base64.decodeBase64(ivStr);
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
            byte[] encrypted = Base64.decodeBase64(text);
            original = cipher.doFinal(encrypted);
        } catch (Exception e) {
            throw new Exception(e);
        }
        String xmlContent;
        String fromClientId;
        try {
            // 去除补位字符
            byte[] bytes = PKCS7Encoder.decode(original);
            // 分离16位随机字符串,网络字节序和ClientId
            byte[] networkOrder = Arrays.copyOfRange(bytes, 16, 20);
            int xmlLength = recoverNetworkBytesOrder(networkOrder);
            xmlContent = new String(Arrays.copyOfRange(bytes, 20, 20 + xmlLength), CHARSET);
            fromClientId = new String(Arrays.copyOfRange(bytes, 20 + xmlLength, bytes.length), CHARSET);
        } catch (Exception e) {
            throw new Exception(e);
        }
        return xmlContent;
    }

    public static String getType(Object test) {
        return test.getClass().getName().toString();

    }

    /**
     * 还原4个字节的网络字节序
     *
     * @param orderBytes 字节码
     *
     * @return sourceNumber
     */
    private int recoverNetworkBytesOrder(byte[] orderBytes) {
        int sourceNumber = 0;
        int length = 4;
        int number = 8;
        for (int i = 0; i < length; i++) {
            sourceNumber <<= number;
            sourceNumber |= orderBytes[i] & 0xff;
        }
        return sourceNumber;
    }

    /**
     * 加密解密demo
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String dy = "toMIrTrp2WovaM4RUoqsrBX4kR7p5JThrzY8bW4ZTBKm4YPRr0CfxY8ZZFwk0RJIPEVCVNebRuN3h6zOIHrHrjdvz5hcKkRfX3VO4OfoHJ3LiZv5uVRl6056iLBgNm+x2HY6T07A40aKeYJQDT3kmgdaAi3UB7NUlrEFUpAuZ2Tsm5B+bF3lnbmUzhskTCFE";

        String sessionKey = "a28bea08d86e426f8d51e024194eae6f";
        String iv = "a28bea08d86e426f8d51ew==";

        GetUserInfoFromSessionKey demo = new GetUserInfoFromSessionKey();
        String dd = demo.decrypt(dy, sessionKey, iv);
        System.out.println(dd);
    }

}


class PKCS7Encoder {

    static Charset CHARSET = Charset.forName("utf-8");
    static int BLOCK_SIZE = 32;

    /**
     * 获得对明文进行补位填充的字节.
     *
     * @param count 需要进行填充补位操作的明文字节个数
     *
     * @return 补齐用的字节数组
     */
    static byte[] encode(int count) {
        // 计算需要填充的位数
        int amountToPad = BLOCK_SIZE - (count % BLOCK_SIZE);
        if (amountToPad == 0) {
            amountToPad = BLOCK_SIZE;
        }
        // 获得补位所用的字符
        char padChr = chr(amountToPad);
        String tmp = new String();
        for (int index = 0; index < amountToPad; index++) {
            tmp += padChr;
        }
        return tmp.getBytes(CHARSET);
    }

    /**
     * 删除解密后明文的补位字符
     *
     * @param decrypted 解密后的明文
     *
     * @return 删除补位字符后的明文
     */
    static byte[] decode(byte[] decrypted) {
        int pad = (int) decrypted[decrypted.length - 1];
        if (pad < 1 || pad > 32) {
            pad = 0;
        }
        return Arrays.copyOfRange(decrypted, 0, decrypted.length - pad);
    }

    /**
     * 将数字转化成ASCII码对应的字符，用于对明文进行补码
     *
     * @param a 需要转化的数字
     *
     * @return 转化得到的字符
     */
    static char chr(int a) {
        byte target = (byte) (a & 0xFF);
        return (char) target;
    }

}

