package com.alr.imageCode;


import com.alr.core.utils.JsonHelper;
import com.alr.core.utils.LoggerHelper;
import com.alr.gateway.constant.CONST;
import com.alr.gateway.session.Session;
import com.alr.gateway.session.SessionManager;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.Random;

/**
 * <p>
 * Created by JianjunTang
 * <p>
 * Date: 2015/4/20
 * <p>
 * Time: 14:24
 */
public class RandomValidateCode {
    private Random random = new Random();

    private final int width;//图片宽
    private final int height;//图片高
    private final int lineSize;//干扰线数量
    private final int stringNum;//随机产生字符数量
    private final String randString = "123456789";//随机产生的字符串;//随机产生的字符串

    public RandomValidateCode(int width, int height, int lineSize, int stringNum) {
        this.width = width;
        this.height = height;
        this.lineSize = lineSize;
        this.stringNum = stringNum;
    }

    /**
     * 获得字体
     *
     * @return
     */
    private Font getFont() {
        return new Font("Arial", Font.CENTER_BASELINE, 18);
    }

    /**
     * 获得颜色
     *
     * @param fc
     * @param bc
     * @return
     */
    private Color getRandColor(int fc, int bc) {
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc - 16);
        int g = fc + random.nextInt(bc - fc - 14);
        int b = fc + random.nextInt(bc - fc - 18);
        return new Color(r, g, b);
    }

    /**
     * 生成随机图片
     */
    public void getRandomCode(Session session, ByteArrayOutputStream byteArrayOutputStream) {
        //BufferedImage类是具有缓冲区的Image类,Image类是用于描述图像信息的类
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        Graphics g = image.getGraphics();//产生Image对象的Graphics对象,改对象可以在图像上进行各种绘制操作
        g.fillRect(0, 0, width, height);

        Font f = g.getFont();
        g.setFont(new Font(f.getName(), f.getStyle(), 20));

        g.setColor(getRandColor(110, 133));
        //绘制干扰线
        for (int i = 0; i <= lineSize; i++) {
            drawLine(g);
        }
        //绘制随机字符
        String randomString = "";
        for (int i = 1; i <= stringNum; i++) {
            randomString = drawString(g, randomString, i);
        }

        /**将验证码放入session中*/
        session.addAttribute(CONST.KEY_IMG_VALIDATE_CODE, randomString);
        SessionManager.instance().refreshSession(session);
        g.dispose();
        try {
            ImageIO.write(image, "JPEG", byteArrayOutputStream);//将内存中的图片通过流动形式输出到客户端
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 绘制字符串
     *
     * @param g
     * @param randomString
     * @param i
     * @return
     */
    private String drawString(Graphics g, String randomString, int i) {
        g.setFont(getFont());
        g.setColor(new Color(random.nextInt(101), random.nextInt(111), random.nextInt(121)));
        String rand = String.valueOf(getRandomString(random.nextInt(randString.length())));
        randomString += rand;
        g.translate(random.nextInt(3), random.nextInt(3));
        g.drawString(rand, 13 * i, 16);
        return randomString;
    }

    /**
     * 绘制干扰线
     *
     * @param g
     */
    private void drawLine(Graphics g) {
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        int xl = random.nextInt(13);
        int yl = random.nextInt(15);
        g.drawLine(x, y, x + xl, y + yl);
    }

    /**
     * 获取随机的字符
     *
     * @param num
     * @return
     */
    public String getRandomString(int num) {
        return String.valueOf(randString.charAt(num));
    }
}
