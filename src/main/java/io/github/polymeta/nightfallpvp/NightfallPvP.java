package io.github.polymeta.nightfallpvp;

import com.google.inject.Inject;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.permission.Context;
import com.griefdefender.api.permission.option.Options;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;

import java.util.HashSet;

@Plugin(id = "nightfallpvp",
        name = "Nightfall PvP",
        description = "Let PvP rule the nights, while days remain save.",
        version = "1.0.4",
        authors = {"Polymeta"})
public class NightfallPvP
{
    @Inject
    private Logger logger;

    private boolean isDaylight = true;
    private boolean isNightfall = true;

    @Listener
    public void onInit(GameInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event)
    {
        logger.info("Nightfall PvP by Polymeta!");
        logger.info("locked and loaded");
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event)
    {
        Sponge.getServer().getWorldProperties(Sponge.getServer().getDefaultWorldName()).ifPresent(worldProperties ->
        {
            int worldTime = (int) (worldProperties.getWorldTime() % 24000L);
            //logger.warn(Integer.toString(worldTime));
            if (worldTime > 13000 && worldTime < 23000)
            {
                //official nighttime
                if (isDaylight)
                {
                    //it was day before; change
                    Claim claim = GriefDefender.getCore().getClaimManager(worldProperties.getUniqueId()).getWildernessClaim();
                    //claim.setFlagPermission(Flags.ENTITY_DAMAGE, Tristate.TRUE, new HashSet<Context>(){{add(new Context("gd_claim", claim.getUniqueId().toString()));}});
                    claim.setOption(Options.PVP, "true", new HashSet<Context>()
                    {{
                        add(new Context("gd_claim", claim.getUniqueId().toString()));
                    }});
                    isDaylight = false;
                    isNightfall = true;
                    logger.info("The night came over, PvP has been activated");
                }
            }
            else
            {
                //daytime dawn dusk etc
                //it is daytime in world
                if (isNightfall)
                {
                    //it was night before; change
                    Claim claim = GriefDefender.getCore().getClaimManager(worldProperties.getUniqueId()).getWildernessClaim();
                    claim.setOption(Options.PVP, "false", new HashSet<Context>()
                    {{
                        add(new Context("gd_claim", claim.getUniqueId().toString()));
                    }});
                    isDaylight = true;
                    isNightfall = false;
                    logger.info("The Day rose, PvP has been deactivated");
                }
            }
        });
    }
}
