package github.zimoyin.tool.command.game;

import github.zimoyin.mtool.annotation.Command;
import github.zimoyin.mtool.annotation.CommandClass;
import github.zimoyin.mtool.command.CommandData;
import github.zimoyin.mtool.dao.H2Connection;

@CommandClass
public class Attendance {
    @Command("签到")
    public void onAttendance(CommandData data, H2Connection h2) {
    }


    private void read(CommandData data, H2Connection h2) {
        //注册角色
//        ResultSet userid = h2.executeQuery(data.getBotId(), "SELECT * FROM USERID where id =" + data.getSenderID());
    }
}
