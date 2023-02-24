package github.zimoyin.application.control;

import github.zimoyin.mtool.annotation.Controller;
import github.zimoyin.mtool.annotation.EventType;
import github.zimoyin.mtool.event.HistoricalMessageEvent;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.roaming.RoamingMessages;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.function.Consumer;

@Controller
public class TestControl {
    @EventType(HistoricalMessageEvent.class)
    public void onHistoricalMessage(HistoricalMessageEvent event){
        System.out.println(event.getAuthor());
    }
}
