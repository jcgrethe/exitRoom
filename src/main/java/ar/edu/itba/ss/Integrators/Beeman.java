package ar.edu.itba.ss.Integrators;

import ar.edu.itba.ss.Simulation;
import ar.edu.itba.ss.models.*;

import java.util.List;

public class Beeman extends Integrator {
    public Beeman(Double dt, ForceFunction forceFunction, Double W, Double L, Double D) {
        super(dt, forceFunction, W, L, D);
    }

    @Override
    public void moveParticle(Particle particle, Double time, List<Particle> neighbours, List<Wall> walls) {
        Vector2D force = forceFunction.getForce(particle, neighbours, walls);
        Vector2D previousAcceleration;
        if (time == 0){
            previousAcceleration = new Vector2D(
                    force.getX()/particle.getMass(),
                    force.getY()/particle.getMass()
            );
        }else{
            previousAcceleration = new Vector2D(
                    particle.getPreviousAcceleration().getX(),
                    particle.getPreviousAcceleration().getX()
            );
        }
        Vector2D position = particle.getPosition()
                .add(particle.getVelocity().multiply(dt))
                .add(particle.getAcceleration().multiply(2d*dt*dt/3d))
                .add(previousAcceleration.multiply(-dt*dt/6d));
        Particle predictedPositionParticle = new Particle(
                particle.getRadius(), particle.getMass(),
                position.getX(), position.getY(),
                particle.getvX(), particle.getvY());


        //TODO: CHECK IF REALLY NEED TO RECALCULATE WALLS!?
        walls = Simulation.getWallsCollisions(predictedPositionParticle, W, L , D);


        Vector2D predictedAcceleration = forceFunction.getForce(predictedPositionParticle, neighbours, walls)
                .multiply(1d/particle.getMass());
        Vector2D velocity = particle.getVelocity()
                .add(predictedAcceleration.multiply(dt/3d))
                .add(particle.getAcceleration().multiply(5d*dt/6d))
                .add(previousAcceleration.multiply(-dt/6d));

        particle.setFutureState(new State(
                position.getX(),position.getY(),
                velocity.getX(), velocity.getY(),
                predictedAcceleration.getX(), predictedAcceleration.getY(), particle.getPressure()
        ));
    }
}
