package github.zimoyin.application.command;

import github.zimoyin.mtool.annotation.Command;
import github.zimoyin.mtool.annotation.CommandClass;
import github.zimoyin.mtool.command.CommandData;
import github.zimoyin.mtool.dao.JsonSerializeUtil;
import github.zimoyin.mtool.uilt.net.httpclient.HttpClientUtils;
import github.zimoyin.mtool.uilt.net.httpclient.ShortURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@CommandClass
public class CommandLanZouYun {
    private static final String URL="https://api.vvhan.com/api/lz?url=%s";
    private final Logger logger = LoggerFactory.getLogger(CommandLanZouYun.class);
    @Command("蓝奏云")
    public void url(CommandData command) throws IOException {
        if (command.isEmptyParams()) {
            command.sendMessage("命令格式错误：%s蓝奏云 URL",command.getPrefix());
            return;
        }
        String param = command.getParams()[0].trim();
        String url = String.format(URL,param);
        logger.info("访问URL：{}",url);
        String content = HttpClientUtils.doGet(url).getContent();
        logger.info("响应正文: {} \r\n{}",url,content);
        String result = JsonSerializeUtil.getJsonPath().read(content, "/download/");
        String shortURL = new ShortURL().getShortURL(result);
        command.sendMessage(shortURL);
    }
}
