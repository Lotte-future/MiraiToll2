package github.zimoyin.mtool.run;

import github.zimoyin.mtool.command.CommandLoad;
import github.zimoyin.mtool.command.CommandSet;
import github.zimoyin.mtool.config.global.LoginConfig;
import github.zimoyin.mtool.control.Controller;
import github.zimoyin.mtool.login.Login;
import lombok.extern.slf4j.Slf4j;

/**
 * 启动类
 */
@Slf4j
public class RunMain extends LoginMirai{
    /**
     * 启动框架
     */
    public static void main(String[] args) {
        RunMain.run();
    }

    /**
     * 启动框架
     */
    public static void init(){
        initLogin();
        initController();
        initCommand();
    }

    /**
     * 登录信息初始化
     */
    public static void initLogin(){
        //登录配置信息单例创建，并初始化
        LoginConfig.getInstance();
    }

    /**
     * 控制器初始化
     */
   public static void initController(){
        //控制器初始化
        Controller.init();
    }

    /**
     * 命令初始化
     */
    public static void initCommand(){
        //命令集合单例创建，并初始化
        CommandSet.getInstance();
        //初始化命令，将命令监听进命令集合中
        new CommandLoad().init();
    }

    public static void run() {
        //初始化框架
        init();
        //登录
        LoginMirai.login();
    }

    public static void run(long id,String password) {
        //初始化框架
        init();
        //登录
        Login.login(id,password);
    }

}
