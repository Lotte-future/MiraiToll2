package github.zimoyin.mtool.run;

import github.zimoyin.mtool.command.CommandLoadInit;
import github.zimoyin.mtool.command.CommandSet;
import github.zimoyin.mtool.command.filter.FilterTable;
import github.zimoyin.mtool.command.filter.GlobalFilterInitOrExecute;
import github.zimoyin.mtool.config.global.LevelConfig;
import github.zimoyin.mtool.config.global.LoginConfig;
import github.zimoyin.mtool.control.Controller;
import github.zimoyin.mtool.login.Login;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.utils.BotConfiguration;

import java.util.function.Consumer;

/**
 * 启动类
 */
@Slf4j
public class RunMain extends LoginMirai {
    /**
     * 启动框架
     */
    public static void main(String[] args) {
        //启动框架
        RunMain.run();
        //启动cli
    }

    /**
     * 启动框架
     */
    public static void initAll() {
        initLogin();
        initController();
        initCommand();
        GlobalFilterInit();
        initLevel();
    }

    /**
     * 登录信息初始化
     */
    public static void initLogin() {
        //登录配置信息单例创建，并初始化
        LoginConfig.getInstance();
    }

    /**
     * 控制器初始化
     */
    public static void initController() {
        //控制器初始化
        Controller.init();
    }

    /**
     * 命令初始化
     */
    public static void initCommand() {
        //命令集合单例创建，并初始化
        CommandSet.getInstance();
    }

    /**
     * 权限初始化
     */
    public static void initLevel() {
        LevelConfig.LevelConfigInfo info = LevelConfig.getInstance().getCommandConfigInfo();
        FilterTable filterTable = FilterTable.getInstance();
        if (info.getSystems() != null) filterTable.getSystem().addAll(info.getSystems());
        if (info.getSystems() != null) for (LevelConfig.LevelConfigItem item : info.getLevels()) {
            Long groupid = item.getGroupid();
            if (item.getRoots() != null) for (Long id : item.getRoots()) {
                filterTable.setRoot(groupid, id);
            }
            if (item.getFirsts() != null) for (Long id : item.getFirsts()) {
                filterTable.setFirst(groupid, id);
            }
            if (item.getSeconds() != null) for (Long id : item.getSeconds()) {
                filterTable.setSecond(groupid, id);
            }
        }
    }

    /**
     * 全局过滤器初始化
     */
    public static void GlobalFilterInit() {
        GlobalFilterInitOrExecute.getInstance();
    }


    public static void run(BotConfiguration configuration) {
        //初始化框架
        initAll();
        //登录
        LoginMirai.login(configuration);
    }

    public static void run() {
        //初始化框架
        initAll();
        //登录
        LoginMirai.login(null);
    }

    @Deprecated
    public static void run(long id, String password) {
        //初始化框架
        initAll();
        //登录
        Login.login(id, password);
    }

}
