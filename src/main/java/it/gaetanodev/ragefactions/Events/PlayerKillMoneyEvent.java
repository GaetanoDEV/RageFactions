package it.gaetanodev.ragefactions.Events;

import it.gaetanodev.ragefactions.RageFactions;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerKillMoneyEvent implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killed = event.getEntity();
        Player killer = killed.getKiller();

        if (killer != null) {
            // Ottiene l'economia da Vault
            Economy economy = RageFactions.getEconomy();

            // Calcola la % dei soldi del giocatore ucciso
            double transferAmount = economy.getBalance(killed) * RageFactions.instance.getConfig().getDouble("kill-moneypercent");
            String amount = String.format("%,.0f", transferAmount);

            // Trasferisce i soldi
            economy.withdrawPlayer(killed, transferAmount);
            economy.depositPlayer(killer, transferAmount);

            // Controllo se il giocatore ha 0 Soldi, annulla l'evento
            if (economy.getBalance(killed) == 0) {
                return;
            }

            killed.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("killed-moneylost").replace("%s", amount) + "€"));
            killer.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("killer-moneyerned").replace("%s", amount) + "€"));
        }
    }
}
