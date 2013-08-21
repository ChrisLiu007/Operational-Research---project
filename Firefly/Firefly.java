package pl.edu.agh.iet.BO.pizzeria.Algorithms.Firefly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import pl.edu.agh.iet.BO.pizzeria.Beans.Point;
import pl.edu.agh.iet.BO.pizzeria.Beans.Vehicles.Vehicle;

public class Firefly {

	private Map<Vehicle, List<Point>> permutation = new HashMap<Vehicle, List<Point>>();

	public Firefly(List<Point> points, List<Vehicle> vehicles) {

		Random rand = new Random();
		List<Point> vehiclePoints = new ArrayList<Point>();
		
		/*
		 * we create a permutation which is a map of vehicles and randomly chosen points assigned to them
		 */
		for (Vehicle vehicle : vehicles) {

			vehiclePoints.clear();
			for (int i = 0; i < vehicle.getCapacity(); i++) {
				vehiclePoints.add(points.remove(rand.nextInt(points.size())));
			}
			permutation.put(vehicle, new ArrayList<>(vehiclePoints));
		}
	}

	public double distanceFunction() {

		double result = 0;
		double currentTime;

		for (Vehicle vehicle : permutation.keySet()) {
			currentTime = 0;
			
			currentTime += getDistance(new Point(0.0, 0.0), permutation.get(vehicle).get(0)) / vehicle.getSpeed();
			result += currentTime;
			for (int i = 0; i < vehicle.getCapacity() - 1; i++) {
				currentTime += getDistance(permutation.get(vehicle).get(i), permutation.get(vehicle).get(i + 1)) / vehicle.getSpeed();
				result += currentTime;
			}
		}
		return result / permutation.values().size();

	}

	private double getDistance(Point p1, Point p2) {

		return Math.sqrt((p1.getX() - p2.getX()) * (p1.getX() - p2.getX())
				+ (p1.getY() - p2.getY()) * (p1.getY() - p2.getY()));
	}

	public Map<Vehicle, List<Point>> getPermutation() {
		return permutation;
	}
	
	public Map<Point, Double> getApproximatedDeliveryTime(){
		
		Map<Point, Double> approximatedDeliveryTime = new HashMap<Point, Double>();
		double currentTime;
		
			for (Vehicle vehicle : permutation.keySet()) {
				currentTime = 0;
				currentTime += getDistance(new Point(0.0, 0.0), permutation.get(vehicle).get(0)) / vehicle.getSpeed();
				approximatedDeliveryTime.put(permutation.get(vehicle).get(0), currentTime);
				
				for (int i = 0; i < vehicle.getCapacity() - 1; i++) {
					currentTime += getDistance(permutation.get(vehicle).get(i), permutation.get(vehicle).get(i + 1)) / vehicle.getSpeed();
					approximatedDeliveryTime.put(permutation.get(vehicle).get(i+1), currentTime);
					
				}
		}
		
		return approximatedDeliveryTime;
	}

}
