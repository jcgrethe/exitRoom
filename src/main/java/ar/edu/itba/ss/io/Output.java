package ar.edu.itba.ss.io;
import ar.edu.itba.ss.models.Particle;

import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Output {
    private final static String FILENAME = "output.txt";
    private final static String SIMULATION_FILENAME = "positions.xyz";
    private final static String STATISTICS_FILENAME = "statistics_energy_per_unit_of_time.csv";
    private final static String OSCILLATION_RESULTS_FILENAME = "oscillation_results.csv";
    private final static String VELOCITY_NAME = "velocities.csv";
    private final static String PARTICLE_FILENAME = "statistics_particle_per_unit_of_time.csv";

    private static BufferedWriter simulationBufferedWriter;
    private static BufferedWriter energyBufferedWriter;
    private static BufferedWriter leftbuffer;
    private static BufferedWriter velocityBufferedWriter;
    private static BufferedWriter caudalBufferedWriter;

    public static void printOscillationsResults(double[][] analitycPositions,
                                                double[][] beemanPositions,
                                                double[][] GearPredictorPositions,
                                                double[][] verletPositions,
                                                double[] beenmanError,
                                                double[] gearPredictorError,
                                                double[] verletError,
                                                List<Double> dts
                                                ){
        System.out.println("Printing...");
        try{
            FileWriter ofw = new FileWriter(OSCILLATION_RESULTS_FILENAME);
            BufferedWriter obufferedw = new BufferedWriter(ofw);
            obufferedw.write(
                    "ECM\\Algoritmo,Beeman,GP,VelocityVerlet"
            );
            obufferedw.newLine();
            for (int i=0 ; i < dts.size() ; i++) {
                obufferedw.write(
                        dts.get(i) + "," +
                        beenmanError[i] + "," +
                        gearPredictorError[i] + "," +
                        verletError[i]
                        );
                obufferedw.newLine();
            }
            for (int i=0 ; i < dts.size() ; i++){
                try {
                    obufferedw.write(
                            dts.get(i).toString()
                    );
                    obufferedw.newLine();
                    obufferedw.write(
                            "Analytic,Beeman,Gear Predictor,VelocityVerlet"
                    );
                    obufferedw.newLine();
                    for (int p=0 ; p < analitycPositions[i].length ; p++){
//                        if (p % 100 == 0){
                            obufferedw.write(
                                    analitycPositions[i][p] + ","+
                                            beemanPositions[i][p] + "," +
                                            GearPredictorPositions[i][p] + ","+
                                            verletPositions[i][p]
                            );
                            obufferedw.newLine();
//                        }
                    }
                }catch (IOException e) {
                }
            }
            obufferedw.flush();
            obufferedw.close();
            ofw.close();

        }catch (IOException e) {
        }
    }




    public static void generateXYZFile(){
        try{
            FileWriter fileWriter = new FileWriter(SIMULATION_FILENAME);
            simulationBufferedWriter = new BufferedWriter(fileWriter);
        }catch(IOException e){
            System.out.println(e);
        }
    }

    public static void printVelocities(List<Particle> particles, Double time) throws IOException {
        particles.stream().forEach(particle -> {
            try{
                velocityBufferedWriter.write(
                        time.toString() + "," + particle.getCurrentState().getvModule()
                );
                velocityBufferedWriter.newLine();
            }catch (IOException e){
                System.out.println(e);
            }
        });
        velocityBufferedWriter.flush();
    }

    public static void printToFile(List<Particle> particles) throws IOException {
        simulationBufferedWriter.write(String.valueOf(particles.size()));
        simulationBufferedWriter.newLine();
        simulationBufferedWriter.newLine();
        particles.stream().forEach(particle -> {
            try{
                simulationBufferedWriter.write(
                        particle.getId()
                                + " " + particle.getX()
                                + " " + particle.getY()
                                + " " + particle.getvX()
                                + " " + particle.getvY()
                                + " " + particle.getRadius()
                                + " " + particle.getMass()
                                + " " + particle.getPressure()
                );
                simulationBufferedWriter.newLine();
            }catch (IOException e){
                System.out.println(e);
            }
        });
        simulationBufferedWriter.flush();
    }

    public static void generateVelocityStatistics(){
        try{
            FileWriter fileWriter = new FileWriter(VELOCITY_NAME);
            velocityBufferedWriter = new BufferedWriter(fileWriter);
            velocityBufferedWriter.write("time,velocity");
            velocityBufferedWriter.newLine();
            velocityBufferedWriter.flush();

            FileWriter fw = new FileWriter(STATISTICS_FILENAME);
            energyBufferedWriter = new BufferedWriter(fw);
            energyBufferedWriter.write("time,energyk");
            energyBufferedWriter.newLine();
            energyBufferedWriter.flush();

            FileWriter fw2 = new FileWriter("caudal.csv");
            caudalBufferedWriter = new BufferedWriter(fw2);
            caudalBufferedWriter.write("caudal,time");
            caudalBufferedWriter.newLine();
            caudalBufferedWriter.flush();
        }catch(IOException e){
            System.out.println(e);
        }
    }

    public static void printCaudal(int caudal,double time) throws IOException {
        caudalBufferedWriter.write(caudal+","+time);
        caudalBufferedWriter.newLine();
        caudalBufferedWriter.flush();
    }


    public static void printEnergy(List<Particle> particles,double time) throws IOException {
        double kineticE = 0.0;
        for(Particle particle: particles)
            kineticE+= kinetic(particle);

        energyBufferedWriter.write(time+","+kineticE);
        energyBufferedWriter.newLine();
        energyBufferedWriter.flush();
    }

    public static double kinetic(Particle p){
        return 0.5 * p.getMass() * Math.pow(p.getCurrentState().getvModule(),2);
    }


    public static double potencial(Particle p, List<Particle> neighbours, double rm, double e){
        double acum= 0.0;
        for(Particle n: neighbours){
            double distance = p.point2D().distance(n.point2D());
            double aux = e * ( Math.pow(rm/distance,12) - 2 * Math.pow(rm/distance,6) );
            acum+= aux;
        }
        return acum;
    }

    private static double getDistances(Particle p1, Particle p2){
        double y = Math.abs(p2.getY() - p1.getY());
        double x = Math.abs(p2.getX() - p1.getX());
        return Math.hypot(y, x);
    }

    public static double energyError(double analitic, double predicted){
        return Math.pow(analitic - predicted, 2);
    }

    public static void generateParticleStadistics(){
        try{
            FileWriter fileWriter = new FileWriter(PARTICLE_FILENAME);
            leftbuffer = new BufferedWriter(fileWriter);
            leftbuffer.write("time,particle_in_left");
            leftbuffer.newLine();
            leftbuffer.flush();
        }catch(IOException e){
            System.out.println(e);
        }
    }

    public static int printParticle(List<Particle> particles, double time) throws IOException {
        int acum=0;
        for(Particle particle: particles){
            if(particle.getX()<200)
                acum++;
        }
        leftbuffer.write(time + "," + acum);
        leftbuffer.newLine();
        leftbuffer.flush();
        return acum;
    }



}
