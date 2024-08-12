package Vehicles;

import java.util.ArrayList;

public class RepairShop implements Mechanic{

    private ArrayList<Mechanic> mechanics = new ArrayList<Mechanic>();

    /**
     * add mechanic element
     * @param element
     */
    public void add(Mechanic element) {
        mechanics.add(element);
    }


    /**
     * remove mechanic element
     * @param element
     */
    public void remove(Mechanic element) {
        mechanics.remove(element);
    }

    /**
     * remove all the mechanics
     */
    public void removeAll(){
        mechanics.clear();
    }

    /**
     * fix all the damage to each mechanic element
     */
    @Override
    public void fixDamage() {

        for (Mechanic r: mechanics) {
            r.fixDamage();
        }
    }

    /**
     * calculate all the cost to each mechanic element
     * @return the final cost
     */
    @Override
    public int calculateCost() {
        int temp = 0;
        for (Mechanic r: mechanics) {
            temp += r.calculateCost();
        }
        return temp;
    }
}
