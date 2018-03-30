package com.altas.gateway.utils;

import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * created on 15-4-3
 *
 * @author 王松
 */
public class Base64Util {

    public static void decodeBase64ToImage(String base64, String imgName) throws IOException{
        FileOutputStream write = new FileOutputStream(new File(imgName));
        byte[] decoderBytes = Base64.decodeBase64(base64);
        write.write(decoderBytes);
        write.flush();
        write.close();
    }

    /** 将图片文件转化为字节数组字符串，并对其进行Base64编码处理 */
    public static String encodeImgageToBase64(File imageFile) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(imageFile);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", outputStream);

        //对字节数组Base64编码
        return Base64.encodeBase64String(outputStream.toByteArray());
    }

    public static String encode(String str){
        return Base64.encodeBase64String(str.getBytes());
    }

    public static String decode(String base64){
        return new String(Base64.decodeBase64(base64));
    }
}
