package pl.edu.agh.iet.BO.pizzeria.Algorithms.Firefly;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import pl.edu.agh.iet.BO.pizzeria.Algorithms.SolvingAlgorithm;
import pl.edu.agh.iet.BO.pizzeria.Beans.FireflyParams;
import pl.edu.agh.iet.BO.pizzeria.Beans.InputData;
import pl.edu.agh.iet.BO.pizzeria.Beans.OutputData;
import pl.edu.agh.iet.BO.pizzeria.Beans.ParamsInterface;
import pl.edu.agh.iet.BO.pizzeria.Beans.Point;
import pl.edu.agh.iet.BO.pizzeria.Beans.Vehicles.Vehicle;

public class FireflyAlgorithm implements SolvingAlgorithm {

	List<Point> points = new ArrayList<Point>();
	List<Vehicle> vehicles = new ArrayList<Vehicle>();
	List<Firefly> fireflies = new ArrayList<Firefly>();
	Firefly bestFirefly;
	int bestIterationNumber = 0;

	/*
	 * algorithm parameters - default
	 */

	int firefliesNumber = 500;
	int beta = 25;
	int alpha = 10;
	int cycles = 200;

	@Override
	public String getName() {
		return "Firefly algorithm";
	}

	@Override
	public void setInputData(InputData inputData) {

		points = new ArrayList<Point>(inputData.getPoints());
		vehicles = new ArrayList<Vehicle>(inputData.getVehicles());
	}

	@Override
	public <T extends ParamsInterface> void setAlgotithmParams(T params) {

		if (params instanceof FireflyParams) {
			FireflyParams p = (FireflyParams) params;
			this.alpha = p.getAlpha();
			this.beta = p.getBeta();
			this.cycles = p.getCycles();
			this.firefliesNumber = p.getFirefliesNumber();
		} else
			throw new IllegalArgumentException("not instance of FireflyParams");
	}

	@Override
	public OutputData solve() {

		initialize();

		for (int i = 0; i < cycles; i++) {
			for (Firefly firefly : fireflies) {
				if (firefly != bestFirefly)
					mutate(firefly);
			}
			if (setBestFirefly())
				bestIterationNumber = i;
		}

		return new OutputData(bestFirefly.getPermutation(),
				bestFirefly.getApproximatedDeliveryTime(), bestIterationNumber,
				getName());
	}

	/*
	 * initialization - number of fireflies with random permutation created
	 */
	private void initialize() {

		for (int i = 0; i < firefliesNumber; i++) {
			fireflies.add(new Firefly(new ArrayList<Point>(points),
					new ArrayList<Vehicle>(vehicles)));
		}

		/*
		 * leader chosen
		 */
		bestFirefly = fireflies.get(0);
		setBestFirefly();

	}

	private void mutate(Firefly firefly) {
		Random rand = new Random();

		/*
		 * beta-step
		 */
		List<Point> candidateList = mergeLists(firefly);
		List<Point> bestList = mergeLists(bestFirefly);

		List<Integer> candidateFreeSlotsIndexes = new ArrayList<Integer>();
		List<Integer> bestLeftPointsIndexes = new ArrayList<Integer>();

		/*
		 * depending on beta parameter firefly gets closer to the currently best
		 * firefly
		 */
		for (int i = 0; i < bestList.size(); i++) {

			if (candidateList.get(i) != bestList.get(i)) {
				if (rand.nextInt(100) < beta) {

					candidateList.set(i, new Point(bestList.get(i).getX(),
							bestList.get(i).getY()));

				} else {
					candidateFreeSlotsIndexes.add(i);
					bestLeftPointsIndexes.add(i);
				}
			}

		}

		for (int i = 0; i < candidateFreeSlotsIndexes.size(); i++) {

			int randomPointNumber = rand.nextInt(bestLeftPointsIndexes.size());
			candidateList.set(
					candidateFreeSlotsIndexes.get(i),
					new Point(bestList.get(
							bestLeftPointsIndexes.get(randomPointNumber))
							.getX(), bestList.get(
							bestLeftPointsIndexes.get(randomPointNumber))
							.getY()));
			bestLeftPointsIndexes.remove(randomPointNumber);
		}

		/*
		 * alpha-step
		 */

		int swaps;
		if (alpha != 0)
			swaps = rand.nextInt(points.size() * alpha / 100);
		else
			swaps = 0;

		for (int i = 0; i < swaps; i++) {
			swap(firefly);
		}

		/*
		 * unmerging permutation list into vehicle-point lists
		 */
		firefly.getPermutation().clear();
		for (Vehicle vehicle : vehicles) {
			List<Point> newVehiclePermutation = new ArrayList<Point>();
			for (int i = 0; i < vehicle.getCapacity(); i++) {
				newVehiclePermutation.add(candidateList.remove(0));
			}
			firefly.getPermutation().put(vehicle, newVehiclePermutation);
		}

	}

	/*
	 * merging vehicle-point lists into one permutation list for easier
	 * calculation
	 */
	private ArrayList<Point> mergeLists(Firefly firefly) {

		List<Point> pointsList = new ArrayList<Point>();
		for (Vehicle vehicle : firefly.getPermutation().keySet()) {
			for (Point point : firefly.getPermutation().get(vehicle)) {
				pointsList.add(point);
			}
		}

		return (ArrayList<Point>) pointsList;
	}

	/*
	 * swapping permutation elements for alpha step
	 */
	private void swap(Firefly firefly) {
		Random rand = new Random();

		Vehicle swappedVehicle1 = vehicles.get(rand.nextInt(vehicles.size()));
		List<Point> pointsPool = firefly.getPermutation().get(swappedVehicle1);
		Point swappedPoint1 = pointsPool.get(rand.nextInt(pointsPool.size()));

		Vehicle swappedVehicle2 = vehicles.get(rand.nextInt(vehicles.size()));
		pointsPool = firefly.getPermutation().get(swappedVehicle2);
		Point swappedPoint2 = pointsPool.get(rand.nextInt(pointsPool.size()));

		if (swappedPoint1 == swappedPoint2)
			swap(firefly);

		Point temp = swappedPoint1;
		swappedPoint1 = swappedPoint2;
		swappedPoint2 = temp;

	}

	private boolean setBestFirefly() {

		boolean isNewBestFireflyFound = false;
		for (int i = 0; i < firefliesNumber; i++) {
			if (fireflies.get(i).distanceFunction() < bestFirefly
					.distanceFunction()) {
				bestFirefly = fireflies.get(i);
				isNewBestFireflyFound = true;
			}
		}

		return isNewBestFireflyFound;
	}


	@Override
	public Collection<Point> getPoints() {
		return points;
	}

}
