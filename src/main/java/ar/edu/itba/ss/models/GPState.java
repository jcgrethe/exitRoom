package ar.edu.itba.ss.models;

public class GPState{
    private Vector2D r;
    private Vector2D r1;
    private Vector2D r2;
    private Vector2D r3;
    private Vector2D r4;
    private Vector2D r5;

    public GPState(Vector2D r, Vector2D r1, Vector2D r2, Vector2D r3, Vector2D r4, Vector2D r5) {
        this.r = r;
        this.r1 = r1;
        this.r2 = r2;
        this.r3 = r3;
        this.r4 = r4;
        this.r5 = r5;
    }

    public GPState(){
        this.r = new Vector2D();
        this.r1 = new Vector2D();
        this.r2 = new Vector2D();
        this.r3 = new Vector2D();
        this.r4 = new Vector2D();
        this.r5 = new Vector2D();
    }

    public GPState(Double x, Double y, Double vx, Double vy){
        this.r = new Vector2D(x,y);
        this.r1 = new Vector2D(vx,vy);
        this.r2 = new Vector2D();
        this.r3 = new Vector2D();
        this.r4 = new Vector2D();
        this.r5 = new Vector2D();
    }


    public Vector2D getR() {
        return r;
    }

    public Vector2D getR1() {
        return r1;
    }

    public Vector2D getR2() {
        return r2;
    }

    public Vector2D getR3() {
        return r3;
    }

    public Vector2D getR4() {
        return r4;
    }

    public Vector2D getR5() {
        return r5;
    }
}
