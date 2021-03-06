package template;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import logist.plan.Action;
import logist.task.Task;
import logist.topology.Topology.City;

public class State {
	
	private final City city;
	private final List<Task> tasks;
	private final List<Task> toDeliver;
	private final List<Action> actions;
	private final double dist;
	
	
	
	public State(City c, List<Task> t, List<Task> d, List<Action> a, double dist) {
		this.city = c;
		this.tasks = t;
		this.toDeliver = d;
		this.actions = a;
		this.dist = dist;
		
	}
	
	public State(State s) {
		this.city = s.city;
		this.tasks = s.tasks;
		this.toDeliver = s.toDeliver;
		this.actions = s.actions;
		this.dist = s.dist;
	}
	
	public boolean isFinal() {
		return tasks.isEmpty() && toDeliver.isEmpty();
	}
	
	public boolean equals(State s) {
		return this.city.equals(s.getCity()) && this.tasks.equals(s.getTasks()) && this.toDeliver.equals(s.getToDeliver());
	}
	
	private HashSet<City> toGo(){
		HashSet<City> cities = new HashSet<City>();
		for(Task t: this.tasks) {
			cities.add(t.pickupCity);
			cities.add(t.deliveryCity);
		}
		for(Task t: this.toDeliver) {
			cities.add(t.deliveryCity);
		}
		return cities;
	}
	
	
	/*
	 * Our heuristic function
	 */
	public double distanceToFinal() {
		HashSet<City> cities = this.toGo();
		if (cities.isEmpty())
			return 0.;
		double maxD = Double.MIN_VALUE;
		double d = 0;
		for (Task t: this.tasks){
			d = this.city.distanceTo(t.pickupCity) + t.pickupCity.distanceTo(t.deliveryCity);
			if (d>maxD)
				maxD = d;
		}
		for (Task t: this.toDeliver){
			d = this.city.distanceTo(t.deliveryCity);
			if (d>maxD)
				maxD = d;
		}
		return maxD;
	}
	
	/*
	 * The sum of the cost of past actions + the heuristic
	 */
	public double d() {
		return this.dist + this.distanceToFinal();
	}

	/*
	 * Getters
	 */
	public City getCity() {
		return this.city;
	}
	
	public List<Task> getTasks(){
		return this.tasks;
	}
	
	public List<Task> getToDeliver() {
		return this.toDeliver;
	}
	
	public List<Action> getActions(){
		return this.actions;
	}
	
	public double getDist() {
		return this.dist;
	}
	
	/*
	 * Lists the set of tasks that can be picked up in the current city
	 */
	public List<Task> localPickupTasks(){
		List<Task> local = new ArrayList<Task>();
		for(Task t: this.tasks) {
			if(t.pickupCity.equals(this.city)) {
				local.add(t);
			}
		}
		return local;
	}
	
	/*
	 * Returns the list of tasks transported that can be delivered in the current city
	 */
	public List<Task> localDeliverTask(){
		List<Task> local = new ArrayList<Task>();
		for(Task t: this.toDeliver) {
			if(t.deliveryCity.equals(this.city)) {
				local.add(t);
			}
		}
		return local;
	}
	
	/*
	 * Total weight currently transported
	 */
	public int weight() {
		int w = 0;
		for(Task t: this.toDeliver) {
			w += t.weight;
		}
		return w;
	}
	
	public State move(City c) {
		List<Action> act = new ArrayList<Action>(this.actions);
		act.add(new Action.Move(c));
		return new State(c, this.tasks, this.toDeliver, act, this.dist + this.city.distanceTo(c));
	}
	
	public State deliver(Task t) {
		List<Task> deliver = new ArrayList<Task>(this.toDeliver);
		deliver.remove(t);
		List<Action> act = new ArrayList<Action>(this.actions);
		act.add(new Action.Delivery(t));
		return new State(this.city, this.tasks, deliver, act, this.dist);
	}
	
	public State pickup(Task t) {
		List<Task> newtasks = new ArrayList<Task>(this.tasks);
		List<Task> deliver = new ArrayList<Task>(this.toDeliver);
		List<Action> act = new ArrayList<Action>(this.actions);
		act.add(new Action.Pickup(t));
		newtasks.remove(t);
		deliver.add(t);
		return new State(this.city, newtasks, deliver, act, this.dist);
	}

}
