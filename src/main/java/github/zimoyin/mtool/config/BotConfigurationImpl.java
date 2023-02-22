package github.zimoyin.mtool.config;


import github.zimoyin.mtool.dao.MiraiLog4j;
import github.zimoyin.mtool.exception.BotConfigRuntimeException;
import github.zimoyin.mtool.uilt.OSinfo;
import github.zimoyin.solver.ImageLoginSolver;
import github.zimoyin.solver.ImageLoginSolverKt;
import lombok.Data;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.MiraiLoggerPlatformBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.awt.OSInfo;

import java.io.File;
import java.io.IOException;

/**
 * 配置机器人的登录已经状态信息
 */
@Data
public class BotConfigurationImpl extends BotConfiguration {
    //缓存保存地方
    private String cache = "./cache/%s/";
    //日志
    private Logger logger = LoggerFactory.getLogger(BotConfigurationImpl.class);
    //心跳策略
    private HeartbeatStrategy register = HeartbeatStrategy.REGISTER;
    //登录协议
    private MiraiProtocol version = MiraiProtocol.ANDROID_PAD;
    //是否关闭log日志
    private boolean isLog = false;
    //是否关闭net日志
    private boolean isNet = true;
    private String devicePath;

    public BotConfigurationImpl(final long id) {
        this.cache = String.format(cache, id);
        devicePath = cache + "device.json";
        try {
            init();
        } catch (IOException e) {
            throw new BotConfigRuntimeException(e);
        }
    }

    public BotConfigurationImpl(final long id, final boolean devicePath) {
        this.cache = String.format(cache, id);
        logger.info("[配置信息]缓存信息目录:{} {}", cache, new File(cache).mkdirs());
        if (devicePath) this.devicePath = "cache/device.json";
        else this.devicePath = cache + "device.json";
        try {
            init();
        } catch (IOException e) {
            throw new BotConfigRuntimeException(e);
        }
    }

    //初始化
    public void init() throws IOException {
        initBefore();
        redirectBotLogToFile(new File("./log/mirai/log.log"));
        redirectNetworkLogToFile(new File("./log/mirai/net.log"));
        //重定向日志
        MiraiLoggerPlatformBase log = new MiraiLog4j();
        setBotLoggerSupplier(bot -> log);
        setNetworkLoggerSupplier(bot -> log);
        logger.info("[配置信息]Mirai日志重定向为：{}", log);
        //设备信息
        fileBasedDeviceInfo(devicePath);
        logger.info("[配置信息]设备信息：{}", devicePath);
        // 心跳策略
        setHeartbeatStrategy(register);
        logger.info("[配置信息]心跳策略为：{}", register);
        // 登录协议
        setProtocol(version);
        logger.info("[配置信息]登录协议为：{}", version);
        // 运行目录
        //setWorkingDir(new File("C:/mirai"));
        logger.info("[配置信息]运行目录为： {}", getWorkingDir().getCanonicalPath());
        // 修改 Bot 缓存目录 (以运行目录为相对路径的坐标系)
        setCacheDir(new File(cache)); // 最终为 workingDir 目录中的 cache 目录
        logger.info("[配置信息]缓存目录为:{}", getCacheDir());
        //关闭日志
        if (isLog) noBotLog();
        logger.info("[配置信息]关闭log日志: {}", isLog);
        if (isNet) noNetworkLog();
        logger.info("[配置信息]关闭net日志: {}", isNet);
        // 开启所有列表缓存
        enableContactCache();
        logger.info("[配置信息]列表缓存已开启");

        //覆盖登录器
        ImageLoginSolver solver = new ImageLoginSolver();//无短信验证
        ImageLoginSolverKt solverKt = new ImageLoginSolverKt();//有短信验证
        setLoginSolver(solverKt);
        initAfter();
    }
    public void initAfter(){}
    public void initBefore(){}
}
