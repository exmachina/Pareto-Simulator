package com.musifier.pareto;

public class Person implements Comparable<Person> {

	float cash;

	public Person(float startCach) {
		this.cash = startCach;
	}

	public void increment() {
		cash++;
	}

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

}
