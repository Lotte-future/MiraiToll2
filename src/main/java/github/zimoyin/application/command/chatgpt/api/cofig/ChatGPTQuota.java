package github.zimoyin.application.command.chatgpt.api.cofig;

import github.zimoyin.mtool.config.application.ApplicationConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Getter
@Setter
@Slf4j
public final class ChatGPTQuota extends ApplicationConfig {
    //总额度，默认为免费额度 50
    private volatile double TotalAmount = 50;
    //调用一次api的价格
    public static final double Price = 0.002385701754386;
    //调用次数
    private volatile int Frequency = 0;
    //消费的额度
    private volatile double ConsumptionAmount = Frequency * Price;
    //剩余调用次数
    private volatile int RemainingCalls = (int) ((TotalAmount-ConsumptionAmount) / Price);

    public ChatGPTQuota() {
        super(true);
        initialize();
        try {
            save();
        } catch (IOException e) {
            log.warn("无法保存配置文件：{}",this.getClass().getCanonicalName());
        }
    }

    public ChatGPTQuota(boolean isInit) {
        super(isInit);
        if (isInit)initialize();
    }

    private void initialize(){
        setFieldValue(this);
    }

    @Override
    public void save() throws IOException {
        update();
        super.save();
    }
    private void update(){
        this.setConsumptionAmount(Frequency * Price);
        this.setRemainingCalls( (int) ((TotalAmount-ConsumptionAmount) / Price));
    }

    public void add(){
        setFrequency(Frequency+1);
    }

    public void setFrequency(int frequency) {
        Frequency = frequency;
        update();
        this.put("Frequency", frequency);
    }

    public void setTotalAmount(double totalAmount) {
        TotalAmount = totalAmount;
        update();
        this.put("TotalAmount", totalAmount);
    }

    public void setConsumptionAmount(double consumptionAmount) {
        ConsumptionAmount = consumptionAmount;
        this.put("ConsumptionAmount", consumptionAmount);
    }

    public void setRemainingCalls(int remainingCalls) {
        RemainingCalls = remainingCalls;
        this.put("RemainingCalls", remainingCalls);
    }
}
