package cliffordha.totvw.config;

import cliffordha.totvw.client.screen.ConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.api.UpdateChecker;

import java.util.Map;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigScreen::create;
    }

    @Override
    public Map<String, UpdateChecker> getProvidedUpdateCheckers() {
        return ModMenuApi.super.getProvidedUpdateCheckers();
    }
}