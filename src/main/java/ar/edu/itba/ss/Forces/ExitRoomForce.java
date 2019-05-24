package ar.edu.itba.ss.Forces;

import ar.edu.itba.ss.models.ForceFunction;
import ar.edu.itba.ss.models.Particle;
import ar.edu.itba.ss.models.Vector2D;
import ar.edu.itba.ss.models.Wall;

import java.util.List;

public class ExitRoomForce implements ForceFunction {
    private double Kn;
    private double Kt;
    private double boxWidth;
    private double boxHeigth;

    public ExitRoomForce(double kn, double kt, double boxWidth, double boxHeigth) {
        Kn = kn;
        Kt = kt;
        this.boxWidth = boxWidth;
        this.boxHeigth = boxHeigth;
    }

    @Override
    public Vector2D getForce(Particle particle, List<Particle> neighbours, List<Wall> walls) {

        // The combination of the three forces.
        return new Vector2D();
    }
}
