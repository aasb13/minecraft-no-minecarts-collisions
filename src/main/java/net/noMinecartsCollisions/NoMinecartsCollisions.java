package net.noMinecartsCollisions;

import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public final class NoMinecartsCollisions extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getLogger().info("NoMinecartsCollisions has been enabled");

        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("NoMinecartsCollisions has been disabled");
    }

    @EventHandler(ignoreCancelled = true)
    public void onMinecartCollision(VehicleEntityCollisionEvent event) {
        if (event.getEntity() instanceof org.bukkit.entity.Mob) {
            event.setCancelled(true);
            return;
        }

        if (!(event.getVehicle() instanceof Minecart collider) || !(event.getEntity() instanceof Minecart target)) {
            return;
        }

        // Cancel vanilla collision
        event.setCancelled(true);

        Vector colliderVel = collider.getVelocity();
        Vector targetVel = target.getVelocity();

        double colliderSpeedSq = colliderVel.lengthSquared();
        double targetSpeedSq = targetVel.lengthSquared();

        if (targetSpeedSq > 0.01 && colliderSpeedSq > 0.01) {
            return;
        }

        if (targetSpeedSq < 0.001) {  // Standing still threshold
            if (colliderSpeedSq > 0.01) {  // Collider has momentum
                Vector pushVel = colliderVel.clone().normalize().multiply(
                        colliderVel.length() * 0.75  // 0.75x collider's speed
                );
                target.setVelocity(pushVel);
            }
            return;
        }

        if (colliderSpeedSq < 0.001 && targetSpeedSq > 0.01) {
            Vector catchupVel = targetVel.clone().normalize().multiply(
                    targetVel.length() * 0.5  // 0.5x target's speed
            );
            collider.setVelocity(catchupVel);
        }
    }

}
