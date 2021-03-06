package mcjty.rftoolscontrol.proxy;

import mcjty.lib.network.PacketHandler;
import mcjty.lib.proxy.AbstractCommonProxy;
import mcjty.rftoolscontrol.CommandHandler;
import mcjty.rftoolscontrol.ForgeEventHandlers;
import mcjty.rftoolscontrol.RFToolsControl;
import mcjty.rftoolscontrol.blocks.ModBlocks;
import mcjty.rftoolscontrol.config.GeneralConfiguration;
import mcjty.rftoolscontrol.gui.GuiProxy;
import mcjty.rftoolscontrol.items.ModItems;
import mcjty.rftoolscontrol.logic.registry.Functions;
import mcjty.rftoolscontrol.logic.registry.Opcodes;
import mcjty.rftoolscontrol.network.RFToolsCtrlMessages;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.Level;

import java.io.File;

@Mod.EventBusSubscriber
public abstract class CommonProxy extends AbstractCommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
        CommandHandler.registerCommands();

        mainConfig = new Configuration(new File(modConfigDir.getPath() + File.separator + "rftools", "control.cfg"));
        readMainConfig();

        SimpleNetworkWrapper network = PacketHandler.registerMessages(RFToolsControl.MODID, "rftoolsctrl");
        RFToolsCtrlMessages.registerNetworkMessages(network);

        Opcodes.init();
        Functions.init();
        ModBlocks.init();
        ModItems.init();

//        if (RFToolsControl.mcmpPresent) {
//            MCMPSetup.init();
//        }
    }

    private void readMainConfig() {
        Configuration cfg = mainConfig;
        try {
            cfg.load();
            cfg.addCustomCategoryComment(GeneralConfiguration.CATEGORY_GENERAL, "General settings");

            GeneralConfiguration.init(cfg);
        } catch (Exception e1) {
            FMLLog.log(Level.ERROR, e1, "Problem loading config file!");
        } finally {
            if (mainConfig.hasChanged()) {
                mainConfig.save();
            }
        }
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        NetworkRegistry.INSTANCE.registerGuiHandler(RFToolsControl.instance, new GuiProxy());
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
        if (mainConfig.hasChanged()) {
            mainConfig.save();
        }
        mainConfig = null;
    }
}
