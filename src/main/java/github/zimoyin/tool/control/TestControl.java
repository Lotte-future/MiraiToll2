package github.zimoyin.tool.control;

import github.zimoyin.mtool.annotation.Controller;
import github.zimoyin.mtool.annotation.EventType;
import github.zimoyin.mtool.event.HistoricalMessageEvent;

@Controller
public class TestControl {
    @EventType(HistoricalMessageEvent.class)
    public void onHistoricalMessage(HistoricalMessageEvent event){
        System.out.println(event.getAuthor());
    }
}
