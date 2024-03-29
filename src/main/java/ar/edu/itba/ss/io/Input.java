package ar.edu.itba.ss.io;
import ar.edu.itba.ss.models.Grid;
import ar.edu.itba.ss.models.Particle;
import ar.edu.itba.ss.models.Vector2D;
import javafx.util.Pair;

import java.io.IOException;
import java.nio.DoubleBuffer;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Input {
    private List<Particle> particles;
    private int particlesQuantity = Integer.MAX_VALUE;  // Will put maximum possible particles
    private double L=20;
    private double W=20;
    private double D=1.2;
    private final double minRadio = 0.5/2;
    private final double maxRadio = 0.58/2;
    private double Kn= 1.2E5;
    private double Kt = 2.4E5;
    private final double y = 70d;
    private final double mass = 80;
    private double endTime = 10.0;
    private double cellSideLength;
    private double interactionRadio = 0.05;

    private double totalTries = 1E6;    //TODO: Not too much?
    private double tries = 0;

    private static double MAXDESIREDVELOCITY =  5.0;
    private static double MINDESIREDVELOCITY =  5.0;


    private static double DESIREDVELOCITY = - 5.0;


    public Input(){

    }

    /**
     * Empty constructor generates random inputs based in the max and min setted for each variable.
     */
    public Input(Long quantity, double dt, double v){
        System.out.println("[Generating Input... ");
//        dt = 0.1*Math.sqrt(mass/Kn);

        this.particles = new ArrayList<>();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        this.cellSideLength = maxRadio * 4;

        System.out.println("L:" + L + "; W:" + W + "; D:" + D + " ; Kt:" + Kt + " ; Kn:" + this.Kn + "; Velocity:" + v+ "; DT: " + dt);

        //Maximum particle quantity
        while(tries < totalTries && particles.size() < quantity ) {
            double radius = random.nextDouble(minRadio,maxRadio);
            double desiredVelocity = v;
            Particle potential = new Particle(
                    radius,
                    mass,
                    random.nextDouble(0 + maxRadio,W - maxRadio),
                    random.nextDouble(5 + maxRadio,L- maxRadio), //TODO remove /3 just for testing
                    0.0,
                    0.0,
                    dt,
                    desiredVelocity
            );
            if (noOverlapParticle(potential.getX(), potential.getY(), potential.getRadius())) {
                particles.add(potential);
            }
            tries++;
        }
        System.out.println("Particles: " + particles.size());
        System.out.println("Done.]");
    }

    public boolean noOverlapParticle(Double x, Double y, Double radio){
        if (particles.size() == 0) return true;
        for (Particle particle : particles){
                if ( (Math.pow(particle.getX() - x, 2) + Math.pow(particle.getY() - y, 2)) <= Math.pow(particle.getRadius() + radio + interactionRadio, 2)){
                    return false;
            }
        }
        return true;
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public double getKn() {
        return Kn;
    }

    public double getKt() {
        return Kt;
    }

    public double getY() {
        return y;
    }

    public double getMass() {
        return mass;
    }

    public double getEndTime() {
        return endTime;
    }

    public double getL() {
        return L;
    }

    public double getW() {
        return W;
    }

    public double getD() {
        return D;
    }

    public double getCellSideLength() {
        return cellSideLength;
    }

    public double getInteractionRadio() {
        return interactionRadio;
    }

    public double getMaxRadio() {
        return maxRadio;
    }
}
