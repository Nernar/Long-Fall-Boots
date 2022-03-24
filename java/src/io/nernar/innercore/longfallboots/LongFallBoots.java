package io.nernar.innercore.longfallboots;

import com.zhekasmirnov.apparatus.adapter.innercore.game.entity.EntityActor;
import com.zhekasmirnov.apparatus.adapter.innercore.game.item.ItemStack;
import com.zhekasmirnov.apparatus.mcpe.NativePlayer;
import com.zhekasmirnov.innercore.api.NativeAPI;
import com.zhekasmirnov.innercore.api.NativeItem;
import com.zhekasmirnov.innercore.api.commontypes.ItemInstance;
import com.zhekasmirnov.innercore.api.constants.ArmorType;
import com.zhekasmirnov.innercore.api.constants.EnchantType;
import com.zhekasmirnov.innercore.api.constants.ItemCategory;
import com.zhekasmirnov.innercore.api.mod.util.ScriptableFunctionImpl;
import com.zhekasmirnov.innercore.api.runtime.Callback;
import com.zhekasmirnov.innercore.api.runtime.other.NameTranslation;
import com.zhekasmirnov.innercore.api.unlimited.IDRegistry;
import com.zhekasmirnov.innercore.mod.build.Config;
import java.util.HashMap;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class LongFallBoots {
	public static final int LONG_FALL_BOOTS_ID = IDRegistry.genItemID("long_fall_boots");
	
	private static boolean isHardModeEnabled = false;
	
	public static boolean isHardMode() {
		return isHardModeEnabled;
	}
	
	private static class PreBlocksDefined extends ScriptableFunctionImpl {
		
		@Override
		public Object call(Context context, Scriptable scriptable, Scriptable scope, Object[] objects) {
			NativeItem.createArmorItem(LONG_FALL_BOOTS_ID, "long_fall_boots", "Long Fall Boots", "long_fall_boots", 0, "textures/models/armor/long_fall.png", ArmorType.boots, 3, 429, 0);
			NativeItem.addToCreative(LONG_FALL_BOOTS_ID, 1, 0, null);
			NativeItem.setCreativeCategoryForId(LONG_FALL_BOOTS_ID, ItemCategory.EQUIPMENT);
			NativeItem bootsItem = NativeItem.getItemById(LONG_FALL_BOOTS_ID);
			bootsItem.setEnchantability(EnchantType.boots, 10);
			bootsItem.setMaxStackSize(1);
			return null;
		}
	}
	
	private static final int DAMAGE_TYPE_FALL = 5;

	
	private static class EntityHurt extends ScriptableFunctionImpl {
		
		@Override
		public Object call(Context context, Scriptable scriptable, Scriptable scope, Object[] objects) {
			if (((Integer) objects[3]).intValue() == DAMAGE_TYPE_FALL) {
				long entityUid = ((Long) objects[1]).longValue();
				EntityActor actor = new EntityActor(entityUid);
				ItemStack boots = actor.getArmorSlot(ArmorType.boots);
				if (boots != null && boots.id == LONG_FALL_BOOTS_ID) {
					NativeAPI.preventDefault();
					int damageValue = ((Integer) objects[2]).intValue();
					NativeAPI.setVelocityAxis(entityUid, 1, (float) (Math.atan(damageValue / 16) / 2.f));
				}
			}
			return null;
		}
	}
	
	private static class ItemUseNoTarget extends ScriptableFunctionImpl {
		
		@Override
		public Object call(Context context, Scriptable scriptable, Scriptable scope, Object[] objects) {
			ItemInstance item = (ItemInstance) objects[0];
			if (item.getId() == LONG_FALL_BOOTS_ID) {
				long playerUid = ((Long) objects[1]).longValue();
				NativePlayer player = new NativePlayer(playerUid);
				ItemStack equipped = player.getArmor(ArmorType.boots);
				int slot = player.getSelectedSlot();
				ItemStack handed = player.getInventorySlot(slot);
				player.setArmor(ArmorType.boots, handed.id, handed.count, handed.data, handed.extra);
				if (!equipped.isEmpty()) {
					player.setInventorySlot(slot, equipped.id, equipped.count, equipped.data, equipped.extra);
				} else {
					player.setInventorySlot(slot, 0, 0, 0, null);
				}
				NativeAPI.playSoundEnt("armor.equip_iron", playerUid, 1f, 1f);
			}
			return null;
		}
	}
	
	static {
		Callback.addCallback("PreBlocksDefined", new PreBlocksDefined(), Integer.MAX_VALUE);
		Callback.addCallback("EntityHurt", new EntityHurt(), Integer.MAX_VALUE);
		Callback.addCallback("ItemUseNoTarget", new ItemUseNoTarget(), Integer.MAX_VALUE);
	}
	
	public static void boot(HashMap sources) {
		HashMap<String, String> nameTranslation = new HashMap<>();
		nameTranslation.put("fr", "Bottes de longue chute");
		nameTranslation.put("ru", "Сапоги прыгуна");
		NameTranslation.addTranslation("Long Fall Boots", nameTranslation);
	}
	
	public static void setConfig(Config config) {
		isHardModeEnabled = config.getBool("hardMode");
	}
}
