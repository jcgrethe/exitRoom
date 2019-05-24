package ar.edu.itba.ss.models;

import ar.edu.itba.ss.models.Particle;
import ar.edu.itba.ss.models.Vector2D;

import java.util.List;
import java.util.Optional;

public interface ForceFunction {
    public Vector2D getForce(Particle particle, List<Particle> neighbours, List<Wall> walls);
}
