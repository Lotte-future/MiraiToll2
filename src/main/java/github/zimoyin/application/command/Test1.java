package github.zimoyin.application.command;

import github.zimoyin.mtool.annotation.Command;
import github.zimoyin.mtool.annotation.CommandClass;
import github.zimoyin.mtool.annotation.Filter;
import github.zimoyin.mtool.annotation.ThreadSpace;
import github.zimoyin.mtool.command.CommandData;
import github.zimoyin.mtool.command.filter.Level;
import github.zimoyin.mtool.dao.H2Connection;
import github.zimoyin.application.command.filter.TestFilter;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MusicKind;
import net.mamoe.mirai.message.data.MusicShare;

@CommandClass
@ThreadSpace
@Slf4j
public class Test1 {
    //测试 拦截器
    //测试 音乐卡片
    @ThreadSpace
    @Command("test")
    @Filter(value = Level.UNLevel,filterCls={TestFilter.class})
    public void a(MessageEvent event, CommandData data, H2Connection connection){
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
    }
}
