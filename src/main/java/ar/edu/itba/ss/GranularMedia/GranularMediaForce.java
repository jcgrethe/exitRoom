package ar.edu.itba.ss.GranularMedia;

import ar.edu.itba.ss.io.Input;
import ar.edu.itba.ss.models.ForceFunction;
import ar.edu.itba.ss.models.Particle;
import ar.edu.itba.ss.models.Vector2D;
import ar.edu.itba.ss.models.Wall;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Optional;

public class GranularMediaForce  implements ForceFunction {
    private double Kn;
    private double Kt;
    private double boxWidth;
    private double boxHeigth;
    Vector2D exitTarget;
    double A=2000;
    double B =0.08;

    public GranularMediaForce(double kn, double kt, double boxWidth, double boxHeigth) {
        Kn = kn;
        Kt = kt;
        this.boxWidth = boxWidth;
        this.boxHeigth = boxHeigth;
        this.exitTarget = new Vector2D(boxWidth/2, 3.0);
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
            overlapSize = overlapSize(particle, neighbour); //mirar esto
            if (overlapSize > 0) {
                Vector2D normalVector = new Vector2D(yDistanceFraction, -xDistanceFraction); //TODO Check '-'
                double relativeVelocity = getRelativeVelocity(particle, neighbour, normalVector);
                double normalForceValue = -Kn * overlapSize;
                double tangencialForceValue = -Kt * overlapSize * relativeVelocity;
                forceX += normalForceValue * xDistanceFraction + tangencialForceValue * (-yDistanceFraction);
                forceY += normalForceValue * yDistanceFraction + tangencialForceValue * xDistanceFraction;
                pressure += normalForceValue;
            /*            if(Math.abs(forceX)>15 || Math.abs(forceY)>15)
                System.out.println("error1");*/
            }
            //social
            double socialForceValue = A * Math.exp(overlapSize / B);
            forceX += (-1d)*socialForceValue * xDistanceFraction;
            forceY += (-1d)*socialForceValue * yDistanceFraction;
        }
        particle.setPressure(Math.abs(pressure)/(2*Math.PI*particle.getRadius()));
        force = new Vector2D(forceX,forceY);


        // Force from walls
        for (Wall wall : walls){
            overlapSize = overlapSize(particle, wall);
            if (overlapSize > 0){
                double relativeVelocity = getRelativeVelocity(particle, wall);
                Vector2D forceNormalAndTan = getNormalAndTangencialVector(overlapSize, relativeVelocity);
                force = addForceFromWall(force, wall, forceNormalAndTan, overlapSize);
            }
            force = addSocialForceFromWall(force, wall, overlapSize, boxWidth);
        }

/*        if(Math.abs(force.getX())>15 || Math.abs(force.getY())>15)
            System.out.println("error2");*/

        // Driving Force
        Vector2D directionVersor;
        Vector2D drivingForceValue;

        if(particle.getY() < 3)
            directionVersor = particle.getPosition().subtract(particle.getDesiredTarget());
        else
            directionVersor = particle.getPosition().subtract(exitTarget);
        directionVersor = directionVersor.multiply(1/directionVersor.getModule());

        drivingForceValue = directionVersor.multiply(particle.getDesiredVelocity()).subtract(particle.getVelocity());
        drivingForceValue = drivingForceValue.multiply(particle.getMass()/0.5);

        return new Vector2D(force.getX()+drivingForceValue.getX(),force.getY()+drivingForceValue.getY());

//        return force;
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
        return overlapSize;
    }

    private double overlapSize(Particle p, Wall wall){
        switch (wall.getTypeOfWall()){
//            case TOP:
//                return p.getRadius() - Math.abs(p.getY());
            case RIGHT:
                return p.getRadius() - boxWidth + p.getX();
            case BOTTOM:
                return p.getRadius() - p.getY() + 3;
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

    private Vector2D addForceFromWall(Vector2D force, Wall wall, Vector2D normalAndTan, double overlapSize){
        switch (wall.getTypeOfWall()){
            case RIGHT: // normal [1,0] ; tan [0,-1]
                return force.add(
                    normalAndTan.getX(), //+ socialForceValue,
                    -normalAndTan.getY()
                );
            case BOTTOM: // normal [0,-1] ; tan [-1,0]
                return force.add(
                    -normalAndTan.getY(),
                    -normalAndTan.getX() // - socialForceValue
                );
            case LEFT: // normal [-1,0] ; tan [0,1]
                return force.add(
                    -normalAndTan.getX(), // - socialForceValue,
                    normalAndTan.getY()
                );
        }
        return force;
    }

    private Vector2D addSocialForceFromWall(Vector2D force, Wall wall, double overlapSize, double boxWidth){
        double socialForceValue = A * Math.exp( overlapSize / B);
        switch (wall.getTypeOfWall()){
            case RIGHT:
                return force.add(
                        -1d*socialForceValue,
                        0d
                );
            case BOTTOM:
                return force.add(
                        0d,
                        1d*socialForceValue
                );
            case LEFT:
                return force.add(
                        1d*socialForceValue,
                        0d
                );
        }
        return force;
    }
}
