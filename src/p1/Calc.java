package p1;

public class Calc {

	public static void main(String[] args) {
		Calc c = new Calc();

		for (int i = 0; i <= 10; i++) {
			System.out.println("" + i + " " + c.calculate(i));
		}
	}

	private int calculate(int i) {
		return 3 * i;
	}
}
