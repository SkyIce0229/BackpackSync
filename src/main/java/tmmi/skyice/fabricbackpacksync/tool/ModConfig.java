package tmmi.skyice.fabricbackpacksync.tool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.yaml.snakeyaml.DumperOptions;
import tmmi.skyice.fabricbackpacksync.data.ConfigData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;


public class ModConfig {

    public static ConfigData configData;

    public static void createConfigYaml() throws IOException, SQLException {

        //设置yml格式，一般使用的是最喜欢的格式
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        //创建FileWrite
        File file = new File("./config/backpack.yml");
        if (!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        if (!file.exists()){
            file.createNewFile();
        }

        //初始化配置文件
        //当不存在或为空时创建文件
        if (!file.exists() || file.length() == 0) {
            try (FileOutputStream stream = new FileOutputStream(file)) {
                ConfigData configData = new ConfigData();
                configData.setMysqlData(new ConfigData.MysqlData());
                byte[] bytes = new YAMLMapper().writeValueAsBytes(configData);
                stream.write(bytes);

            }
        }

        //读取配置文件
        try (FileInputStream stream = new FileInputStream(file)){
            byte[] bytes = stream.readAllBytes();

            String data = new String(bytes, StandardCharsets.UTF_8);
            ObjectMapper mapper = new YAMLMapper();
            configData = mapper.readValue(data,ConfigData.class);
            MysqlUtil.initDatabase(configData);
        }


    }

    public static ConfigData getConfigData() {
        return configData;
    }
}
