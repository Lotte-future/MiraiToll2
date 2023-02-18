package github.zimoyin.application.command.chatgpt.api;

import com.alibaba.fastjson2.JSONObject;
import github.zimoyin.application.command.chatgpt.api.cofig.ChatGPTQuota;
import github.zimoyin.application.command.chatgpt.api.server.ChatAPI;
import github.zimoyin.application.dao.chat.Thesaurus;
import github.zimoyin.application.dao.chat.ThesaurusDao;
import github.zimoyin.application.uilts.MybatisUtils;
import github.zimoyin.mtool.annotation.Command;
import github.zimoyin.mtool.annotation.CommandClass;
import github.zimoyin.mtool.annotation.Filter;
import github.zimoyin.mtool.command.CommandData;
import github.zimoyin.mtool.command.filter.impl.Level;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.MessageReceipt;
import org.apache.ibatis.session.SqlSession;

import java.io.IOException;
import java.util.List;

@Slf4j
@CommandClass
public class CommandChatAPI {
    private SqlSession sqlSession = null;
    ThesaurusDao mapper = null;

    public CommandChatAPI() {
        //获取SqlSession 对象
        sqlSession = MybatisUtils.getSqlSession();
        mapper = sqlSession.getMapper(ThesaurusDao.class);
    }

    private long start = System.currentTimeMillis();

    @Command(value = "chat", description = "关于ChatGPT的指令")
    public String commandChatHelp() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("chat 指令").append("\n");
        buffer.append("==================").append("\n");
        buffer.append("gpt 参数：访问GPT").append("\n");
        buffer.append("rgpt : 重置会话缓存").append("\n");
        buffer.append("rest : 重置GPT缓存列表").append("\n");
        buffer.append("ginfo : GPT信息").append("\n");
        buffer.append("==================").append("\n");
        return buffer.toString();
    }

    @Command("gpt")
    public String commandChat(CommandData data) {
        MessageReceipt<Contact> receipt = data.sendMessage("少女思考中...");
        if (System.currentTimeMillis() - start > 24 * 60 * 60 * 1000) {
            ChatAPI.getInstance().getCachesCount().clear();
            log.info("ChatGPT： 正在重置缓存数值列表");
        }
        String param = data.getParam();
        //随机返回一个数据库里面都词条
        try {
            if (param!=null && !param.isEmpty()){
                List<String> values = mapper.getValues(param.trim());
                if (values.size() >0){
                    return values.get((int) (Math.random()*values.size()));
                }
            }
        }catch (Exception e){
            log.warn("无法访问数据库拿到词条");
        }
        //参数校验
        if (param.isEmpty()) {
            return "参数不合法，请保持参数的长度在 1-300 之间";
        }
        if (!ChatAPI.getInstance().preChat(data.getWindowID() + "-" + data.getSenderID())) {
            return "访问失败,请重置会话缓存后再试";
        }
        String text = "访问失败,无法连接至 ChatGPT 服务器";
        try {
            String chat = ChatAPI.getInstance().chat(param, data.getWindowID() + "-" + data.getSenderID());
            text = JSONObject.parseObject(chat).getJSONArray("choices").getJSONObject(0).get("text").toString().trim();
            try {
                mapper.addThesaurus(new Thesaurus(data.getParam().trim(), text.trim(), "ChatGPT"));
            } catch (Exception e) {
                log.warn("记录对话日志失败:\n{}\n\n",text, e);
            }
            return text;
        } catch (IOException e) {
            log.error("与Chat API 交流时产生异常", e);
            return "抱歉无法访问到ChatGPT API";
        } finally {
            receipt.recall();
        }
    }

    @Command("rgpt")
    public String commandChatReset(CommandData data) {
        ChatAPI.getInstance().reset(data.getWindowID() + "-" + data.getSenderID());
        return "重置完毕";
    }

    @Command("rest")
    @Filter(value = Level.Root)
    public String commandChatResetRoot(CommandData data) {
        ChatAPI.getInstance().getCachesCount().clear();
        log.info("ChatGPT： 正在重置缓存数值列表");
        return "重置完毕";
    }


    @Command("ginfo")
    @Filter(value = Level.Root)
    public String commandChatInfo(CommandData data) {
        ChatGPTQuota info = ChatAPI.getInstance().getInfo();
        StringBuffer buffer = new StringBuffer();
        buffer.append("GPT INFO").append("\n");
        buffer.append("==================").append("\n");
        buffer.append("额度: ").append(info.getConsumptionAmount()).append("/").append(info.getTotalAmount()).append("\n");
        buffer.append("调用次数: ").append(info.getFrequency()).append("/").append(info.getRemainingCalls()).append("\n");
        buffer.append("==================").append("\n");
        return buffer.toString();
    }
}
