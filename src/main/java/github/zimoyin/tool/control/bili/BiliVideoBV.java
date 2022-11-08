package github.zimoyin.tool.control.bili;

import github.zimoyin.core.video.info.VideoInfo;
import github.zimoyin.core.video.info.pojo.info.Data;
import github.zimoyin.core.video.info.pojo.info.WEBVideoINFOJsonRootBean;
import github.zimoyin.core.video.info.pojo.info.data.Owner;
import github.zimoyin.core.video.info.pojo.info.data.Stat;
import github.zimoyin.core.video.info.pojo.online.OnlinePopulationRootBean;
import github.zimoyin.core.video.url.VideoURLPreviewFormatP1080;
import github.zimoyin.mtool.annotation.Controller;
import github.zimoyin.mtool.annotation.EventType;
import github.zimoyin.mtool.annotation.ThreadSpace;
import github.zimoyin.mtool.uilt.message.ImageUtils;
import github.zimoyin.mtool.uilt.message.MessageData;
import github.zimoyin.mtool.uilt.net.httpclient.ShortURL;
import github.zimoyin.tool.uilts.BV;
import github.zimoyin.tool.uilts.StringAlign;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

@Controller
@Slf4j
public class BiliVideoBV {
    @ThreadSpace
//    @EventType
    public void videoInfo(MessageEvent event) throws IOException, NoSuchAlgorithmException, KeyStoreException, URISyntaxException, KeyManagementException {
        //判断是否是bv号
        String bvidStr = MessageData.getTextMessage(event).trim();
        boolean bv = BV.isBV(bvidStr);
        if (!bv) return;
        //获取视频信息
        VideoInfo videoInfo = new VideoInfo();
        WEBVideoINFOJsonRootBean info = videoInfo.getInfo(bvidStr);
        log.debug("监听到BV号：{}，获取视频信息代码[{}]",bvidStr,info.getCode());
        //如果获取不到视频就返回
        if (info.getCode() != 0){
            log.warn("获取{}信息失败，code={},message={}",bvidStr,info.getCode(),info.getMessage());
            return;
        }
        //视频具体信息
        Data data = info.getData();
        URL pic = data.getPic();//封面
        Image image = ImageUtils.getImage(String.valueOf(pic), event.getSubject());
        long cid = data.getCid();//cid
        String bvid = data.getBvid();//bv id

        long ctime = data.getCtime();//用户投稿数据
        long pubdate = data.getPubdate();//发布时间
        String desc = data.getDesc();//简介
        String title = data.getTitle();//标题
        int videos = data.getVideos();//分p数
        int copyright = data.getCopyright();//1:原创 2:转载
        OnlinePopulationRootBean online = data.getOnline();//当前在线观看人数

        Stat stat = data.getStat();
        int like = stat.getLike();//点赞数
        long coin = stat.getCoin();//投币数
        int share = stat.getShare();//分享数
        int favorite = stat.getFavorite();//收藏数
        int danmaku = stat.getDanmaku();//视频弹幕数
        int dislike = stat.getDislike();//点踩数
        int view = stat.getView();//视频播放数

        //作者
        Owner owner = data.getOwner();
        String name = owner.getName();//作者mingc
        long mid = owner.getMid();
        //获取视频的下载链接
        ArrayList<URL> urLs = new VideoURLPreviewFormatP1080().getURLs(bvidStr);
        String shortURL = new ShortURL().getShortURL(String.valueOf(urLs.get(0)));
        //对齐
        StringAlign left = new StringAlign(10,StringAlign.Alignment.LEFT);
        StringAlign right = new StringAlign(15,StringAlign.Alignment.LEFT);
        //格式化日期
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String pubdateS = formatter.format(pubdate*1000);
        String ctimeS = formatter.format(ctime*1000);

        MessageChainBuilder builder = new MessageChainBuilder();
        builder.append("【标题·"+(copyright==1?"原创":"转载")+"】 ").append(title).append("\r\n");
        builder.append("【UP】 ").append(name).append("(").append(String.valueOf(mid)).append(")").append("\r\n");
        builder.append(image).append("\r\n");
        builder.append("【简介】 \r\n").append(desc).append("\r\n");
        builder.append("【投稿时间】 ").append(ctimeS).append("\r\n");
        builder.append("【发布时间】 ").append(pubdateS).append("\r\n");


        builder.append("【点赞】").append(left.format(like)).append("");
        builder.append("【分享】").append(right.format(share)).append("\r\n");

        builder.append("【播放】").append(left.format(view)).append("");
        builder.append("【投币】").append(right.format(view)).append("\r\n");

        builder.append("【在线】").append(left.format(online.getData().getTotal())).append("");
        builder.append("【收藏】").append(right.format(favorite)).append("\r\n");

        builder.append("【分p】1/").append(left.format(videos)).append("");
//        builder.append("【下载】").append(right.format(shortURL)).append("\r\n");
        builder.append("【下载】").append(right.format("维修中...")).append("\r\n");
        MessageChain build = builder.build();
        event.getSubject().sendMessage(build);

    }


    @EventType
    public void videoInfoaa(MessageEvent event) throws Exception {
//        ArrayList<Image> images = MessageData.getImages(event.getMessage());
//        for (Image image : images) {
//            System.out.println(image);
//        }
    }
}
