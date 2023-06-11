package tmmi.skyice.fabricbackpacksync.data;


public class ConfigData {
    private MysqlData mysqlData;

    public MysqlData getMysqlData() {
        return mysqlData;
    }

    public ConfigData setMysqlData(MysqlData mysqlData) {
        this.mysqlData = mysqlData;
        return null;
    }
    public static class MysqlData {

        private String ip = "localhost";
        private int port = 3306;
        private String username = "root";
        private String password = "123456";
        private String database = "minecraft";

        public MysqlData(){}
        public MysqlData (String ip, int port, String username, String password, String database){
            this.ip = ip;
            this.port = port;
            this.username = username;
            this.password = password;
            this.database = database;
        }



        public String getIp() { return ip;}

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getDatabase() {
            return database;
        }

        public void setDatabase(String database) {
            this.database = database;
        }

        @Override
        public String toString() {
            return "MysqlData{" +
                    "ip='" + ip + '\'' +
                    ", port=" + port +
                    ", username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    ", password='" + database + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ConfigData{" +
                "mysqlData=" + mysqlData +
                '}';
    }
}
