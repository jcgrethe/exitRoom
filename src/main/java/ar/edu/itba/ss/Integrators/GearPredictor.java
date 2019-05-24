package ar.edu.itba.ss.Integrators;

import ar.edu.itba.ss.Simulation;
import ar.edu.itba.ss.models.*;

import java.util.List;

public class GearPredictor extends Integrator {
    private final Integer[] periodicNumbers = {1, 1, 2, 6, 24, 120};

    private Double correctFactor0;
    private Double correctFactor1;
    private Double correctFactor2;
    private Double correctFactor3;
    private Double correctFactor4;
    private Double correctFactor5;

    public GearPredictor(Double dt, ForceFunction forceFunction, Double W, Double L, Double D) {
        super(dt, forceFunction, W, L, D);
        setFactors();
    }

    private void setFactors(){
        this.correctFactor0 = 3d / 20d;
        this.correctFactor1 = (251d / 360d) / dt;
        this.correctFactor2 = 1d * (2d / Math.pow(dt,2));
        this.correctFactor3 = (11d / 18d) * (6d / Math.pow(dt,3));
        this.correctFactor4 = (1d / 6d) * (24d / Math.pow(dt,4));
        this.correctFactor5 = (1d / 60d) * (120d / Math.pow(dt,5));
    }
    @Override
    public void moveParticle(Particle particle, Double time, List<Particle> neighbours, List<Wall> walls) {
        GPState gpState;
        if (particle.getGPState().isPresent()){
            gpState = particle.getGPState().get();
        }else {
            gpState = new GPState(
                    particle.getPosition(), particle.getVelocity(), particle.getAcceleration(),
                    new Vector2D(),
                    new Vector2D(),
                    new Vector2D()
            );
        }
            //Predict
            GPState predictedGPState = new GPState(
                    getR(gpState),
                    getR1(gpState),
                    getR2(gpState),
                    getR3(gpState),
                    getR4(gpState),
                    getR5(gpState)
            );

            Particle predictedParticle = new Particle(
                    particle.getRadius(), particle.getMass(),
                    predictedGPState.getR().getX(), predictedGPState.getR().getY(),
                    predictedGPState.getR1().getX(), predictedGPState.getR1().getY()
            );

            //TODO: CHECK IF REALLY NEED TO RECALCULATE WALLS!?
            walls = Simulation.getWallsCollisions(predictedParticle, W, L, D);

            //Evaluate
            Vector2D force = forceFunction.getForce(predictedParticle, neighbours, walls);
            Vector2D acceleration = force.multiply(1.0/particle.getMass());
            Vector2D deltaAcceleration = acceleration.add(predictedGPState.getR2().multiply(-1.0));
            Vector2D deltaR2 = deltaAcceleration.multiply(dt*dt/periodicNumbers[2]);

            //Correct
            GPState correctedGPState = new GPState(
                    predictedGPState.getR().add(deltaR2.multiply(correctFactor0)),
                    predictedGPState.getR1().add(deltaR2.multiply(correctFactor1)),
                    predictedGPState.getR2().add(deltaR2.multiply(correctFactor2)),
                    predictedGPState.getR3().add(deltaR2.multiply(correctFactor3)),
                    predictedGPState.getR4().add(deltaR2.multiply(correctFactor4)),
                    predictedGPState.getR5().add(deltaR2.multiply(correctFactor5))
            );
            State newParticleState = new State(
              correctedGPState.getR().getX(),
              correctedGPState.getR().getY(),
              correctedGPState.getR1().getX(),
              correctedGPState.getR1().getY(),
              correctedGPState.getR2().getX(),
              correctedGPState.getR2().getY(),
                    particle.getPressure()
            );

            // Finally, update the new state
            newParticleState.changeGPState(correctedGPState);
            particle.setFutureState(newParticleState);
    }

    private Vector2D getR(GPState gpState){
        return new Vector2D(gpState.getR())
                .add(gpState.getR1().multiply(Math.pow(dt,1)/periodicNumbers[1]))
                .add(gpState.getR2().multiply(Math.pow(dt,2)/periodicNumbers[2]))
                .add(gpState.getR3().multiply(Math.pow(dt,3)/periodicNumbers[3]))
                .add(gpState.getR4().multiply(Math.pow(dt,4)/periodicNumbers[4]))
                .add(gpState.getR5().multiply(Math.pow(dt,5)/periodicNumbers[5]));
    }

    private Vector2D getR1(GPState gpState){
        return new Vector2D(gpState.getR1())
                .add(gpState.getR2().multiply(Math.pow(dt,1)/periodicNumbers[1]))
                .add(gpState.getR3().multiply(Math.pow(dt,2)/periodicNumbers[2]))
                .add(gpState.getR4().multiply(Math.pow(dt,3)/periodicNumbers[3]))
                .add(gpState.getR5().multiply(Math.pow(dt,4)/periodicNumbers[4]));
    }

    private Vector2D getR2(GPState gpState){
        return new Vector2D(gpState.getR2())
                .add(gpState.getR3().multiply(Math.pow(dt,1)/periodicNumbers[1]))
                .add(gpState.getR4().multiply(Math.pow(dt,2)/periodicNumbers[2]))
                .add(gpState.getR5().multiply(Math.pow(dt,3)/periodicNumbers[3]));
    }

    private Vector2D getR3(GPState gpState){
        return new Vector2D(gpState.getR3())
                .add(gpState.getR4().multiply(Math.pow(dt,1)/periodicNumbers[1]))
                .add(gpState.getR5().multiply(Math.pow(dt,2)/periodicNumbers[2]));
    }

    private Vector2D getR4(GPState gpState){
        return new Vector2D(gpState.getR4())
                .add(gpState.getR5().multiply(Math.pow(dt,1)/periodicNumbers[1]));
    }

    private Vector2D getR5(GPState gpState){
        return new Vector2D(gpState.getR5());
    }

}
