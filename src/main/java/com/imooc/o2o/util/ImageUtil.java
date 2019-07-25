package com.imooc.o2o.util;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ImageUtil {
    private static String basePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
    private static final SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final Random r = new Random();
    private static Logger logger = LoggerFactory.getLogger(ImageUtil.class);

    /**
     * 将CommonsMultipartFile转换成File类
     * @param cFile
     * @return
     */
    public static File transferCommonsMultipartFileToFile(CommonsMultipartFile cFile){
        File newFile = new File(cFile.getOriginalFilename());
        try {
            cFile.transferTo(newFile);
        } catch (IllegalStateException e) {
            logger.error(e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            logger.error(e.toString());
            e.printStackTrace();
        }
        return newFile;
    }

    /**
     * 处理缩略图，并返回新生成图片的相对值路径
     * @param thumbnailInputStream 用户传送的文件流
     * @param fileName
     * @param targetAddr 新文件存储路径
     * @return
     */
    public static String generateThumbnail(InputStream thumbnailInputStream,String fileName,String targetAddr){
        //获取文件的随机名
        String realFileName = getRandomFileName();
        //获取用户上传的图片的扩展名 例如：jpg png
        String extension = getFileExtension(fileName);
        //创建目录
        makeDirPath(targetAddr);
        //获取相对路径
        String relativeAddr = targetAddr + realFileName + extension;
        logger.debug("current relativeAddr is:" + relativeAddr);
        File dest = new File(PathUtil.getImgBasePath() + relativeAddr);
        logger.debug("current complete addr is:" + PathUtil.getImgBasePath() + relativeAddr);
        try{
            Thumbnails.of(thumbnailInputStream).size(200,200)
                    .watermark(Positions.BOTTOM_RIGHT,ImageIO.read(new File(basePath + "/watermark.jpg")),0.25f)
                    .outputQuality(0.8f).toFile(dest);
        }catch (IOException e){
            logger.error(e.toString());
            e.printStackTrace();
        }
        return relativeAddr;
    }

    /**
     * 生成随机文件名，当前年月日小时分钟秒钟+五位随机数
     * @return
     */
    public static String getRandomFileName(){
        //获取随机的五位数
        // nextInt(89999), 在0到89999之间取值
        //10000 < rannum < 99999
        int rannum = r.nextInt(89999) + 10000;
        String nowTimeStr = sDateFormat.format(new Date());
        //字符串 + 整形 自动转换成字符串
        return nowTimeStr + rannum;
    }

    /**
     * 获取输入文件流的扩展名
     * @param fileName
     * @return
     */
    private static String getFileExtension(String fileName){
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * 创建目标路径所涉及到的目录，即/home/work/xiangze/xxx.jpg,
     * 那么home work xiangze 这三个文件夹都得自动创建
     * @param targetAddr
     */
    private static void makeDirPath(String targetAddr){
        String realFileParentPath = PathUtil.getImgBasePath() + targetAddr;
        File dirPath = new File(realFileParentPath);
        if (!dirPath.exists()){
            dirPath.mkdirs();
        }
    }

    public static void main(String[] args) throws IOException {
        Thumbnails.of(new File("D:/baidu/work/image/xiaohuangren.jpg"))
                .size(200,200).watermark(Positions.BOTTOM_RIGHT,
                ImageIO.read(new File(basePath + "/watermark.jpg")),0.25f).outputQuality(0.8f)
                .toFile("D:/baidu/work/image/xiaohuangrennew.jpg");
    }
}
