package ar.edu.itba.ss.Integrators;

import ar.edu.itba.ss.GranularMedia.GranularMediaForce;
import ar.edu.itba.ss.Simulation;
import ar.edu.itba.ss.models.*;

import java.util.List;

public class VelocityVerlet extends Integrator {

    public VelocityVerlet(Double dt, ForceFunction forceFunction, Double W, Double L, Double D) {
        super(dt, forceFunction, W, L, D);
    }

    @Override
    public void moveParticle(Particle particle, Double time, List<Particle> neighbours, List<Wall> walls) {
            Vector2D force = forceFunction.getForce(particle, neighbours, walls);
            Vector2D predictedPosition = particle.getPosition().multiply(2d).add(particle.getPreviousPosition().multiply(-1d)).add(force.multiply(dt*dt/particle.getMass()));
            Vector2D predictedVelocity = predictedPosition.add(particle.getPreviousPosition().multiply(-1d)).multiply(1d/(2d*dt));

            particle.setFutureState(new State(
                    predictedPosition.getX(), predictedPosition.getY(),
                    predictedVelocity.getX(), predictedVelocity.getY(),
                    0d,0d, particle.getPressure()
            ));
    }
}
