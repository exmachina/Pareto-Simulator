/**
 * 
 */
package com.musifier.pareto;

/**
 * @author jonas
 *
 */
public class Tax {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		

		float cash = 0;
		float taxMinProc = 0.1f;
		float taxMaxProc = 0.5f;
		float taxMaxProcLim = 100;
		float d = Tax.getFactor(cash, taxMinProc, taxMaxProc, taxMaxProcLim);

		System.out.println("Proc=" + d);

	}

	/**
	 * Returns the taxation factor
	 * @param cash the persons amount of cash after base deduction 
	 * @param taxMinProc
	 * @param taxMaxProc
	 * @param taxMaxProcLim
	 * @return
	 */
	public static float getFactor(float cash, float taxMinProc, float taxMaxProc, float taxMaxProcLim) {

		if (taxMaxProcLim < cash) {
			return taxMaxProc;
		}
		return taxMinProc + ((cash / taxMaxProcLim) * (taxMaxProc - taxMinProc));
	}

}
