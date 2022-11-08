package github.zimoyin.solver.gui;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

public class CommunicationChannelOfURL implements CommunicationChannel<String> {
    //一个由链表结构组成的无界阻塞队列
    private volatile BlockingQueue<String> queue = new LinkedTransferQueue<>();
    private volatile static CommunicationChannelOfURL INSTANCE;

    private CommunicationChannelOfURL() {
    }

    public static CommunicationChannelOfURL getInstance() {
        if (INSTANCE == null) synchronized (CommunicationChannelOfURL.class) {
            if (INSTANCE == null) INSTANCE = new CommunicationChannelOfURL();
        }
        return INSTANCE;
    }

    @Override
    public String getValue() {
        try {
            return queue.poll(3, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean setValue(String value) {
        return queue.add(value);
    }
}
