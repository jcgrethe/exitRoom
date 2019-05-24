package ar.edu.itba.ss.models;
import java.util.Optional;

public class State {

    /**
     * Position
     */
    private double x;
    private double y;

    /**
     * Velocity
     */
    private double vX;
    private double vY;
    private double vAngle;
    private double vModule;

    /**
     * Acceleration
     */
    private double aX;
    private double aY;
    private double aAngle;
    private double aModule;

    /**
     * Optional State for Gear Predictor
     */
    private Optional<GPState> gpState;

    private double pressure;    //TODO: Add to constructors where need to


    public State(double x, double y, double vX, double vY) {
        this.x = x;
        this.y = y;
        this.vX = vX;
        this.vY = vY;
        this.vModule = Math.hypot(vX, vY);
        this.vAngle = Math.atan(vY/vX);
        this.aX = 0.0;
        this.aY = 0.0;
        this.aModule = 0.0;
        this.aAngle = 0.0;
        this.gpState = Optional.empty();
    }

    public State(double x, double y){
        this.x = x;
        this.y = y;
        this.vX = 0.0;
        this.vY = 0.0;
        this.aX = 0.0;
        this.aY = 0.0;
        this.vModule = 0.0;
        this.vAngle = 0.0;
        this.aModule = 0.0;
        this.aAngle = 0.0;
        this.gpState = Optional.empty();
    }

    public State(double x, double y, double vX, double vY, double aX, double aY, double pressure) {
        this.x = x;
        this.y = y;
        this.vX = vX;
        this.vY = vY;
        this.aX = aX;
        this.aY = aY;
        this.vModule = Math.hypot(vX, vY);
        this.vAngle = Math.atan(vY/vX);
        this.aModule = Math.hypot(aX, aY);
        this.aAngle = Math.atan(aY/aX);
        this.gpState = Optional.empty();
        this.pressure = pressure;
    }

    public State(){
        this.x = 0.0;
        this.y = 0.0;
        this.vX = 0.0;
        this.vY = 0.0;
        this.aX = 0.0;
        this.aY = 0.0;
        this.vModule = 0.0;
        this.vAngle = 0.0;
        this.aModule = 0.0;
        this.aAngle = 0.0;
        this.gpState = Optional.empty();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getvX() {
        return vX;
    }

    public double getvY() {
        return vY;
    }

    public double getvAngle() {
        return vAngle;
    }

    public double getvModule() {
        return vModule;
    }

    public double getaX() {
        return aX;
    }

    public double getaY() {
        return aY;
    }

    public double getaAngle() {
        return aAngle;
    }

    public double getaModule() {
        return aModule;
    }

    public void changeGPState(GPState gpState){
        this.gpState = Optional.of(gpState);
    }

    public Optional<GPState> getGPState(){
        return gpState;
    }
    
    public void setNewGPState(Double x, Double y, Double vx, Double vy){
        this.gpState = Optional.of(new GPState(x,y, vx, vy));
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Optional<GPState> getGpState() {
        return gpState;
    }

    public double getPressure() {
        return pressure;
    }
    public void setPressure(double pressure) {
        this.pressure = pressure;
    }
}
