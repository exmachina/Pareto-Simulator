package p1;

public class Calc {

	/**
	 * Goint to 20
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Calc c = new Calc();

		for (int i = 0; i <= 20; i++) {
			System.out.println("" + i + " " + c.calculate(i));
		}
	}

	private int calculate(int i) {
		return tripple(i);
	}

	private int tripple(int i) {
		return 3 * i;
	}

}
