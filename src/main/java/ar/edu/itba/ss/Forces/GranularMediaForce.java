package ar.edu.itba.ss.Forces;

import ar.edu.itba.ss.io.Input;
import ar.edu.itba.ss.models.ForceFunction;
import ar.edu.itba.ss.models.Particle;
import ar.edu.itba.ss.models.Vector2D;
import ar.edu.itba.ss.models.Wall;

import java.util.List;
import java.util.Optional;

public class GranularMediaForce  implements ForceFunction {
    private double Kn;
    private double Kt;
    private double boxWidth;
    private double boxHeigth;

    public GranularMediaForce(double kn, double kt, double boxWidth, double boxHeigth) {
        Kn = kn;
        Kt = kt;
        this.boxWidth = boxWidth;
        this.boxHeigth = boxHeigth;
    }

    @Override
    public Vector2D getForce(Particle particle, List<Particle> neighbours, List<Wall> walls) {
        Vector2D force;
        double xDistanceFraction, yDistanceFraction, distance, overlapSize;

        // Force from particles
        double forceX = 0;
        double forceY = 0;
        double pressure = 0;
        for (Particle neighbour : neighbours){
            distance = neighbour.getDistance(particle);
            xDistanceFraction = (neighbour.getX() - particle.getX())/distance;
            yDistanceFraction = (neighbour.getY() - particle.getY())/distance;

            Vector2D normalVector = new Vector2D(yDistanceFraction, -xDistanceFraction); //TODO Check '-'
            overlapSize = overlapSize(particle, neighbour); //mirar esto
            if(overlapSize < 0) continue; // Not colliding
            double relativeVelocity = getRelativeVelocity(particle, neighbour , normalVector);
            double normalForceValue = - Kn * overlapSize;
            double tangencialForceValue = - Kt * overlapSize * relativeVelocity;
            forceX += normalForceValue * xDistanceFraction + tangencialForceValue * (-yDistanceFraction);
            forceY += normalForceValue * yDistanceFraction + tangencialForceValue * xDistanceFraction;
            pressure += normalForceValue;
            /*            if(Math.abs(forceX)>15 || Math.abs(forceY)>15)
                System.out.println("error1");*/
        }
        particle.setPressure(Math.abs(pressure)/(2*Math.PI*particle.getRadius()));
        force = new Vector2D(forceX,forceY);


        // Force from walls
        for (Wall wall : walls){
            overlapSize = overlapSize(particle, wall);
            if (overlapSize > 0){
                double relativeVelocity = getRelativeVelocity(particle, wall);
                Vector2D forceNormalAndTan = getNormalAndTangencialVector(overlapSize, relativeVelocity);
                force = addForceFromWall(force, wall, forceNormalAndTan);
            }
        }

/*        if(Math.abs(force.getX())>15 || Math.abs(force.getY())>15)
            System.out.println("error2");*/

        // Force from gravity
        force = force.add(
                0d,
                -Input.getGravity()*particle.getMass()
        );

        return force;
    }

    private Vector2D getNormalAndTangencialVector(double overlapSize, double relativeVelocity){
        return new Vector2D(
                -Kn * overlapSize,
                -Kt * overlapSize * relativeVelocity
        );
    }

    private Vector2D getElasticForce(double overlapSize, Vector2D normalVector){
        return normalVector.multiply(-Kn).multiply(overlapSize);
    }
    private Vector2D getDampedForce(double overlapSize, Vector2D relativeVelocity, Vector2D normalVector){
        return normalVector.multiply(-Kt).multiply(overlapSize).multiply(relativeVelocity.dotMultiply(normalVector));
    }

    private static double overlapSize(Particle one, Particle another) {
        double overlapSize = one.getRadius() + another.getRadius() - one.getDistance(another);
        return (overlapSize < 0)? 0 : overlapSize;
    }

    private double overlapSize(Particle p, Wall wall){
        switch (wall.getTypeOfWall()){
//            case TOP:
//                return p.getRadius() - Math.abs(p.getY());
            case RIGHT:
                return p.getRadius() - boxWidth + p.getX();
            case BOTTOM:
                return p.getRadius() - p.getY();
            case LEFT:
                return p.getRadius() - p.getX();
        }
        return 0d;
    }

    private static double getRelativeVelocity(Particle one, Particle another, Vector2D tan) {
        Vector2D v = another.getVelocity().subtract(one.getVelocity());
        return v.getX() * tan.getX() + v.getY() * tan.getY();
    }

    private static Vector2D getRelativeVelocity(Particle one, Particle another) {
        return another.getVelocity().subtract(one.getVelocity());
    }

    private static double getRelativeVelocity(Particle p, Wall wall) {
        switch (wall.getTypeOfWall()){
            case TOP: // --->
                return p.getVelocity().getX();
            case RIGHT: // v
                return -p.getVelocity().getY();
            case BOTTOM: // <---
                return -p.getVelocity().getX();
            case LEFT: // ^
                return p.getVelocity().getY();
        }
        return 0d;  //Not should happen.
    }

    private Vector2D addForceFromWall(Vector2D force, Wall wall, Vector2D normalAndTan){
        switch (wall.getTypeOfWall()){
            case TOP: // normal [0,1] ; tan [1,0]
                return force.add(
                        normalAndTan.getY(),    // Only tan
                        normalAndTan.getX()     // Only normal
                );
            case RIGHT: // normal [1,0] ; tan [0,-1]
                return force.add(
                    normalAndTan.getX(),
                    -normalAndTan.getY()
                );
            case BOTTOM: // normal [0,-1] ; tan [-1,0]
                return force.add(
                    -normalAndTan.getY(),
                    -normalAndTan.getX()
                );
            case LEFT: // normal [-1,0] ; tan [0,1]
                return force.add(
                    -normalAndTan.getX(),
                    normalAndTan.getY()
                );
        }
        return force;
    }
}
