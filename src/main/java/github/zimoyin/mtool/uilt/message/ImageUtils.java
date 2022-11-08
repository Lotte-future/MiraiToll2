package github.zimoyin.mtool.uilt.message;

import github.zimoyin.mtool.uilt.net.httpclient.HttpClientUtils;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * 图片工具：用来格式化可发送图片和发送图片
 */
public class ImageUtils {
    private static Logger logger = LoggerFactory.getLogger(ImageUtils.class);//日志
//    private static Logger logger = LoggerExp.getLogger(ImageUtils.class);

    private ImageUtils() {
    }

    /**
     * 发送图片
     *
     * @param inputStream 图片流
     * @param contact
     */
    public static void sendImage(InputStream inputStream, Contact contact) {
        long s = System.currentTimeMillis();
        contact.sendMessage(getImage(inputStream, contact));
        long e = System.currentTimeMillis();
        logger.debug(" :[系统日志]"+contact.getId()+" <--发送图片用时: "+((e-s)/1000)+" s");
    }

    /**
     * 发送图片
     *
     * @param url     图片地址
     * @param contact
     */
    public static void sendImage(String url, Contact contact) throws IOException {
        long s = System.currentTimeMillis();
        contact.sendMessage(getImage(url, contact));
        long e = System.currentTimeMillis();
        logger.debug(" :[系统日志]"+contact.getId()+" <--发送图片用时: "+((e-s)/1000)+" s");
    }


    /**
     * 返回可发送的图片
     *
     * @param inputStream 图片流
     * @param contact
     * @return
     */
    public static Image getImage(InputStream inputStream, Contact contact) {
        ExternalResource externalResource = null;
        try {
            long s = System.currentTimeMillis();
            //创建外部资源
            externalResource = ExternalResource.create(inputStream);
            Image image1 = contact.uploadImage(externalResource);
            long e = System.currentTimeMillis();
            logger.debug(" :[系统日志]"+image1.getImageId()+" (ID)<--获取格式化后的图片(流)用时: "+((e-s)/1000)+" s");
            return image1;
        } catch (IOException e) {
            logger.error("获取图片时发生了异常", e);
        } catch (Exception e) {
            logger.error("获取图片时发生了异常",e);
        } finally {
            try {
                if (externalResource != null) {
                    externalResource.close();
                }
            } catch (IOException e) {
                logger.error("关闭图片输入流时发生了异常", e);
            }
        }
        return null;
    }

    /**
     * 返回可发送的图片（构建一个图片对象）
     *
     * @param url     图片地址
     * @param contact
     * @return
     */
    public static Image getImage(String url, Contact contact) throws IOException {
        ExternalResource externalResource = null;
//        InputStream inputStream = HttpDownload.getDownloadInputStream(url);//获取网络流
        InputStream inputStream = HttpClientUtils.doGet(url).getInputStream();
        try {
            long s = System.currentTimeMillis();
            //创建外部资源
            externalResource = ExternalResource.create(inputStream);
            Image image1 = contact.uploadImage(externalResource);
            long e = System.currentTimeMillis();
            logger.debug(" :[系统日志]"+image1.getImageId()+" (ID)<--获取格式化的图片(流)用时: "+((e-s)/1000)+" s");
            return image1;
        } catch (IOException e) {
            logger.error("获取图片时发生了异常", e);
        } catch (Exception e) {
            logger.error("获取图片时发生了异常,url可能出现了错误请检查图片url： " + url);
        } finally {
            try {
                if (externalResource != null) {
                    externalResource.close();
                }
            } catch (Exception e) {
                logger.error("关闭图片输入流时发生了异常", e);
            }
        }
        return null;
    }


    /**
     * 返回可发送的图片对象
     *
     * @param b       图片二进制字节
     * @param contact
     * @return
     */
    public static Image getImage(byte[] b, Contact contact) {
        ExternalResource externalResource = null;
        try {
            long s = System.currentTimeMillis();
            //创建外部资源
            externalResource = ExternalResource.create(b);
            Image image1 = contact.uploadImage(externalResource);
            long e = System.currentTimeMillis();
            logger.debug(" :[系统日志]"+image1.getImageId()+" (ID)<--获取格式化的图片(流)用时: "+((e-s)/1000)+" s");
            return image1;
        } catch (Exception e) {
            logger.error("获取图片时发生了异常", e);
        } finally {
            try {
                if (externalResource != null) {
                    externalResource.close();
                }
            } catch (Exception e) {
                logger.error("关闭图片输入流时发生了异常", e);
            }
        }
        return null;
    }

    /**
     * 返回图片的URL
     *
     * @param image
     * @return
     */
    public static String getURL(Image image) {
        return Image.queryUrl(image);
    }

    /**
     * 获取图片的InputStream流
     *
     * @param image
     * @return
     */
    public static InputStream getImageInputStream(Image image) throws IOException {
//        return HttpDownload.getDownloadInputStream(getURL(image));
        return HttpClientUtils.doGet(getURL(image)).getInputStream();
    }

    /**
     * 获取图片的二进制数组
     *
     * @param image
     * @return
     */
    public static byte[] getImageByBytes(Image image) throws IOException {
//        InputStream downloadInputStream = HttpDownload.getDownloadInputStream(getURL(image));
        InputStream downloadInputStream = getImageInputStream(image);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int code = 0;
        byte[] b = new byte[2 * 1024];
        while ((code = downloadInputStream.read(b)) != -1) {
            byteArrayOutputStream.write(b, 0, code);
        }

        return byteArrayOutputStream.toByteArray();
    }
}
