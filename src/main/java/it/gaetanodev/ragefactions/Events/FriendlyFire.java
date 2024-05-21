package it.gaetanodev.ragefactions.Events;

import it.gaetanodev.ragefactions.Faction;
import it.gaetanodev.ragefactions.FactionManager;
import it.gaetanodev.ragefactions.RageFactions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;


public class FriendlyFire implements Listener {
    private final FactionManager factionManager;

    public FriendlyFire(FactionManager factionManager) {
        this.factionManager = factionManager;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            Player victim = (Player) event.getEntity();

            String attackerFactionName = factionManager.playerFactions.get(attacker.getUniqueId().toString());
            String victimFactionName = factionManager.playerFactions.get(victim.getUniqueId().toString());

            // Se i giocatori sono della stessa Fazione annulla l'evento
            if (attackerFactionName != null && attackerFactionName.equals(victimFactionName)) {
                event.setCancelled(true);
                attacker.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-friendlyfiresame")));
                return;
            }

            // Se i giocatori sono di fazioni alleate annulla l'evento
            if (attackerFactionName != null && victimFactionName != null) {
                Faction attackerFaction = factionManager.factions.get(attackerFactionName);
                Faction victimFaction = factionManager.factions.get(victimFactionName);

                if (attackerFaction.getAllies().contains(victimFactionName) && victimFaction.getAllies().contains(attackerFactionName)) {
                    event.setCancelled(true);
                    attacker.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-friendlyfire")));
                }
            }
        }
    }
}

