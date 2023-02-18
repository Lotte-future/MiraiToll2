package github.zimoyin.application.command.chatgpt.api2.api;

import com.alibaba.fastjson2.JSON;
import github.zimoyin.application.dao.chat.Thesaurus;
import github.zimoyin.application.dao.chat.ThesaurusDao;
import github.zimoyin.application.uilts.MybatisUtils;
import github.zimoyin.mtool.annotation.Command;
import github.zimoyin.mtool.annotation.CommandClass;
import github.zimoyin.mtool.command.CommandData;
import github.zimoyin.mtool.uilt.net.httpclient.HttpClientResult;
import github.zimoyin.mtool.uilt.net.httpclient.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

import java.io.IOException;

/**
 * @link <a href="https://api.aa1.cn/doc/chatgpts.html">夏柔 ChatGPT官方v1版</a>
 */
@CommandClass
@Slf4j
public class XiaRou {
    private SqlSession sqlSession = null;
    ThesaurusDao mapper = null;

    public XiaRou() {
        //获取SqlSession 对象
        sqlSession = MybatisUtils.getSqlSession();
        mapper = sqlSession.getMapper(ThesaurusDao.class);
    }

    /**
     * apitype=sql，将优先索引数据库
     */
    private static final String URL = "https://v1.apigpt.cn/?q=%s&apitype=sql";

    @Command(value = "xr", alias = {"q", "Q"})
    public String command(CommandData data) throws IOException {
        HttpClientResult result = HttpClientUtils.doGet(String.format(URL, data.getParam().trim()));
        String content = result.getContent();
        try {
            content = JSON.parseObject(content).getString("ChatGPT_Answer").trim();
        } catch (Exception e) {
            log.warn("无法解析的字符串\n{}\n{}\n{}\n",
                    "=============================================================",
                    content,
                    "==========================================================",
                    e
            );
            return "ChatGPT 服务器无法被访问";
        }
        try {
            mapper.addThesaurus(new Thesaurus(data.getParam().trim(), content.trim(), "ChatGPT"));
        } catch (Exception e) {
            log.warn("记录对话日志失败", e);
        }
        return content;
    }
}
