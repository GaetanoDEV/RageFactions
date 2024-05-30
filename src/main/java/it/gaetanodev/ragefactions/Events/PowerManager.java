package it.gaetanodev.ragefactions.Events;

import it.gaetanodev.ragefactions.Faction;
import it.gaetanodev.ragefactions.FactionManager;
import it.gaetanodev.ragefactions.RageFactions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PowerManager implements Listener {
    private final FactionManager factionManager;

    public PowerManager(FactionManager factionManager) {
        this.factionManager = factionManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killed = event.getEntity();
        Player killer = killed.getKiller();

        Faction killedFaction = factionManager.getFaction(killed);
        if (killedFaction != null) {
            killedFaction.decreasePower();
            RageFactions.instance.saveFaction(killedFaction);
            killed.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-killed-decreasepower")));
        }

        if (killer != null) {
            Faction killerFaction = factionManager.getFaction(killer);
            if (killerFaction != null && killedFaction != null) {
                killerFaction.increasePower();
                RageFactions.instance.saveFaction(killerFaction);
                killer.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-killer-power")));
            }
        }
    }
}

