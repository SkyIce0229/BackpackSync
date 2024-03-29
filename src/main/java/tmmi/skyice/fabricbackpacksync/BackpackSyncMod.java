package tmmi.skyice.fabricbackpacksync;


import com.google.gson.JsonObject;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import tmmi.skyice.fabricbackpacksync.data.playData;
import tmmi.skyice.fabricbackpacksync.tool.LogUtil;
import tmmi.skyice.fabricbackpacksync.tool.ModConfig;
import tmmi.skyice.fabricbackpacksync.tool.MysqlUtil;

import java.util.Timer;
import java.util.TimerTask;

public class BackpackSyncMod implements DedicatedServerModInitializer {
    public static final String MOD_ID = "backpacksync";
    public static String version;

    static {
        ModContainer modContainer = FabricLoader.getInstance().getModContainer(MOD_ID).orElse(null);
        if (modContainer != null) {
            // 获取元数据
            ModMetadata modMetadata = modContainer.getMetadata();
            version = modMetadata.getVersion().getFriendlyString();
        }
    }

    public static final String MOD_Version = version;

    @Override
    public void onInitializeServer() {
//服务器初始化完毕
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            try {
                ModConfig.createConfigYaml();
                LogUtil.LOGGER.info("-------------------------------------------------------");
                LogUtil.LOGGER.info("MOD已就绪");
                LogUtil.LOGGER.info(MOD_ID + "当前版本：" + MOD_Version);
                LogUtil.LOGGER.info("作者：SkyIce");
                LogUtil.LOGGER.info("-------------------------------------------------------");
                LogUtil.LOGGER.info("感谢您的使用");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            //定时器：半个小时保存一次
            Timer savedata = new Timer();
            savedata.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {
                    try {
                        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                            //读取玩家背包nbt然后赋值
                            String nbtStr = player.getInventory().writeNbt(new NbtList()).toString();
                            JsonObject dataObj = new JsonObject();
                            //往对象加入关键词
                            dataObj.addProperty("inventory", nbtStr);
                            dataObj.addProperty("xp", player.experienceProgress);
                            dataObj.addProperty("level", player.experienceLevel);
                            if (MysqlUtil.updataTable(player.getName().getString(), dataObj.toString())) {
                                LogUtil.LOGGER.info("更新成功");
                            } else if (MysqlUtil.insertTable(player.getName().getString(), dataObj.toString())) {
                                LogUtil.LOGGER.info("保存成功");
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 1000 * 60 * 2, 1000 * 60 * 2);
        });


        //玩家进入服务器
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            new Thread(() -> {
                ServerPlayerEntity player = handler.getPlayer();
                Timer later = new Timer();
                later.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        String data = MysqlUtil.selectData(player.getName().getString());
                        if (data != null) {
                            player.getInventory().clear();
                            playData.changeBag(player, data);
                        }
                    }
                },1000);
            }).start();
        });

        //玩家离开服务器
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            new Thread(() -> {
                try {
                    ServerPlayerEntity player = handler.getPlayer();
                    //读取玩家背包nbt然后赋值
                    String nbtStr = player.getInventory().writeNbt(new NbtList()).toString();
                    JsonObject dataObj = new JsonObject();
                    //往对象加入关键词
                    dataObj.addProperty("inventory", nbtStr);
                    dataObj.addProperty("xp", player.experienceProgress);
                    dataObj.addProperty("level", player.experienceLevel);
                    if (MysqlUtil.updataTable(player.getName().getString(), dataObj.toString())) {
                        LogUtil.LOGGER.info("更新成功");
                    } else if (MysqlUtil.insertTable(player.getName().getString(), dataObj.toString())) {
                        LogUtil.LOGGER.info("保存成功");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }
}


