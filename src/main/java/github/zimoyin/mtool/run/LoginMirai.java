package github.zimoyin.mtool.run;

import github.zimoyin.mtool.config.global.LoginConfig;
import github.zimoyin.mtool.login.Login;
import lombok.extern.slf4j.Slf4j;

/**
 * 登录Mirai
 */
@Slf4j
public class LoginMirai {
    /**
     * 登录配置文件中定义的账号与密码
     */
    public static void login() {
        LoginConfig.getInstance().getLoginInfo().getUser().forEach((name, user) -> {
            Login.login(user.getId(), user.getPassword());
            log.info("登录：{}({})", name, user.getId());
            System.out.println("============================================================================================================");
        });
    }
}
