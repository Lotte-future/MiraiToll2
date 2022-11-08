package github.zimoyin.mtool.command;

import github.zimoyin.mtool.config.global.CommandConfig;
import github.zimoyin.mtool.uilt.message.MessageData;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 命令数据封装
 */
@Data
@Slf4j
public class CommandData {
    /**
     * 在特定时间内对目标群发言的频率
     */
    @Deprecated
    private static final HashMap<Long, Integer> sendCount = new HashMap<Long, Integer>();
    /**
     * 统计3s内的发送频率
     */
    @Deprecated
    private static final long sendTime = 3000L;
    /**
     * 统计发言时间
     */
    @Deprecated
    private static long Time = 0L;
    /**
     * 单位时间内限制发言次数
     */
    @Deprecated
    private static int count = 1;
    /**
     * 是否被AT
     */
    private boolean isAT;
    /**
     * 命令前缀
     */
    private String prefix;
    /**
     * 命令主语
     */
    private String header;
    /**
     * 命令参数（文本）
     */
    private String[] params;
    /**
     * 命令参数（图片）
     */
    private ArrayList<Image> images;


    /**
     * 命令参数（图片）
     */
    private Image image;

    /**
     * 命令原始文本
     */
    private String textMessage;

    /**
     * 命令信息链
     */

    private MessageChain chain;

    /**
     * 是否是命令语句
     */
    private boolean isCommand;

    /**
     * 信息事件源
     */
    private MessageEvent event;

    /**
     * 信息发送者ID
     */
    private long senderID;
    /**
     * 信息发送者名称
     */
    private String senderName;

    /**
     * 群对象
     */
    private Group group;

    /**
     * 朋友对象
     */
    private Friend friend;

    /**
     * 会话窗口ID
     */
    private long windowID;
    /**
     * 会话窗口名称
     */
    private String windowName;

    /**
     * 联系人
     */
    private Contact contact;


    public CommandData(MessageChain chain) {
        init(chain);
    }

    public CommandData(MessageEvent event) {
        this.event = event;
        init(event.getMessage());
        initGroup();
        initSender();
        contact = event.getSubject();
    }

    public void init(MessageChain chain) {
        this.chain = chain;
        this.textMessage = MessageData.getTextMessage(chain);
        if (!CommandParsing.isCommandSubjectParsing(textMessage)) {
            this.isCommand = false;
            return;
        }
        this.isCommand = true;
        this.image = MessageData.getImage(chain);
        this.images = MessageData.getImages(chain);
        initParams();
    }

    private void initSender() {
        this.senderID = event.getSource().getFromId();
        this.senderName = event.getSender().getNick();
    }


    private void initGroup() {
        if (event.getSubject() instanceof Group) {
            Group subject = (Group) event.getSubject();
            this.group = subject;
            this.windowName = group.getName();
            this.windowID = event.getSource().getTargetId();
        }
    }

    private void initFriend() {
        if (event.getSubject() instanceof Friend) {
            Friend subject = (Friend) event.getSubject();
            this.friend = subject;

            this.windowName = this.friend.getNick();
            this.windowID = this.friend.getId();
        }
    }


    private void initParams() {
        String[] strings = CommandParsing.commandParsing(textMessage);
        assert strings != null;
        this.prefix = CommandConfig.getInstance().getCommandConfigInfo().getCommandPrefix();
        this.header = strings[0];
        this.params = new String[strings.length - 1];
        for (int i = 1; i < strings.length; i++) {
            params[i - 1] = strings[i];
        }
    }

    public boolean isNotEmptyParams() {
        return params != null && params.length > 0;
    }

    public boolean isEmptyParams() {
        return !isNotEmptyParams();
    }

    public Bot getBot() {
        return event.getBot();
    }

    public long getBotId() {
        return getBot().getId();
    }

    public void sendMessage(String message) {
//        if (!setSendCount(contact.getId())) {
//            log.warn("对 {} 的发言频率高于阈值({}c/{}s)，以禁止发言 {}",contact.getId(),count,sendTime,message);
//            return;
//        }
        contact.sendMessage(message);
    }

    public void sendMessage(String message, Object... params) {
//        if (!setSendCount(contact.getId())) {
//            log.warn("对 {} 的发言频率高于阈值({}c/{}s)，以禁止发言 {}",contact.getId(),count,sendTime,message);
//            return;
//        }
        contact.sendMessage(String.format(message, params));
    }

    public void sendMessage(MessageChain chain) {
//        if (!setSendCount(contact.getId())) {
//            log.warn("对 {} 的发言频率高于阈值({}c/{}s)，以禁止发言 {}",contact.getId(),count,sendTime,chain);
//            return;
//        }
        contact.sendMessage(chain);
    }

    public void sendMessage(Number number) {
//        if (!setSendCount(contact.getId())) {
//            log.warn("对 {} 的发言频率高于阈值({}c/{}s)，以禁止发言 {}",contact.getId(),count,sendTime,number);
//            return;
//        }
        contact.sendMessage(String.valueOf(number));
    }

    public void sendMessage(Message message) {
//        if (!setSendCount(contact.getId())) {
//            log.warn("对 {} 的发言频率高于阈值({}c/{}s)，以禁止发言 {}",contact.getId(),count,sendTime,message);
//            return;
//        }
        contact.sendMessage(message);
    }

    public String getParam() {
        StringBuilder builder = new StringBuilder();
        for (String param : params) {
            builder.append(param).append(" ");
        }
        return builder.toString();
    }

    @Deprecated
    private boolean setSendCount(long id) {
        long thisTime = System.currentTimeMillis();
        //刷新发言时间
        if (thisTime >= Time + sendTime) {
            sendCount.clear();
            Time = System.currentTimeMillis();
        }
        //更新发言频率
        sendCount.put(id, sendCount.get(id) == null ? 0 : sendCount.get(id));
        //统计发言频率是否大于阈值
        return sendCount.get(id) <= count;
    }

}
