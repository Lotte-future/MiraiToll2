package github.zimoyin.tool.dao.table;

import github.zimoyin.mtool.dao.Cache;
import github.zimoyin.mtool.dao.CacheManager;
import github.zimoyin.mtool.dao.ResultMapObject;
import github.zimoyin.tool.dao.game.UserInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * 读取数据库数据进内存
 */
public class SelectTable {
    private String [] slqs = {
            "select * from userid"
    };

    public void select(long botID) throws SQLException {
        CacheManager manager = CacheManager.getInstance();
        //用户信息
        List<UserInfo> userInfos = new ResultMapObject<UserInfo>().parseObject(botID, slqs[0], UserInfo.class);
        manager.put(botID,new Cache<List<UserInfo>>().add(userInfos));
    }
}
