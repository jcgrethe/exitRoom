package ar.edu.itba.ss.models;

import ar.edu.itba.ss.io.Output;
import javafx.util.Pair;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static javafx.application.Platform.exit;

public class Grid {
    private Cell[][] cells;
    private HashSet<Pair<Integer, Integer>> usedCells;
    private int HsideCellsQuantity;
    private int VsideCellsQuantity;
    private double sideCellLength;
    private double sideLength;

    public Grid(double sideCellLength, double W, double H) {
        this.sideCellLength = sideCellLength;
        this.HsideCellsQuantity = (int) Math.ceil(H / sideCellLength);
        this.VsideCellsQuantity = (int) Math.ceil(H / sideCellLength);
        this.cells = new Cell[VsideCellsQuantity][HsideCellsQuantity];
        for (int i = 0 ; i < VsideCellsQuantity ; i++)
            for (int j = 0 ; j < HsideCellsQuantity ; j++)
                this.cells[i][j] = new Cell();
        this.sideLength = sideLength;
    }

    public Cell getCell(int x, int y){
        return cells[x][y];
    }

    public void clean(){
        Arrays.stream(cells).parallel().forEach(cells1 -> Arrays.stream(cells1).parallel().forEach(cell -> cell.removeAll())
        );
    }

    public Cell getSideCell(int x, int y){
        return cells[Math.floorMod(x, VsideCellsQuantity)][Math.floorMod(y, HsideCellsQuantity)];
        }


    public void setCell(int x, int y, Cell cell){
        cells[x][y] = cell;
    }

    public int getHSideCellsQuantity() {
        return HsideCellsQuantity;
    }
    public int getVSideCellsQuantity() {
        return VsideCellsQuantity;
    }

    public double getSideLength() {
        return sideLength;
    }

    public void setParticles(List<Particle> particles){
        usedCells = new HashSet<>();
        Double cellSideLength = this.sideCellLength;
        for (Particle particle : particles){
            int row = (int)Math.floor(particle.getY() / VsideCellsQuantity); // Cast truncates
            int column = (int)Math.floor(particle.getX() / HsideCellsQuantity); // Cast truncates
            try {
                if (row >= 0 && row < VsideCellsQuantity) {
                    cells[row][column].addParticle(particle);
                    usedCells.add(new Pair(row, column));
                }
            } catch (Exception e){
                try {
                    Output.printToFile(particles);
                }catch (IOException ex){
                    System.out.println(ex.getMessage());
                }
                System.out.println("Wrong dt. Particle with id:" + particle.getId());
                System.out.println(row + " " + column);
                System.out.println(particle.getY() + " " + particle.getX());
                System.exit(0);
            }
        }
    }

    public HashSet<Pair<Integer, Integer>> getUsedCells() {
        return usedCells;
    }
}
