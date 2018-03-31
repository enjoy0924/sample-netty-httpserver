package com.altas.gateway.utils;


import com.qq.tars.support.log.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public final class ImageUtil {

    public final static void scale(File srcImageFile, File result){
        if(srcImageFile == null || !srcImageFile.exists()){
            return;
        }
        long length = srcImageFile.length();
        double scale = 1.5;
        if(length > 500*1024 && length <= 1024*1024){ //如果图片大小在(500k,1m]区间
            scale = 3;
        } else if(length > 1024*1024 && length <= 2048*1024){ //如果图片大小在(1m,2m]区间
            scale = 3.5;
        } else if (length > 2048*1024) { //图片大小超过2M
            scale = 4;
        }

        scale(srcImageFile, result, scale);
    }

    /**
     * 缩放图像（按比例缩放）
     * @param srcImageFile 源图像文件地址
     * @param result 缩放后的图像地址
     * @param scale 缩放比例
     */
    public final static void scale(File srcImageFile, File result, double scale) {

        try {
            BufferedImage src = ImageIO.read(srcImageFile); // 读入文件
            int width = src.getWidth(); // 得到源图宽
            int height = src.getHeight(); // 得到源图长
            width = Double.valueOf((new Double(width) / scale)).intValue();
            height = Double.valueOf((new Double(height) / scale)).intValue();

            Image image = src.getScaledInstance(width, height, Image.SCALE_DEFAULT);
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null); // 绘制缩小后的图
            g.dispose();
            ImageIO.write(tag, getImgFormatName(result), result);// 输出到文件流

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static String getImgFormatName(File file){
        String fullName = file.getName();
        String result = fullName.substring(fullName.indexOf(".") + 1).toUpperCase();
        return result;
    }

}
