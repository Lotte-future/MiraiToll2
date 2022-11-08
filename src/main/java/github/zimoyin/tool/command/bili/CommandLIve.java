package github.zimoyin.tool.command.bili;

import github.zimoyin.core.live.video.LiveVideoURL;
import github.zimoyin.core.live.video.data.Quality;
import github.zimoyin.mtool.annotation.Command;
import github.zimoyin.mtool.annotation.CommandClass;
import github.zimoyin.mtool.command.CommandData;
import github.zimoyin.mtool.config.global.CommandConfig;
import github.zimoyin.mtool.uilt.net.httpclient.ShortURL;
import net.mamoe.mirai.event.events.MessageEvent;
import org.apache.http.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;

@CommandClass
public class CommandLIve {
    private Logger logger = LoggerFactory.getLogger(CommandLIve.class);

    @Command(value = "直播",description = "获取b站直播间的真实地址（参数：【房间号】）")
    public void live(MessageEvent event) {
        //直播 房间号
        CommandData commandData = new CommandData(event);
        //解析参数
        String[] params = commandData.getParams();
        if (commandData.isEmptyParams()) {
            event.getSubject().sendMessage("命令格式错误：" + CommandConfig.getInstance().getCommandConfigInfo().getCommandPrefix() + "直播 房间号");
            return;
        }
        //彩蛋
        String trim = params[0].trim();
        switch (trim) {
            case "直播":
                event.getSubject().sendMessage("你是睿智吗？你见过套娃套自己的嘛");
                return;
            case "-1":
                event.getSubject().sendMessage("我...我可是有底线的呢");
                return;
            case "woc":
                event.getSubject().sendMessage("呜呜呜，被骂哩");
                return;
            case "403":
                event.getSubject().sendMessage("我是合法的啦");
                return;
            case "500":
                event.getSubject().sendMessage("服务器娘才没炸呢");
                return;
            case "404":
                event.getSubject().sendMessage("唔~  找不到被404的直播间呢");
                return;
            case "200":
                event.getSubject().sendMessage("我出手你放心");
                return;
        }

        //获取直链接
        String roomID = params[0].trim();
        LiveVideoURL video = new LiveVideoURL();
        try {
            ArrayList<URL> url = video.getURL(Long.parseLong(roomID), Quality.ORIGINAL_PAINTING);
            URL url1 = url.get(0);
            event.getSubject().sendMessage("房间号 " + roomID + " 直链地址：" + new ShortURL().getShortURL(url1.toString()));
        } catch (NumberFormatException e) {
            logger.warn("获取直播真实地址失败,该房间号可能不合法，room id = {}", roomID);
            event.getSubject().sendMessage("403: 房间号：" + roomID + " 不合法");
        } catch (NullPointerException e) {
            logger.warn("获取直播真实地址失败,该房间号可能不存在，room id = {}", roomID);
            event.getSubject().sendMessage("501: 房间号：" + roomID + " 不存在");
        } catch (HttpException e) {
            logger.error("获取直播真实地址失败，room id = {}", roomID, e);
        }

    }
}
