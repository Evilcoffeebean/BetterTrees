package dev.better.trees.particle;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import java.util.Random;

public class TimberEffect {

    private static final Random random = new Random();
    private final Particle particle;
    private final int amount;

    public TimberEffect(Particle particle) {
        this(particle, 10);
    }

    public TimberEffect(Particle particle, int amount) {
        this.particle = particle;
        this.amount = amount;
    }

    private Vector randomCircularVector() {
        double rnd, x, z;
        rnd = random.nextDouble() * 2 * Math.PI;
        x = Math.cos(rnd);
        z = Math.sin(rnd);

        return new Vector(x, 0, z);
    }

    public void display(Location location) {
        if (location == null || location.getWorld() == null)
            return;

        for (int i = 0; i < amount; i++) {
            Vector v = randomCircularVector().multiply(random.nextDouble() * 0.6d);
            v.setY(random.nextFloat() * 1.8);
            location.add(v);
            location.getWorld().spawnParticle(particle, location, 0, 0, 0, 0);
            location.subtract(v);
        }
    }
}
