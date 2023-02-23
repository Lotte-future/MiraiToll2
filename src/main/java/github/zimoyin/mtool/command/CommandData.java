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
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

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
        try{
            this.event = event;
            init(event.getMessage());
            initGroup();
            initSender();
            initFriend();
            contact = event.getSubject();
        }catch (Exception e){
            log.error("无法初始一个 CommandData",e);
        }
    }

    private void init(MessageChain chain) {
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
            this.friend = (Friend) event.getSubject();
            this.windowName = this.friend.getNick();
            this.windowID = this.friend.getId();
        }
    }


    public void initParams(String... command) {
        String[] strings = null;
        if (command == null || command.length == 0) strings = CommandParsing.commandParsing(textMessage);
        else strings = command;
        if (strings == null) log.error("命令主语以及参数皆为 null");
        assert strings != null;
        this.prefix = CommandConfig.getInstance().getCommandConfigInfo().getCommandPrefix();
        this.header = strings[0];
        this.params = new String[strings.length - 1];
        System.arraycopy(strings, 1, params, 0, strings.length - 1);
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

    public MessageReceipt<Contact> sendMessage(String message) {
        return contact.sendMessage(message);
    }

    public MessageReceipt<Contact> sendMessage(String message, Object... params) {
        return contact.sendMessage(String.format(message, params));
    }

    public MessageReceipt<Contact> sendMessage(MessageChain chain) {
        return contact.sendMessage(chain);
    }

    public MessageReceipt<Contact> sendMessage(Number number) {
        return contact.sendMessage(String.valueOf(number));
    }

    public MessageReceipt<Contact> sendMessage(Message message) {
        return contact.sendMessage(message);
    }

    /**
     * 允许通过 函数式接口来构建一个信息并发送
     */
    public MessageReceipt<Contact> sendMessage(Consumer<MessageChainBuilder> consumer) {
        MessageChainBuilder messages = new MessageChainBuilder();
        consumer.accept(messages);
        return sendMessage(messages.build());
    }

    public MessageReceipt<Contact> sendQuoteMessage(MessageChain msg) {
        MessageChain chain = new MessageChainBuilder() // 引用收到的消息并回复 "Hi!", 也可以添加图片等更多元素.
                .append(new QuoteReply(event.getMessage()))
                .append(msg)
                .build();
        return sendMessage(chain);
    }

    public MessageReceipt<Contact> sendQuoteMessage(String msg) {
        MessageChain chain = new MessageChainBuilder() // 引用收到的消息并回复 "Hi!", 也可以添加图片等更多元素.
                .append(new QuoteReply(event.getMessage()))
                .append(msg)
                .build();
        return sendMessage(chain);
    }

    public MessageReceipt<Contact> sendForwardMessage(Consumer<ForwardMessageData> consumer) {
        ForwardMessageData messageData = new ForwardMessageData(this);
        consumer.accept(messageData);
        return sendMessage(messageData.build());
    }

    public String getParam() {
        StringBuilder builder = new StringBuilder();
        for (String param : params) {
            builder.append(param).append(" ");
        }
        return builder.toString();
    }
}
