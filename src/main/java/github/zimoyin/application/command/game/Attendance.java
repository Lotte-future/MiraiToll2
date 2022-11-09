package github.zimoyin.application.command.game;

import github.zimoyin.application.dao.game.UserInfo;
import github.zimoyin.mtool.annotation.Command;
import github.zimoyin.mtool.annotation.CommandClass;
import github.zimoyin.mtool.command.CommandData;
import github.zimoyin.mtool.dao.H2Connection;
import github.zimoyin.mtool.dao.ResultMapObject;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Slf4j
@CommandClass
public class Attendance {
    public Attendance() {
        //每五分钟同步一次表，并重新拉取表内容
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Thread.currentThread().setName("Timer-database-userid-AutoUpdate");
                toTable();
            }
        }, 60 * 1000, 5 * 60 * 1000);
    }

    private final HashMap<Long, List<UserInfo>> map = new HashMap<>();

    @Command("签到")
    public void onAttendance(CommandData data, H2Connection h2) throws SQLException {
        List<UserInfo> list = map.get(data.getBotId());
        if (list == null) {
            list = new ResultMapObject<UserInfo>().parseObject(data.getBotId(), "select * from USERID", UserInfo.class);
            map.put(data.getBotId(), list);
        }

        int rand = new Random().nextInt(26) - 1; // 生成[1,25]区间的整数
        for (UserInfo userInfo : list) {
            if (userInfo.getId() == data.getSenderID()) {
                userInfo.setMoney(rand += userInfo.getMoney());
                data.sendMessage("您已签到: " + rand);
                return;
            }
        }

        data.sendMessage("(自动注册)您已签到: " + rand);
        H2Connection.getInstance().execute(data.getBotId(),
                "insert into USERID values (" + data.getSenderID() + "," + rand + ")"
        );
        toTable();
    }

    private void toTable() {
        for (Long id : map.keySet()) {
            H2Connection instance = H2Connection.getInstance();
            try (Connection connection = instance.getConnection(id); Statement statement = instance.getStatement(connection)) {
                for (UserInfo info : map.get(id)) {
                    statement.execute("update USERID set MONEY = " + info.getMoney() + " where ID = " + info.getId());
                }
            } catch (SQLException e) {
                log.error("无法持久化", e);
            }
        }
        map.clear();
    }
}
