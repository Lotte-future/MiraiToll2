package github.zimoyin.application.command;

import github.zimoyin.mtool.annotation.Command;
import github.zimoyin.mtool.annotation.CommandClass;
import github.zimoyin.mtool.annotation.Filter;
import github.zimoyin.mtool.annotation.ThreadSpace;
import github.zimoyin.mtool.command.CommandData;
import github.zimoyin.mtool.command.ForwardMessageData;
import github.zimoyin.mtool.command.filter.FilterTable;
import github.zimoyin.mtool.command.filter.impl.Level;
import github.zimoyin.mtool.dao.H2Connection;
import github.zimoyin.application.command.filter.TestFilter;
import github.zimoyin.mtool.uilt.message.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.Function;

@CommandClass
@ThreadSpace
@Slf4j
public class Test1 {
    //测试 拦截器
    //测试 音乐卡片
    @ThreadSpace
    @Command("test")
    @Filter(value = Level.UNLevel, filterCls = {TestFilter.class})
    /**
     * 方法参数可以是无参数也可以是有参数，参数数量不是固定的，但是参数类型一定是要符合要求：CommandObj.execute() 下 invoke 传入的参数
     * 方法可以没有返回值。如果方法有返回值的话那么这个返回值将会被发送到当前的聊天会话里面
     */
    public void a(MessageEvent event, CommandData data, H2Connection connection) {
        data.getChain();
        //添加权限
        FilterTable.getInstance().setRoot(0, 0);
        FilterTable.getInstance().setRoot(1, 2556608754L);
        //移除权限
        FilterTable.getInstance().removeRoot(0, 0);
        //查询所有的Root权限表
        System.out.println(FilterTable.getInstance().getRoot());

        MusicShare ファッション = new MusicShare(
                MusicKind.NeteaseCloudMusic,
                "ファッション",
                "rinahamu/Yunomi",
                "http://music.163.com/song/1338728297/?userid=324076307",
                "http://p2.music.126.net/y19E5SadGUmSR8SZxkrNtw==/109951163785855539.jpg",
                "http://music.163.com/song/media/outer/url?id=1338728297&userid=324076307"
        );
        log.warn("aga");
        event.getSubject().sendMessage(ファッション);
        if (event instanceof FriendMessageEvent) {
            ((FriendMessageEvent) event).getFriend().getRoamingMessages();//获取历史记录
        }
    }

    @Command("t")
    public void text2(CommandData data) throws IOException {

    }
}
