package ar.edu.itba.ss.Integrators;

import ar.edu.itba.ss.models.ForceFunction;
import ar.edu.itba.ss.models.Particle;
import ar.edu.itba.ss.models.Wall;

import java.util.Collections;
import java.util.List;

public abstract class Integrator {
    Double dt, W, L, D;
    ForceFunction forceFunction;

    public Integrator(Double dt, ForceFunction forceFunction, Double W, Double L , Double D) {
        this.dt = dt;
        this.forceFunction = forceFunction;
        this.W = W;
        this.L = L;
        this.D = D;
    }

    public abstract void moveParticle(Particle particle, Double time, List<Particle> neighbours, List<Wall> walls);

    public Double unidimensionalNextPosition(Particle particle, Double time) {
        moveParticle(particle, time, Collections.emptyList(), Collections.emptyList());
        particle.updateState();
        return particle.getY();
    }
}
