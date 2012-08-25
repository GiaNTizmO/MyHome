package me.taylorkelly.myhome.listeners;

import me.taylorkelly.myhome.HomeSettings;
import me.taylorkelly.myhome.timers.WarmUp;

import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Animals;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;


public class MHEntityListener implements Listener {
	private Plugin plugin;
	public MHEntityListener(Plugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR) 
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.isCancelled() || !(event instanceof EntityDamageByEntityEvent) || !(event.getEntity() instanceof LivingEntity) || HomeSettings.abortOnDamage == 0)
			return;
		
		final LivingEntity victim = (LivingEntity) event.getEntity();
		final Entity aggressor =((EntityDamageByEntityEvent)event).getDamager();	

		if(HomeSettings.abortOnDamage == 3) {
			if(victim instanceof Player) {
				Player vplayer = (Player) event.getEntity();
				WarmUp.cancelWarming(vplayer, plugin, WarmUp.Reason.DAMAGE);
			}
			if(aggressor instanceof Player) {
				Player aplayer = (Player) ((EntityDamageByEntityEvent)event).getDamager();
				WarmUp.cancelWarming(aplayer, plugin, WarmUp.Reason.DAMAGE);
			}
		} else if(HomeSettings.abortOnDamage == 2) {
			if(victim instanceof Player && (((aggressor instanceof Monster) || (aggressor instanceof Animals)) && !(aggressor instanceof Player))) {
				Player vplayer = (Player) event.getEntity();
				WarmUp.cancelWarming(vplayer, plugin, WarmUp.Reason.DAMAGE);
			}
			if(aggressor instanceof Player && (((victim instanceof Monster) || (victim instanceof Animals)) && !(victim instanceof Player))) {
				Player aplayer = (Player) ((EntityDamageByEntityEvent)event).getDamager();
				WarmUp.cancelWarming(aplayer, plugin, WarmUp.Reason.DAMAGE);
			}			
		} else if(HomeSettings.abortOnDamage == 1) {
			if(victim instanceof Player && aggressor instanceof Player) {
				Player vplayer = (Player) event.getEntity();
				WarmUp.cancelWarming(vplayer, plugin, WarmUp.Reason.DAMAGE);
				Player aplayer = (Player) ((EntityDamageByEntityEvent)event).getDamager();
				WarmUp.cancelWarming(aplayer, plugin, WarmUp.Reason.DAMAGE);
			}			
		}
	}
}
