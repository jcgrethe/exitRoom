package ar.edu.itba.ss.models;

public class Vector2D {
    private Double x;
    private Double y;

    public Vector2D(){
        this.x = 0.0;
        this.y = 0.0;
    }

    public Vector2D(Double x, Double y) {
        this.x = x;
        this.y = y;
    }
    public Vector2D(Vector2D base){
        this.x = base.x;
        this.y = base.y;
    }


    public Vector2D add(Double magnitude){
        return new Vector2D(this.x + magnitude, this.y + magnitude);
    }
    public Vector2D add(Double magnitudeX, Double magnitudeY){
        return new Vector2D(this.x + magnitudeX, this.y + magnitudeY);
    }
    public Vector2D subtract(Double magnitude){
        return new Vector2D(this.x - magnitude, this.y - magnitude);
    }

    public Vector2D multiply(Double magnitude){
        return new Vector2D(this.x * magnitude, this.y * magnitude);
    }
    public double dotMultiply(Vector2D v){
        return x*v.getY()-y*getX();
    }
    public Vector2D divide(Double magnitude){
        return new Vector2D(this.x / magnitude, this.y / magnitude);
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public Vector2D add(Vector2D vector2D){
        return new Vector2D(this.x + vector2D.x, this.y + vector2D.y);
    }
    public Vector2D subtract(Vector2D vector2D){
        return new Vector2D(this.x - vector2D.x, this.y - vector2D.y);
    }

    public double getModule(){
        return Math.sqrt(Math.pow(this.x,2d) + Math.pow(this.y, 2d));
    }

    public Vector2D distance(Vector2D v){
        return new Vector2D(Math.abs(this.x - v.getX()), Math.abs(this.y - v.getY()));
    }
}
