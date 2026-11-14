package org.humanjourney.core.geography.map.client;
import java.nio.file.*;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
/** Runs before Forge registers/loads this client config, preserving the missing-key signal. */
public final class MapConfigMigration {
 public static final String MODE="world_map_geography.world_map_boundary_mode",LEGACY="world_map_geography.world_map_local_outlines_enabled";
 public static boolean migrate(CommentedConfig data){if(data.contains(MODE))return false;Object old=data.get(LEGACY);boolean disabled=Boolean.FALSE.equals(old)||old instanceof String s&&s.equalsIgnoreCase("false");data.set(MODE,disabled?"off":"hover");return true;}
 public static boolean migrateFile(Path file){if(!Files.isRegularFile(file))return false;try(var data=CommentedFileConfig.builder(file).sync().preserveInsertionOrder().build()){data.load();if(!migrate(data))return false;data.save();return true;}}
 public static void beforeRegistration(Path file){try{migrateFile(file);}catch(RuntimeException error){com.mojang.logging.LogUtils.getLogger().warn("[HJ Core] Could not migrate map presentation config; Forge validation/defaults remain active",error);}}
 private MapConfigMigration(){}
}
