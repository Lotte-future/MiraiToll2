package github.zimoyin.application.command.chatgpt.api;

import com.alibaba.fastjson2.JSONObject;
import github.zimoyin.application.command.chatgpt.api.cofig.ChatGPTQuota;
import github.zimoyin.application.command.chatgpt.api.server.ChatAPI;
import github.zimoyin.application.server.thesaurus.ThesaurusCenter;
import github.zimoyin.mtool.annotation.Command;
import github.zimoyin.mtool.annotation.CommandClass;
import github.zimoyin.mtool.annotation.Filter;
import github.zimoyin.mtool.command.CommandData;
import github.zimoyin.mtool.command.CommandObject;
import github.zimoyin.mtool.command.filter.impl.Level;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.MessageReceipt;

import java.io.IOException;

@Slf4j
@CommandClass
public class CommandChatAPI {

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
    public String commandChat(CommandData data, CommandObject commandObject) {

        MessageReceipt<Contact> receipt = data.sendMessage("少女思考中...");
        if (System.currentTimeMillis() - start > 24 * 60 * 60 * 1000) {
            start=System.currentTimeMillis();
            ChatAPI.getInstance().getCachesCount().clear();
            log.info("ChatGPT： 正在重置缓存数值列表");
        }
        String param = data.getParam();
        //随机返回一个数据库里面都词条
        ThesaurusCenter instance = ThesaurusCenter.getInstance();
        String sv = instance.getEntries(param.trim()).stream().findFirst().orElse(null);
        if (sv != null) return sv;
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
            instance.addEntries(param.trim(),text.trim());
            return text;
        } catch (IOException e) {
            log.error("与Chat API 交流时产生异常", e);
            return "抱歉无法访问到ChatGPT API";
        } finally {
            receipt.recall();
        }
    }

    @Command("gpt2")
    public String commandChat2(CommandData data, CommandObject commandObject) {
        if (true)return "该API无法解析 ChatGPT 返回数据";
        MessageReceipt<Contact> receipt = data.sendMessage("少女思考中...");
        if (System.currentTimeMillis() - start > 24 * 60 * 60 * 1000) {
            start=System.currentTimeMillis();
            ChatAPI.getInstance().getCachesCount().clear();
            log.info("ChatGPT： 正在重置缓存数值列表");
        }
        String param = data.getParam();
        //随机返回一个数据库里面都词条
        ThesaurusCenter instance = ThesaurusCenter.getInstance();
        String sv = instance.getEntries(param.trim()).stream().findFirst().orElse(null);
        if (sv != null) return sv;
        //参数校验
        if (param.isEmpty()) {
            return "参数不合法，请保持参数的长度在 1-300 之间";
        }
        if (!ChatAPI.getInstance().preChat(data.getWindowID() + "-" + data.getSenderID())) {
            return "访问失败,请重置会话缓存后再试";
        }
        String text = "访问失败,无法连接至 ChatGPT 服务器";
        try {
            String chat = ChatAPI.getInstance().chat2(param, ChatAPI.Role.user);
            text = JSONObject.parseObject(chat).getJSONArray("choices").getJSONObject(0).get("text").toString().trim();
            instance.addEntries(param.trim(),text.trim());
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
