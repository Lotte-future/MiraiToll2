package github.zimoyin.application.control;

import github.zimoyin.mtool.annotation.Controller;
import github.zimoyin.mtool.annotation.EventType;
import github.zimoyin.mtool.uilt.message.MessageData;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.SimpleServiceMessage;

@Slf4j
@Controller
public class AppControl {
    @EventType
    public void onEvent(MessageEvent event){
        SimpleServiceMessage message = MessageData.getSimpleServiceMessage(event);
        if (message == null) return;
        log.warn("监听到一条服务信息(主要字段 id，content): "+message);
    }
}
