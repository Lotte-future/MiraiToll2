package github.zimoyin.tool.command.bili;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.KumoFont;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.nlp.tokenizers.ChineseWordTokenizer;
import com.kennycason.kumo.palette.LinearGradientColorPalette;
import github.zimoyin.core.barrage.barrage.video.HistoryBarrage;
import github.zimoyin.core.barrage.data.Barrage;
import github.zimoyin.core.cookie.GlobalCookie;
import github.zimoyin.core.exception.CookieNotFoundException;
import github.zimoyin.core.utils.DateFormat;
import github.zimoyin.core.utils.IDConvert;
import github.zimoyin.mtool.annotation.Command;
import github.zimoyin.mtool.annotation.CommandClass;
import github.zimoyin.mtool.command.CommandData;
import github.zimoyin.mtool.config.global.CommandConfig;
import github.zimoyin.mtool.uilt.message.ImageUtils;
import github.zimoyin.tool.uilts.ThreadTool;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

@CommandClass
public class CommandWord {
    private Logger logger = LoggerFactory.getLogger(CommandWord.class);

    @Command(value = "词云",description = "提供视频弹幕生成词云（参数：【bv号】）")
    public void word(MessageEvent event) {
        CommandData commandData = new CommandData(event);
        String[] params = commandData.getParams();
        if (commandData.isEmptyParams()) {
            event.getSubject().sendMessage("命令格式错误：" + CommandConfig.getInstance().getCommandConfigInfo().getCommandPrefix()
                    + "词云 bv号");
            return;
        }

        if (params[0].trim().substring(0, 2).equalsIgnoreCase("av")) {
            params[0] = IDConvert.AvToBv(params[0].toUpperCase().trim());
            logger.debug("av转为bv：" + params[0]);
        }

        event.getSubject().sendMessage("35s 后进行输出");
        ThreadTool.getINSTANCE().
                getExecutorService().
                execute(() -> {
                    Thread.currentThread().setName("WordClod-"+params[0]);
                    try {
                        HistoryBarrage barrages0 = new HistoryBarrage(GlobalCookie.getInstance());
                        HashMap<String, Integer> time = new HashMap<String, Integer>();
                        //获取本月弹幕
                        ArrayList<String> historyDates = barrages0.getHistoryDates(params[0], DateFormat.format());
                        for (String historyDate : historyDates) {
                            logger.debug(params[0] + ": " + historyDate);
                            ArrayList<Barrage> barrages = barrages0.getBarrage(params[0], historyDate);
                            for (Barrage barrage : barrages) {
                                String text = barrage.getText();
                                Integer integer = time.get(text);
                                if (integer == null) integer = 0;
                                time.put(text, integer + 1);
                            }
                            Thread.sleep(1500);
                        }

                        WordCloud wordCountPic = createWordCountPic(time);
                        BufferedImage bufferedImage = wordCountPic.getBufferedImage();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
                        byte[] bytes = byteArrayOutputStream.toByteArray();
                        Image image = ImageUtils.getImage(bytes, event.getSubject());
                        event.getSubject().sendMessage(new MessageChainBuilder().append(params[0]).append("\r\n").append(image).build());
                        return;
                    } catch (CookieNotFoundException e) {
                        logger.error("无法获取b站的登录Cookie", e);
                    } catch (Exception e) {
                        logger.error("词云生成失败", e);
                    }finally {
                        Thread.currentThread().setName("thread"+System.currentTimeMillis());
                    }

                    event.getSubject().sendMessage("生成词云失败: " + params[0]);
                });
    }

    private static WordCloud createWordCountPic(HashMap<String, Integer> time) {
        // 建立词频分析器，设置词频，以及词语最短长度，此处的参数配置视情况而定即可
        FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        frequencyAnalyzer.setWordFrequenciesToReturn(600);
        frequencyAnalyzer.setMinWordLength(2);
        frequencyAnalyzer.setWordTokenizer(new ChineseWordTokenizer());
        // 可以直接从文件中读取
        //List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(getInputStream("D:\\citydo-one\\技术\\Java_Note-master\\python\\tp\\Trump.txt"));
        List<WordFrequency> wordFrequencies = new ArrayList<>();
        // 用词语来随机生成词云
//        String strValue = "菠萝=20, 草莓=20, 苹果=100, 西红柿=30, 榴莲=15,  西瓜=0, 猕猴桃=1, 火龙果=4";

        //以逗号为分割号
//        String[] split = strValue.split(", ");
//        String word = "";
//        int count = 0;

//        for (int i = 0; i < split.length; i++) {
//            String[] wordInfo = split[i].split("=");
//            word = wordInfo[0];
//            count = Integer.valueOf(wordInfo[1]);
//            wordFrequencies.add(new WordFrequency(word, count));
//        }

        time.forEach(new BiConsumer<String, Integer>() {
            @Override
            public void accept(String word, Integer count) {
                wordFrequencies.add(new WordFrequency(word, count));
            }
        });

        //加入分词并随机生成权重，每次生成得图片都不一样
//        test.stream().forEach(e-> wordFrequencies.add(new WordFrequency(e,new Random().nextInt(test.size()))));
        //此处不设置会出现中文乱码
        java.awt.Font font = new java.awt.Font("STSong-Light", 2, 18);
        //设置图片分辨率
        Dimension dimension = new Dimension(500, 500);
        //此处的设置采用内置常量即可，生成词云对象
        WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
        //设置边界及字体
        wordCloud.setPadding(2);
        //因为我这边是生成一个圆形,这边设置圆的半径
        wordCloud.setBackground(new CircleBackground(255));
        wordCloud.setFontScalar(new SqrtFontScalar(12, 42));
        //设置词云显示的三种颜色，越靠前设置表示词频越高的词语的颜色
        wordCloud.setColorPalette(new LinearGradientColorPalette(Color.RED, Color.BLUE, Color.GREEN, 30, 30));
        wordCloud.setKumoFont(new KumoFont(font));
        wordCloud.setBackgroundColor(new Color(255, 255, 255));
        //因为我这边是生成一个圆形,这边设置圆的半径
        wordCloud.setBackground(new CircleBackground(255));
        wordCloud.build(wordFrequencies);
        //生成词云图路径
//        wordCloud.writeToFile("./cache/词云.png");
        return wordCloud;
    }

}
