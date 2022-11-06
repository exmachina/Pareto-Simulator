/**
 * 
 */
package com.musifier.pareto;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author jonas
 *
 *         Demonstrate a game where personCount persons with startCach dollars
 *         each at the beginning of the game. They play two and two betting one
 *         dollar each and one of them randomly wins.
 *
 */
public class Paretodemo implements Runnable {

	enum WelfareModes {
		Charity, CharityMinimum, Flat, CharityMinimumRDNTA
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Paretodemo();
	}

	// private int[] statArray = null;
	/**
	 * Total cash in system
	 */
	float cashTotal;

	private int categoryCount = 1000;

	private int gamesPerRound = 100;

	/**
	 * Height of main panel
	 */
	private int height = 1000;

	private ParetoPanel panel;
	/**
	 * The number of persons in the system.
	 */
	int personCount;
	ArrayList<Person> personsActive;
	ArrayList<Person> personsAll;
	ArrayList<Person> personsDumped;

	private int personsPerCategory = 6;

	private boolean redistNonNeedToAll;
	private boolean running;

	private long sleepMs = 30;

	/**
	 * The amount given to each person from start
	 */
	private float startCach = 20;

	private float taxMaxProc = 0.01f;

	private float taxMaxProcLim = 100;

	private float taxMinProc = 0.01f;
	public WelfareModes welfareMode = WelfareModes.Flat;

	/**
	 * 
	 */
	private float povertyLim = 5;

	/**
	 * The amount in the "state tax treasury"
	 */
	private float taxTreasury;

	private int width = 1500;

	private JFrame frame;

	Person personX;

	public float abilityStart = 4;
	public int abilityMin = 2;
	public int abilityMax = 6;

	float abilityDelta = 0.01f;

	public boolean welfareAbilityBoost;

	public Paretodemo() {
		init();
	}

	/**
	 * 
	 * @param treasury
	 * @param distributeNonNeedToAll
	 * @return
	 */
	private float paybackCharityMinimum(float treasury, boolean distributeNonNeedToAll) {
		float needAcc = 0;
		List<Person> poor = new ArrayList<Person>();
		for (Person p : personsAll) {
			float need = povertyLim - p.getCash();
			if (need > 0) {
				needAcc += need;
				poor.add(p);
//				p.increment(need);
			}
		}

		if (needAcc <= treasury) {
			for (Person p : poor) {
				float need = povertyLim - p.getCash();
				p.increment(need);
			}

			if (distributeNonNeedToAll) {
				float rest = treasury - needAcc;
				float d = rest / personCount;
				for (Person p : personsAll) {
					p.increment(d);
				}
				needAcc = treasury;
			}

		} else {
			float q = treasury / needAcc;
			for (Person p : poor) {
				float need = povertyLim - p.getCash();
				p.increment(need * q);
			}
		}

		return needAcc;
	}

	/**
	 * Bring all persons up to poverttyLim taxAcc equally over all poor persons,<br>
	 * i.e. persons p with <code>p.getCash() < povertyLim</code>
	 * 
	 * @param taxAcc
	 * @return the amount paid to persons.
	 */
	private float paybackCharity(float taxAcc) {
		float res = 0;
		List<Person> poor = new ArrayList<Person>();
		for (Person p : personsAll) {
			float poverty = povertyLim - p.getCash();
			if (poverty > 0) {
				res += poverty;
				p.increment(poverty);
				if (welfareAbilityBoost) {
					p.setAbility(p.getAbility() + abilityDelta);
				}

			}
		}

//		if (poor.size() == 0) {
//			return paybackFlat(taxAcc);
//		} else {
//			float f = taxAcc / poor.size();
//			for (Person p : poor) {
//				p.increment(f);
//			}
//		}
		return res;
	}

	/**
	 * Divide taxAcc equally over all persons.
	 * 
	 * @param taxAcc
	 * @return taxAcc
	 */
	private float paybackFlat(float taxAcc) {

		float k = taxAcc / personCount;
		// System.out.println("incrementing by tax return=" + k);
		for (Person p : personsAll) {
			p.increment(k);
			if (welfareAbilityBoost) {
				p.setAbility(p.getAbility() + abilityDelta);
			}
		}
		return taxAcc;
	}

	public List<Person> getAllList() {
		return personsAll;
	}

	float getCashOfRichest20Percent(ArrayList<Person> list) {
		int first = (int) (0.8 * list.size());
		// System.out.println("First=" + first);
		int amount = 0;
		for (int i = first; i < list.size(); i++) {
			amount += list.get(i).getCash();
		}
		return amount;
	}

	public int getCategoryCount() {
		return categoryCount;
	}

	public int getPersonsPerCategory() {
		return personsPerCategory;
	}

	private Person getRandomPlayer(List<Person> list) {
		double r = Math.random();
		int n = (int) Math.floor(r * (list.size() - 1));
		if (n >= list.size() - 1) {

			n--;
			System.out.println("fixed " + n);
			System.out.println("persons.size= " + list.size());
		}
		return list.get(n);
	}

	public long getSleepMs() {
		return sleepMs;
	}

	public float getStartCach() {
		return startCach;
	}

	public float getTaxMaxProc() {
		return taxMaxProc;
	}

	public float getTaxMaxProcLim() {
		return taxMaxProcLim;
	}

	public float getTaxMinProc() {
		return taxMinProc;
	}

	public float getTaxPovertyThreshold() {
		return povertyLim;
	}

	void init() {
		personCount = categoryCount * personsPerCategory;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		taxTreasury = 0;
		cashTotal = personCount * startCach;
		personsActive = new ArrayList<Person>();
		personsAll = new ArrayList<Person>();
		personsDumped = new ArrayList<Person>();
		for (int i = 0; i < personCount; i++) {
			Person p = new Person(startCach, this);
			personsActive.add(p);
			personsAll.add(p);
		}

		int r = (int) Math.floor(Math.random() * personCount);
		personX = personsAll.get(r);

		// statArray = new int[cashMax];
		panel = new ParetoPanel(cashTotal, personsAll, this);
		panel.setConstants(personCount, categoryCount, personsPerCategory);
		panel.setBounds(0, 0, width, height);

		if (frame == null) {
			frame = new JFrame("Pareto demo");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		}

		frame.setContentPane(panel);

		frame.pack();
		frame.setBounds(0, 0, width, height);

	}

	public boolean isRedistNonNeedToAll() {
		return redistNonNeedToAll;
	}

	public boolean isRunning() {
		return running;
	}

	/**
	 * Returns the sum total of personal poverty measured as personal cash less than
	 * <code>povertyLim</code> over all persons.
	 * 
	 * @param persons
	 * @return
	 */
	private float need(ArrayList<Person> persons) {
		float acc = 0;
		for (Person p : persons) {
			float c = p.getCash();
			if (c < povertyLim) {
				acc += (povertyLim - c);
			}
		}
		return acc;
	}

	private void paint() {
		panel.repaint();
	}

	/**
	 * For each round,
	 * <ol>
	 * <li>Pick two random persons from <code>personsActive</code>
	 * <li>increment one and decrement the other
	 * <li>if the decremented person is broke, i.e. has zero dollar left, move him
	 * to <code>personsDumped</code>
	 * </ol>
	 * 
	 * 
	 * @param rounds
	 */
	private void play(int rounds) {

		for (int i = 0; i < rounds; i++) {

			Person p1 = getRandomPlayer(personsActive);
			Person p2 = getRandomPlayer(personsActive);

			float a1 = p1.getAbility();
			float a2 = p2.getAbility();

			if (Math.random() * (a1 + a2) < a1) {
				p1.increment();
				p2.decrement();

				p1.setAbility(a1 + abilityDelta);
				p2.setAbility(a2 - abilityDelta);

			} else {
				p2.increment();
				p1.decrement();

				p2.setAbility(a2 + abilityDelta);
				p1.setAbility(a1 - abilityDelta);

			}

			if (p2.getCash() < 1) {
				personsActive.remove(p2);
				personsDumped.add(p2);
			}
			if (p1.getCash() < 1) {
				personsActive.remove(p1);
				personsDumped.add(p1);
			}
		}

		// List<Person> removeList = new ArrayList<Person>();
		// for (Person p : persons) {
		// if (p.getCach() <= 0) {
		//
		// removeList.add(p);
		// }
		// }
		// persons.removeAll(removeList);

	}

	/**
	 * Distributes the collected tax over persons in a way determined by
	 * welfareMode.
	 * 
	 * @return the amount paid back to persons
	 * 
	 * @param taxAcc <@param taxTreasury
	 */
	private float payback_TaxToPersons(float taxTreasury) {
		float res = 0;
		switch (welfareMode) {
		case Flat:
			res = paybackFlat(taxTreasury);
			break;
		case Charity:
			res = paybackCharity(taxTreasury);

			break;
		case CharityMinimum:
			res = paybackCharityMinimum(taxTreasury, false);
			break;
		case CharityMinimumRDNTA:
			res = paybackCharityMinimum(taxTreasury, true);
			break;
		default:
			assert false;
			break;
		}
		return res;
	}

	/**
	 * For all persons p in <code>personsDumped</code>, if
	 * <code>p,cash() >= povetyLim</code>, move to p to <code>personsActive</code>.
	 */
	private void resurectPersons() {
		List<Person> tmp = new ArrayList<Person>();
		for (Person p : personsDumped) {
			if (p.getCash() >= povertyLim) {
				tmp.add(p);
			}
		}
		personsDumped.removeAll(tmp);
		personsActive.addAll(tmp);
	}

	public void run() {
		// long repeats = repeatCount;
		while (running) {
			synchronized (this) {
				play(gamesPerRound);
				float need = need(personsAll);
				panel.setNeed(need);
				while (taxMaxProc > 0 && taxTreasury < need) {
					taxTreasury += tax();
				}
				if (taxTreasury > 0) {
					taxTreasury -= payback_TaxToPersons(taxTreasury);
				}

				resurectPersons();
				// System.out.println("Treasury="+taxTreasury);
				panel.setTreasury(taxTreasury);

			}
			paint();// stats is called from painComponent to ensure sorted lists as input for stats
			// panel.stats();
			sleep();

		}
	}

	public void setCategoryCount(int categoryCount) {
		this.categoryCount = categoryCount;
	}

	public void setPersonsPerCategory(int personsPerCategory) {
		this.personsPerCategory = personsPerCategory;
	}

	public void setRedistNonNeedToAll(boolean b) {
		this.redistNonNeedToAll = b;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public void setSleepMs(long sleepMs) {
		this.sleepMs = sleepMs;
	}

	public void setStartCach(float startCach) {
		this.startCach = startCach;
	}

	public void setTaxMaxProc(double d) {
		this.taxMaxProc = (float) d;
//		System.out.println("max " + taxMaxProc);
	}

	public void setTaxMaxProcLim(double d) {
		this.taxMaxProcLim = (float) d;
	}

	public void setTaxMinProc(double d) {
		this.taxMinProc = (float) d;
//		System.out.println("min " + taxMinProc);
	}

	public void setTaxPovertyThreshold(float taxPovertyThreshold) {
		this.povertyLim = taxPovertyThreshold;
	}

	private void sleep() {
		try {
			Thread.sleep(sleepMs);
		} catch (Exception e) {
		}
	}

	void start(JButton b) {
		running = true;
		Thread t = new Thread(this);
		b.setText("Stop");
		t.start();
	}

	void stop(JButton b) {
		running = false;
		b.setText("Start");

	}

	/**
	 * 
	 * @return
	 * @see tax
	 */
	private float tax() {
		float taxAcc = 0;
		for (Person p : personsAll) {
			taxAcc += p.tax(povertyLim, taxMinProc, taxMaxProc, taxMaxProcLim);
		}

		return taxAcc;
	}

	public int getPersonCount() {
		return personCount;
	}

	public void setPersonCount(int personCount) {
		this.personCount = personCount;
	}

	public float getCashOfPoorest20Percent(ArrayList<Person> list) {
		int last = (int) (0.2 * list.size());
		// System.out.println("First=" + first);
		int amount = 0;
		for (int i = 0; i <= last; i++) {
			amount += list.get(i).getCash();
		}
		return amount;
	}

}
