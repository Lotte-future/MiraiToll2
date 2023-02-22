package github.zimoyin.application.dao.table;

import github.zimoyin.mtool.dao.H2Connection;
import lombok.extern.slf4j.Slf4j;

/**
 * 统一创建表
 */
@Slf4j
public class CreateTable {
    public CreateTable() {
        create();
    }

    String[] sqls = new String[]{
            "userid(id BIGINT primary key,money SMALLINT)",
            "chat(id BIGINT primary key AUTO_INCREMENT,text_key VARCHAR_IGNORECASE,text_value VARCHAR_IGNORECASE)"
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
