package github.zimoyin.mtool.run;

import github.zimoyin.mtool.config.global.LoginConfig;
import github.zimoyin.mtool.login.Login;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.utils.BotConfiguration;

/**
 * 登录Mirai
 */
@Slf4j
public class LoginMirai {
    /**
     * 登录配置文件中定义的账号与密码
     */
    public static void login(BotConfiguration configuration) {
        try{
            LoginConfig.getInstance().getLoginInfo().getUser().forEach((name, user) -> {
                Login.login(user.getId(), user.getPassword(),configuration);
                log.info("登录：{}({})", name, user.getId());
                System.out.println("============================================================================================================");
            });
        }catch (Exception e){
            log.error("通过配置文件中账号信息登录QQ失败");
        }
    }
}
