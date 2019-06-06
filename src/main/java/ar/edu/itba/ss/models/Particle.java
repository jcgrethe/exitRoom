package ar.edu.itba.ss.models;

import ar.edu.itba.ss.io.Input;

import java.awt.geom.Point2D;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class Particle {
    private static Long serial_id = Long.valueOf(0);

    private final Long id;
    private final double radius;
    private final double mass;
    private State previousState;
    private State currentState;
    private State futureState;
    double desiredVelocity;
    boolean marked = false;

    public Particle(double radius, double mass) {
        this.id = serial_id++;
        this.radius = radius;
        this.mass = mass;
        this.previousState = new State();
        this.currentState = new State();
        this.futureState = null;
    }

    public Particle(double radius, double mass, State previousState, State currentState) {
        this.id = serial_id++;
        this.radius = radius;
        this.mass = mass;
        this.previousState = previousState;
        this.currentState = currentState;
        this.futureState = null;
    }

    public Particle(double radius, double mass, State currentState) {
        this.id = serial_id++;
        this.radius = radius;
        this.mass = mass;
        this.previousState =  new State(0, 0, 0, 0, 0, 0,0);
        this.currentState = currentState;
        this.futureState = null;
    }

    /**
     * Big particles without radio and with infinite mass for simulating walls.
     */
    public Particle(long id,double x, double y) {
        this.id = id;
        currentState = new State(x, y, 0, 0, 0, 0, 0);
        previousState = new State(0, 0, 0, 0, 0, 0, 0);
        this.radius = 0;
        this.mass = Double.POSITIVE_INFINITY;
    }

    public Particle(double radius, double mass, double x, double y, double vx, double vy,double dt,double desiredVelocity) {
        this.id = serial_id++;
        this.radius = radius;
        this.mass = mass;
        currentState = new State(x, y, vx, vy, 0, 0, 0);
        double prevX = x - vx * dt;
        double prevY = y - vy * dt;
        this.desiredVelocity = desiredVelocity;
        previousState = new State(prevX, prevY, vx, vy, 0, 0, 0);
    }

    public Particle(double radius, double mass, double x, double y, double vx, double vy){
        this.id = serial_id++;
        this.radius = radius;
        this.mass = mass;
        currentState = new State(x, y, vx, vy, 0, 0, 0);
        previousState = new State(0, 0, 0, 0, 0, 0, 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Particle particle = (Particle) o;

        return id.equals(particle.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public Long getId() {
        return id;
    }

    public double getRadius() {
        return radius;
    }

    public double getMass() {
        return mass;
    }

    public State getPreviousState() {
        return previousState;
    }

    public State getCurrentState() {
        return currentState;
    }

    public void setFutureState(State futureState){
        this.futureState = futureState;
    }

    public void updateState(){
        this.previousState = this.currentState;
        this.currentState = this.futureState;
        this.futureState = null;
    }

    public Double getX(){
        return currentState.getX();
    }

    public Double getY(){
        return currentState.getY();
    }
    public Double getPressure(){
        return currentState.getPressure();
    }
    public void setPressure(double pressure){
        currentState.setPressure(pressure);
    }

    public Double getvX(){
        return currentState.getvX();
    }

    public Double getvY(){
        return currentState.getvY();
    }

    public Double getaX(){
        return currentState.getaX();
    }

    public Double getaY(){
        return currentState.getaY();
    }

    public Vector2D getPosition(){ return new Vector2D(getX(),getY());}
    public Vector2D getPreviousPosition(){ return new Vector2D(getPreviousState().getX(),getPreviousState().getY());}
    public Vector2D getVelocity(){ return new Vector2D(getvX(),getvY());}
    public Vector2D getPreviousVelocity(){ return new Vector2D(getPreviousState().getvX(),getPreviousState().getvY());}
    public Vector2D getAcceleration(){ return new Vector2D(getaX(),getaY());}
    public Vector2D getPreviousAcceleration(){ return new Vector2D(getPreviousState().getaX(),getPreviousState().getaY());}

    public Optional<GPState> getGPState(){
        return currentState.getGPState();
    }

    public void setX(double x ){
        this.currentState.setX(x);
    }

    public void setY(double y){
        this.currentState.setY(y);
    }

    public javafx.geometry.Point2D point2D(){
        return new javafx.geometry.Point2D(getX(),getY());
    }

    public void initializeGPState(Double x, Double y, Double vx, Double vy){
        currentState.setNewGPState(x,y, vx, vy);
    }

    public double getDistance(Particle p){
        return this.getPosition().distance(p.getPosition()).getModule();
    }

    public void reset(Input input) {
        double x;
        double y;
        do{
            x = ThreadLocalRandom.current().nextDouble(input.getMaxRadio(),input.getW()-input.getMaxRadio());
            y = ThreadLocalRandom.current().nextDouble( input.getL()/3 + input.getMaxRadio(),input.getL()- input.getMaxRadio());
        }while (!input.noOverlapParticle(x,y,radius));  //NO NEED TO CHECK WALLS?
        State newState = new State(x,y,0,0);
        currentState = previousState = newState;
    }

    public double getDesiredVelocity() {
        return desiredVelocity;
    }

    public boolean isMarked() {
        return marked;
    }

    public void mark() {
        marked= true;
    }

}
