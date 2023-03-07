package tmmi.skyice.fabricbackpacksync.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.network.ServerPlayerEntity;
import tmmi.skyice.fabricbackpacksync.tool.LogUtil;

public class playData {
    public static void changeBag(ServerPlayerEntity player, String data) {
        try {
            //解析Json字符串
            JsonObject dataObj = JsonParser.parseString(data).getAsJsonObject();
            //数据库获取背包nbt
            NbtList inventoryNbt = (NbtList) new StringNbtReader(new StringReader(dataObj.get("inventory").getAsString())).parseElement();
            //设置背包
            try {
                player.getInventory().readNbt(inventoryNbt);
                LogUtil.LOGGER.info("替换成功");
            } catch (Exception e) {
                LogUtil.LOGGER.info("替换失败");
                e.printStackTrace();
            }

            //设置经验
            //数据库获取XP
            player.experienceProgress = dataObj.get("xp").getAsFloat();
            //设置等级
            //从数据库获取经验Level
            player.experienceLevel = dataObj.get("level").getAsInt();

        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
    }
}
