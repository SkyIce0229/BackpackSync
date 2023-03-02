package tmmi.skyice.fabricbackpacksync.tool;

import tmmi.skyice.fabricbackpacksync.data.ConfigData;

import java.sql.*;

public class MysqlUtil {
    static {
        String mysqlDriver = "com.mysql.cj.jdbc.Driver";
        try {
            //加载MySql驱动
            Class.forName(mysqlDriver);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void getConnection(ConfigData mysqlData) {

        //数据库地址
        String url = "jdbc:mysql://" + mysqlData.getMysqlData().getIp() + ":" + mysqlData.getMysqlData().getPort() + "/";//+"/backpack?useSSL=true&useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai";
        //数据库名字
        String sqlName = "backpack";
        String username = mysqlData.getMysqlData().getUsername();
        String password = mysqlData.getMysqlData().getPassword();

        //连接mysql服务
        try (Connection serverConnection = DriverManager.getConnection(url, username, password);
             Statement stmt = serverConnection.createStatement()) {
            //建库语句
            String createSql = "create database if not exists " + sqlName;
                //尝试创建数据库
                try {
                    if (stmt.executeUpdate(createSql) == 1){
                        LogUtil.LOGGER.info("数据库连接成功");
                    }
                } catch (SQLException e) {
                    LogUtil.LOGGER.error("创建数据库失败",e.getMessage());
                }

        } catch (SQLException e) {
            LogUtil.LOGGER.error("MySQL服务器连接失败",e.getMessage());
            return;
        }
        //连接数据库
        try (Connection dbConnection = DriverManager.getConnection(url+sqlName,username,password);
             Statement dbstmt = dbConnection.createStatement()) {
            //尝试建表
            String createtable = "CREATE TABLE IF NOT EXISTS `player_backpack` (`id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',`name` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,`nbt` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,PRIMARY KEY (`id`) USING BTREE,UNIQUE INDEX `uk_name`(`name` ASC) USING BTREE)";

            try {
                if (dbstmt.executeUpdate(createtable) == 1){
                    LogUtil.LOGGER.info("创建数据表成功");
                }
            }catch (SQLException e) {
                LogUtil.LOGGER.error("创建数据表失败:",e.getMessage());
            }
        }catch (SQLException e) {
            LogUtil.LOGGER.error("数据库连接失败:",e.getMessage());
            return;
        }


    }

        public  static Connection getConnection() throws SQLException {
            String sqlName = "backpack";
            ConfigData configData = ModConfig.getConfigData();
            String url = "jdbc:mysql://"+configData.getMysqlData().getIp()+":"+configData.getMysqlData().getPort()+"/";
            return DriverManager.getConnection(url+sqlName,configData.getMysqlData().getUsername(),configData.getMysqlData().getPassword());
    }

        public static boolean insertTable (String name, String inventory){
            //插入命令
            String inserttable = "insert into `player_backpack` (name, nbt) values (?,?)";
            //连接数据库
        try (Connection conn = getConnection();PreparedStatement dbstmt = conn.prepareStatement(inserttable)){
            dbstmt.setString(1, name);
            dbstmt.setString(2, inventory);
            LogUtil.LOGGER.info(inserttable);
            try {

                if (dbstmt.executeUpdate() == 1){
                    LogUtil.LOGGER.info("数据插入成功");
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                LogUtil.LOGGER.error("数据插入失败");
            }
        } catch (SQLException e) {
            LogUtil.LOGGER.error("数据库连接失败:",e.getMessage());
        }
           return false;
    }

    public static boolean updataTable (String name, String inventory){
        //插入命令
        String updatatable = "update player_backpack set nbt = ? where name = ?";
        //连接数据库
        try (Connection conn = getConnection();PreparedStatement dbstmt = conn.prepareStatement(updatatable)){
            dbstmt.setString(1, inventory);
            dbstmt.setString(2, name);

            try {
                if (dbstmt.executeUpdate() == 1){
                    LogUtil.LOGGER.info("数据更新成功");
                    return true;
                }
            } catch (SQLException e) {
                LogUtil.LOGGER.error("数据更新失败");
                e.printStackTrace();
            }
        } catch (SQLException e) {
            LogUtil.LOGGER.error("数据库连接失败:",e.getMessage());
        }
        return false;
    }

    public static String selectData (String name) {

        //连接数据库
        try (Connection conn = getConnection();Statement dbstmt = conn.createStatement()){
            //查询命令
            String selectdata = "select nbt from player_backpack where name = '"+name+"'";
            try {
                    ResultSet selected = dbstmt.executeQuery(selectdata);
                    if (selected.next()){
                        String data = selected.getString("nbt");
                        LogUtil.LOGGER.info("查询到数据");
                        return data;
                    }
                } catch (SQLException e) {
                LogUtil.LOGGER.error("查询结果为空");
            }

        } catch (SQLException e) {
            LogUtil.LOGGER.error("数据库连接失败:",e.getMessage());
        }

        return null;
    }




}
