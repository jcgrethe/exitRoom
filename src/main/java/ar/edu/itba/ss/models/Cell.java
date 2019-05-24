package ar.edu.itba.ss.models;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    private List<Particle> particles = new ArrayList<>();

    public Cell(){
    }

    public void addParticle(Particle particle){
        this.particles.add(particle);
    }

    public int getParticlesQuantity(){
        return particles.size();
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public void removeAll() {
        particles.clear();
    }
}
