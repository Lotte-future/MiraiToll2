package github.zimoyin.tool.dao.table;

import github.zimoyin.mtool.dao.H2Connection;
import lombok.extern.slf4j.Slf4j;

/**
 * 统一创建表
 */
@Slf4j
public class CreateTable {
    String[] sqls = new String[]{
            "userid(id BIGINT primary key,money SMALLINT)"
    };

    public void create(long botID){
        StringBuilder builder = new StringBuilder();
        for (String sql : sqls) {
            builder.append("create table if not exists").append(" ").append(sql).append(";");
        }
        H2Connection.getInstance().execute(botID,builder.toString());
    }

    public void create(){
        StringBuilder builder = new StringBuilder();
        for (String sql : sqls) {
            builder.append("create table if not exists").append(" ").append(sql).append(";");
        }
        H2Connection.getInstance().execute(builder.toString());
    }
}
