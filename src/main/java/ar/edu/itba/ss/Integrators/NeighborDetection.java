package ar.edu.itba.ss.Integrators;
import ar.edu.itba.ss.models.Grid;
import javafx.util.Pair;
import ar.edu.itba.ss.models.Particle;
import ar.edu.itba.ss.models.Cell;

import java.util.*;
import java.util.stream.Collectors;

public class NeighborDetection {

    /**
     * Returns a map with the neighbours for each particle using the "Cell Index Method".
     *
     * @param grid                  The current grid with all the particles loaded.
     * @param usedCells             A set of pairs of coordenates of the used cells (for optimization)
     * @param interactionRadio      The max distance between two particles to be neighbours.
     * @param contornCondition      True if the contorn condition is on.
     * @return  A Map with a {@link List} of {@link Particle}s for each Particle.
     */
    public static Map<Particle, List<Particle>> getNeighbours(Grid grid, HashSet<Pair<Integer, Integer>> usedCells, Double interactionRadio, Boolean contornCondition){
        Map<Particle, List<Particle>> result = new HashMap<>();
        // Foreach cell with particles
        usedCells.parallelStream().parallel().forEach(pair -> {
            int i = pair.getKey(), j = pair.getValue();
            for (Particle current : grid.getCell(i, j).getParticles()){
                List<Particle> currentNeighbours = new ArrayList<>();
                List<Particle> sameCell = new ArrayList<>();

                //get the neighbor added before or a new linked list
                final List<Particle> neighbours = result.getOrDefault(current, new LinkedList<>());

                if (!contornCondition) {
                    //Check the four neighbours taking advantage of the simetry.
                    if (i != 0)
                        currentNeighbours.addAll(getNeighborParticles(current,
                                grid.getCell(i - 1, j), interactionRadio, contornCondition, grid.getSideLength()));

                    if (i != 0 && j != grid.getHSideCellsQuantity() - 1)
                        currentNeighbours.addAll(getNeighborParticles(current,
                                grid.getCell(i - 1, j + 1), interactionRadio, contornCondition, grid.getSideLength()));

                    if (j != grid.getHSideCellsQuantity() - 1)
                        currentNeighbours.addAll(getNeighborParticles(current,
                                grid.getCell(i, j + 1), interactionRadio, contornCondition, grid.getSideLength()));

                    if (j != grid.getHSideCellsQuantity() - 1 && i != grid.getVSideCellsQuantity() - 1)
                        currentNeighbours.addAll(getNeighborParticles(current,
                                grid.getCell(i + 1, j + 1), interactionRadio, contornCondition, grid.getSideLength()));

                    }else {
                        currentNeighbours.addAll(getNeighborParticles(current,
                                grid.getSideCell((i - 1)  , j), interactionRadio, contornCondition, grid.getSideLength()));

                        currentNeighbours.addAll(getNeighborParticles(current,
                                grid.getSideCell(i - 1, j + 1), interactionRadio, contornCondition, grid.getSideLength()));

                        currentNeighbours.addAll(getNeighborParticles(current,
                                grid.getSideCell(i, j + 1), interactionRadio, contornCondition, grid.getSideLength()));

                        currentNeighbours.addAll(getNeighborParticles(current,
                                grid.getSideCell(i + 1, j + 1), interactionRadio, contornCondition, grid.getSideLength()));
                }

                //check same cell
                sameCell.addAll(getNeighborParticles(current,
                        grid.getCell(i, j), interactionRadio, contornCondition, grid.getSideLength()));

                //add all to the neighbours
                neighbours.addAll(currentNeighbours);
                neighbours.addAll(sameCell);

                //for each neighbours add current to the relation
                for (Particle newRelation : currentNeighbours) {
                    final List<Particle> anotherNeighbours = result.getOrDefault(newRelation, new LinkedList<>());
                    anotherNeighbours.add(current);
                    result.put(newRelation, anotherNeighbours);
                }

                result.put(current, neighbours);
            }
        });
        return result;
    }


    /**
     * This function returns a list of the particles that are near than the interaction radio from the current Particle.
     *
     * @param current The particle looking for neighbours
     * @param cell    The cell under lookup.
     * @param interactionRadio The max length of the distance from the current particle.
     * @return
     */
    private static List<Particle> getNeighborParticles(Particle current, Cell cell, Double interactionRadio, boolean contorn, double gridSize){
        return cell.getParticles().stream()
                .parallel()
                .filter(another -> (getDistance(current, another, contorn, gridSize)) <= interactionRadio)
                .filter(another -> !current.equals(another))
                .collect(Collectors.toList());
    }

    private static Double getDistance(Particle p1, Particle p2, boolean contorn, double size){
        double y = Math.abs(p2.getY() - p1.getY());
        double x = Math.abs(p2.getX() - p1.getX());
        double h = Math.hypot(y, x);
        h = h - p1.getRadius() - p2.getRadius();
        if (contorn){
            double xc = Math.abs(p1.getX() - p2.getX());
            xc = Math.min(xc, size - xc);
            double yc = (double)size - Math.abs(p1.getY() - p2.getY());
            yc = Math.min(yc, size - yc);
            return Math.min(h, Math.hypot(xc, yc));
        }
        return h;
    }

    public static Double getForce(Particle particle, List<Particle> neighbours){
        return null;
    }
}
