package github.zimoyin.application.control;

import github.zimoyin.mtool.annotation.Controller;
import github.zimoyin.mtool.annotation.EventType;
import github.zimoyin.mtool.event.FileMessageEvent;
import github.zimoyin.mtool.event.HistoricalMessageEvent;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.roaming.RoamingMessages;
import net.mamoe.mirai.event.events.MessagePostSendEvent;
import net.mamoe.mirai.event.events.MessagePreSendEvent;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.util.function.Consumer;

@Controller
public class TestControl {
    @EventType(HistoricalMessageEvent.class)
    public void onHistoricalMessage(HistoricalMessageEvent event){
        System.out.println(event.getAuthor());
    }

    @EventType(FileMessageEvent.class)
    public void onHistoricalMessage(FileMessageEvent event){
        System.out.println(event.getSenderName());
    }

}
