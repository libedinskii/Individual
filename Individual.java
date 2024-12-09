import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

class Individual {
    double x;
    double y;
    double fitness;

    public Individual(double x, double y) {
        this.x = x;
        this.y = y;
        this.fitness = evaluate(x, y);
    }

    public static double evaluate(double x, double y) {
        return 1 / (1 + x * x + y * y);
    }
}

public class GeneticAlgorithm {

    private static final int POPULATION_SIZE = 100;
    private static final int GENERATIONS = 1000;
    private static final double MUTATION_RATE = 0.05;
    private static final Random random = new Random();

    public static void main(String[] args) {
        runGeneticAlgorithm(true); // Элитный отбор
        System.out.println("===========================================");
        runGeneticAlgorithm(false); // Рулеточный отбор
    }

    public static void runGeneticAlgorithm(boolean useElitism) {
        ArrayList<Individual> population = createInitialPopulation();
        
        for (int generation = 0; generation < GENERATIONS; generation++) {
            ArrayList<Individual> newPopulation = new ArrayList<>();

            if (useElitism) {
                // Сохранение элиты
                Collections.sort(population, (a, b) -> Double.compare(b.fitness, a.fitness));
                newPopulation.add(population.get(0)); // Сохранить лучший
            }

            while (newPopulation.size() < POPULATION_SIZE) {
                Individual parent1 = selectParent(population, useElitism);
                Individual parent2 = selectParent(population, useElitism);
                Individual child = crossover(parent1, parent2);
                mutate(child);
                newPopulation.add(child);
            }

            population = newPopulation;
        }

        Collections.sort(population, (a, b) -> Double.compare(b.fitness, a.fitness));
        Individual bestIndividual = population.get(0);
        System.out.println("Лучший индивидуум: x=" + bestIndividual.x + ", y=" + bestIndividual.y + " | f(x,y)=" + bestIndividual.fitness);
    }

    private static ArrayList<Individual> createInitialPopulation() {
        ArrayList<Individual> population = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            double x = random.nextDouble() * 20 - 10; // Задает диапазон [-10, 10]
            double y = random.nextDouble() * 20 - 10; // Задает диапазон [-10, 10]
            population.add(new Individual(x, y));
        }
        return population;
    }

    private static Individual selectParent(ArrayList<Individual> population, boolean useElitism) {
        if (useElitism) {
            // Рулеточный отбор
            double totalFitness = population.stream().mapToDouble(ind -> ind.fitness).sum();
            double rand = random.nextDouble() * totalFitness;
            double cumulativeFitness = 0;
            for (Individual ind : population) {
                cumulativeFitness += ind.fitness;
                if (cumulativeFitness >= rand) {
                    return ind;
                }
            }
        } else {
            // Элитный отбор — просто случайный выбор
            return population.get(random.nextInt(POPULATION_SIZE));
        }
        return null;
    }

    private static Individual crossover(Individual parent1, Individual parent2) {
        double x = (parent1.x + parent2.x) / 2;
        double y = (parent1.y + parent2.y) / 2;
        return new Individual(x, y);
    }

    private static void mutate(Individual individual) {
        if (random.nextDouble() < MUTATION_RATE) {
            individual.x += random.nextGaussian(); // Нормальное распределение для мутации
        }
        if (random.nextDouble() < MUTATION_RATE) {
            individual.y += random.nextGaussian(); // Нормальное распределение для мутации
        }
        // Пересчитываем оценку
        individual.fitness = Individual.evaluate(individual.x, individual.y);
    }
}
