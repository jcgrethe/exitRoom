package ar.edu.itba.ss;

import ar.edu.itba.ss.GranularMedia.GranularMediaForce;
import ar.edu.itba.ss.Integrators.*;
import ar.edu.itba.ss.io.Input;
import ar.edu.itba.ss.io.Output;
import ar.edu.itba.ss.models.Grid;
import ar.edu.itba.ss.models.Particle;
import ar.edu.itba.ss.models.Wall;

import java.io.IOException;
import java.util.*;
import org.apache.commons.cli.*;


public class Simulation
{

    static long PARTICLES = 200;
    static int caudal = 0;
    private static double interactionRadio = 0d;


    public static void main( String[] args ) {
        Output.generateVelocityStatistics();
        simulate(args);
        // Output

    }

    public static void simulate(String[] args){
        // Initial conditions
        //Double simulationDT = 0.1*Math.sqrt(input.getMass()/input.getKn());   //Default ; TODO: Check if there is a better one
        double simulationDT = 1E-4;
        CommandLine cmd = getOptions(args);

        if(cmd.getOptionValue("n") != null){
            PARTICLES = Long.parseLong(cmd.getOptionValue("n"));
        }

        double v = 5.0;

        if(cmd.getOptionValue("v") != null){
            v = Double.parseDouble(cmd.getOptionValue("v"));
        }


        Input input = new Input(PARTICLES, simulationDT, -v);
        interactionRadio = input.getInteractionRadio();
        Integer printDT = 1000;


        Integer iteration = 0;
        System.out.println("DT: "+ simulationDT + " | Print DT: " + printDT);
        Integrator integrator = new VelocityVerlet(simulationDT,
                new GranularMediaForce(input.getKn(), input.getKt(), input.getW(), input.getL()),
                input.getW(), input.getL(), input.getD()
        );
        // Can use other integrator.
        List<Particle> particles = input.getParticles();
        Output.generateXYZFile();


        List<Particle> toRemove = new LinkedList<>();
        //Simulation
        Grid grid = new Grid(input.getCellSideLength(),input.getW(), input.getL());
        for (double time = 0d ; particles.size()>1 ; time += simulationDT, iteration++){
            grid.setParticles(input.getParticles());
//            integrator.moveParticle();
            Map<Particle, List<Particle>> neighbours = NeighborDetection.getNeighbours(
                    grid, grid.getUsedCells(),
                    input.getInteractionRadio(), false
            );
            particles.stream().parallel().forEach( particle -> {
                integrator.moveParticle(
                        particle, simulationDT,
                        neighbours.getOrDefault(particle,new LinkedList<>()),
                        getWallsCollisions(particle, input.getW(), input.getL(), input.getD())
                );
            });

            final double auxtime=time;

            particles.stream().parallel().forEach(particle -> {
                particle.updateState();
                if(particle.getY()<3 && !particle.isMarked()){
                    try {
                        updateCaudal(input,auxtime);
                        particle.mark();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (particle.getY() < 1){
                    toRemove.add(particle);
                }
            });

            for(Particle p: toRemove){
                particles.remove(p);
            }

            if (iteration % printDT == 0){ //TODO CHECK!!
                //Print
                try {
                    System.out.println(time);
                    Output.printToFile(particles);
                    Output.printEnergy(input.getParticles(),time);
                }catch (IOException e){
                    System.out.println(e.getMessage());
                }
            }
            grid.clean();
        }
        //end simulation
    }



    private static void updateCaudal(Input input, double time) throws IOException {
        caudal++;
        Output.printCaudal(caudal,time);
    }


    public static List<Wall> getWallsCollisions(Particle p, Double boxWidth, Double boxHeight, Double D){
        List<Wall> walls = new LinkedList<>();
        if (p.getX() < (p.getRadius() + interactionRadio))
            walls.add(new Wall(Wall.typeOfWall.LEFT));
        if (boxWidth - p.getX() < (p.getRadius() + interactionRadio))
            walls.add(new Wall(Wall.typeOfWall.RIGHT));
        if ((p.getY()-3) < (p.getRadius() + interactionRadio) && p.getY()>3)
            if(p.getX() < boxWidth / 2 - D / 2  || p.getX() > boxWidth / 2 + D / 2 ) // apertura
                walls.add(new Wall(Wall.typeOfWall.BOTTOM));
        return walls;
    }

    private static CommandLine getOptions(String[] args){


        Options options = new Options();

        Option v = new Option("v", "v", true, "velocity");
        v.setRequired(false);
        options.addOption(v);


        Option p = new Option("n", "n", true, "n");
        p.setRequired(false);
        options.addOption(p);


        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd=null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }
        return cmd;
    }
}
