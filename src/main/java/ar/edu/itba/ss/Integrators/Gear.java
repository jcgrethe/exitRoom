package ar.edu.itba.ss.Integrators;

import ar.edu.itba.ss.models.Particle;

public class Gear {

    private final double firstFactor;
    private final double secondFactor;
    private final double thirdFactor;
    private final double fourthFactor;
    private final double fifthFactor;

    private final double correctFactor0;
    private final double correctFactor1;
    private final double correctFactor2;
    private final double correctFactor3;
    private final double correctFactor4;
    private final double correctFactor5;

    private final double ri;
    private final double r1i;
    private final double r2i;
    private final double time;
    private final double dt;
    private final double k;
    private final double y;
    private final double m;

    private static final Double A = 1.0;       // TODO: Put real value
    private static final Double K = 10000.0;   // In N/m
    private static final Double yi = 100.0;    // In kg/s
    private static final Double Tf = 5.0;     // In s
    private static final Double M = 70.0;     // In Kilogrames
    private static final Double initialX = 1.0;          // In m
    private static final Double initialV = -A*yi/(2*M);   // In m/s
    private static final Double initialA = (-K*initialX-yi*initialV)/M;   // In m/s
    private static Double endTime = 5.0; // In s


    public static void main(String[] args){
        Gear gear = new Gear(initialX, initialV, initialA, endTime, 0.0001, K, yi, M);
        double[] p = gear.getPositions();

        for (int i = 0 ; i < p.length ; i++){
            System.out.println(p[i]);
        }
    }

    public Gear(double r, double r1, double r2, double time, double dt, double k, double y, double m){
        this.ri = r;
        this.r1i = r1;
        this.r2i = r2;
        this.time = time;
        this.dt = dt;
        this.k = k;
        this.y = y;
        this.m = m;

        this.firstFactor = dt;
        this.secondFactor = (dt * dt) / 2d;
        this.thirdFactor = (dt * dt * dt) / 6d;
        this.fourthFactor = (dt * dt * dt * dt) / 24d;
        this.fifthFactor = (dt * dt * dt * dt * dt) / 120d;
        
        this.correctFactor0 = 3d / 16d;
        this.correctFactor1 = (251d / 360d) / dt;
        this.correctFactor2 = 1d * (2d / (dt * dt));
        this.correctFactor3 = (11d / 18d) * (6d / (dt * dt * dt));
        this.correctFactor4 = (1d / 6d) * (24d / (dt * dt * dt * dt));
        this.correctFactor5 = (1d / 60d) * (120d / (dt * dt * dt * dt * dt));
    }

    public double[] getPositions(){
        int steps = (int)(time/dt);
        double[] p = new double[steps];

        State current = new State(ri,r1i,r2i,0d,0d,0d);
        for (int s = 0; s < steps ; s++){
            State predicted = new State(
                get0Derivate(current),
                get1Derivate(current),
                get2Derivate(current),
                get3Derivate(current),
                get4Derivate(current),
                get5Derivate(current)
            );

            double force = -predicted.r0*k-y*predicted.r1;
            double acceleration = force/m;
            double deltaAcceleration = acceleration-predicted.r2;
            double deltaR2 = deltaAcceleration*secondFactor;

            State corrected = new State(
              predicted.r0 + deltaR2*correctFactor0,
              predicted.r1 + deltaR2*correctFactor1,
              predicted.r2 + deltaR2*correctFactor2,
              predicted.r3 + deltaR2*correctFactor3,
              predicted.r4 + deltaR2*correctFactor4,
              predicted.r5 + deltaR2*correctFactor5
            );

//            current = corrected;
            current = new State(
                    corrected.r0,
                    corrected.r1,
                    corrected.r2,
                    0.0,
                    0.0,
                    0.0
            );
            p[s] = corrected.r0;
        }

        return p;
    }

    double get0Derivate(State s){
        return s.r0 + s.r1*firstFactor + s.r2*secondFactor + s.r3*thirdFactor + s.r4*fourthFactor +s.r5*fifthFactor;
    }
    double get1Derivate(State s){
        return s.r1 + s.r2*firstFactor + s.r3*secondFactor + s.r4*thirdFactor + s.r5*fourthFactor;
    }
    double get2Derivate(State s){
        return s.r2 + s.r3*firstFactor + s.r4*secondFactor + s.r5*thirdFactor;
    }
    double get3Derivate(State s){
        return s.r3 + s.r4*firstFactor + s.r5*secondFactor;
    }
    double get4Derivate(State s){
        return s.r4 + s.r5*firstFactor;
    }
    double get5Derivate(State s){
        return s.r5;
    }

    class State{
        double r0;
        double r1;
        double r2;
        double r3;
        double r4;
        double r5;

        public State(double r0, double r1, double r2, double r3, double r4, double r5) {
            this.r0 = r0;
            this.r1 = r1;
            this.r2 = r2;
            this.r3 = r3;
            this.r4 = r4;
            this.r5 = r5;
        }
    }
}
