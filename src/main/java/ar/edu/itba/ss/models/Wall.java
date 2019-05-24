package ar.edu.itba.ss.models;

public class Wall {
    public enum typeOfWall{
        TOP(-1), BOTTOM(-2), RIGHT(-3), LEFT(-4), MIDDLE(-5), GAPSTART(-6), GAPEND(-7);

        private long val;

        typeOfWall(long i) {
            this.val= i;
        }

        public long getVal() {
            return val;
        }
    }



    private final typeOfWall typeOfWall;

    public Wall(typeOfWall type) {
        this.typeOfWall = type;
    }

    public Wall.typeOfWall getTypeOfWall() {
        return typeOfWall;
    }
}
