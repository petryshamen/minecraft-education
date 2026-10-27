package org.humanjourney.core.data;

import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import org.humanjourney.core.config.TemperatureDataParser;
import org.humanjourney.core.config.TemperatureOverrides;
import org.slf4j.Logger;
import java.io.Reader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class TemperatureReloadListener implements PreparableReloadListener {
    public static final ResourceLocation FILE = ResourceLocation.fromNamespaceAndPath("human_journey", "core/temperature.json");
    private static final Logger LOGGER = LogUtils.getLogger();
    private record Prepared(TemperatureOverrides overrides, String source, String error) {}
    private Prepared prepare(ResourceManager manager) {
        try {
            var resource = manager.getResource(FILE);
            if (resource.isEmpty()) return new Prepared(null, null, null);
            try (Reader reader = resource.get().openAsReader()) {
                return new Prepared(TemperatureDataParser.parse(JsonParser.parseReader(reader)), resource.get().sourcePackId(), null);
            }
        } catch (Exception error) {
            return new Prepared(null, null, error.getMessage() == null ? error.getClass().getSimpleName() : error.getMessage());
        }
    }
    @Override
    public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager, Executor background, Executor game) {
        return CompletableFuture.supplyAsync(() -> prepare(manager), background)
            .thenCompose(barrier::wait)
            .thenAcceptAsync(prepared -> {
                if (prepared.error() != null) {
                    TemperatureBalance.STORE.reject(prepared.error());
                    LOGGER.error("[HJ Core] Rejected {}: {}. Keeping the last valid policy (or fallback).", FILE, prepared.error());
                } else if (prepared.overrides() == null) {
                    TemperatureBalance.STORE.missing();
                    LOGGER.info("[HJ Core] {} missing; using server config / defaults", FILE);
                } else {
                    TemperatureBalance.STORE.accept(prepared.overrides(), prepared.source());
                    LOGGER.info("[HJ Core] Loaded {} from {} (revision {})", FILE, prepared.source(), TemperatureBalance.STORE.snapshot().revision());
                }
            }, game);
    }
}
