package com.musifier.pareto;

public class Person implements Comparable<Person> {

	/**
	 * My cash
	 */
	float cash;
	private float ability = 1;
	private Paretodemo demo;

	public Person(float startCach, Paretodemo demo) {
		this.demo = demo;
		this.cash = startCach;
		this.ability = demo.abilityStart;
	}

	/**
	 * Increment my cash by 1.
	 */
	public void increment() {
		cash++;
	}

	/**
	 * Decrement my cash by 1
	 */
	public void decrement() {
		cash--;
	}

	public float getCash() {
		return cash;
	}

	@Override
	public int compareTo(Person o) {

//		return Float.compare(cash, o.cash);

		float f = getCash() - o.getCash();

		if (f < 0) {
			return -1;
		} else if (0 < f) {
			return 1;
		}

		return 0;
	}

	public float tax(float taxDeduction, float taxMinProc, float taxMaxProc, float taxMaxProcLim) {
		float b = getCash() - taxDeduction;

		float p = Tax.getFactor(b, taxMinProc, taxMaxProc, taxMaxProcLim);

		float tax = b * p;

		decrement(tax);
		return tax;
	}

	private void decrement(float tax) {
		cash -= tax;

	}

	public void increment(float k) {
		cash += k;
	}

	@Override
	public String toString() {
		return "Person [cash=" + cash + "]";
	}

	public float getAbility() {
		if (demo.abilityMax < ability) {
			ability = demo.abilityMax;
		} else if (ability < demo.abilityMin) {
			ability = demo.abilityMin;
		}
		return ability;
	}

	public void setAbility(float a) {
		this.ability = a;
		if (demo.abilityMax < ability) {
			ability = demo.abilityMax;
		} else if (ability < demo.abilityMin) {
			ability = demo.abilityMin;
		}

	}

}
