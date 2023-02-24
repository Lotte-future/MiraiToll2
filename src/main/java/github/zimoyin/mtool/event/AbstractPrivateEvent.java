package github.zimoyin.mtool.event;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.AbstractEvent;
import net.mamoe.mirai.event.EventKt;

/**
 * 自定义事件
 */
public abstract class AbstractPrivateEvent extends AbstractEvent {
    private Bot bot;

    public AbstractPrivateEvent(Bot bot) {
        this.bot = bot;
    }

    public Bot getBot() {
        return bot;
    }

    public void setBot(Bot bot) {
        this.bot = bot;
    }

    public void broadcast(){
        EventKt.broadcast(this);
    }
}
