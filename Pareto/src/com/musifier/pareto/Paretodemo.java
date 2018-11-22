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
 *         each at the beginning of the game. They play two and two beting one
 *         dollar each and one of them randomly wins.
 *
 */
public class Paretodemo implements Runnable {

	enum taxPaybackModes {
		Charity, CharityMinimum, Flat
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
	float cashMax;

	private int categoryCount = 1000;

	private int gamesPerRound = 100;

	private int height = 1000;
	private float maxCash;
	/**
	 * Them minimum cash of any player. When a players cash is down to 0 that person
	 * is put of the game.
	 */
	private float minCash;
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

	private long sleepMs = 0;
	private float startCach = 20;

	private float taxMaxProc = 0.01f;

	private float taxMaxProcLim = 100;

	private float taxMinProc = 0.01f;
	private taxPaybackModes taxPaybackMode = taxPaybackModes.CharityMinimum;

	private float taxPovertyThreshold = 5;

	private float taxTreasury;

	private int width = 1500;

	private JFrame frame;

	public Paretodemo() {
		init();
	}

	private float charityMinimumPayback(float treasury, boolean distributeNonNeedToAll) {
		float resAcc = 0;
		for (Person p : personsAll) {
			float need = taxPovertyThreshold - p.getCash();
			if (need > 0) {
				resAcc += need;
				p.increment(need);
			}
		}

		if (distributeNonNeedToAll) {
			float rest = treasury - resAcc;
			float d = rest / personCount;
			for (Person p : personsAll) {
				p.increment(d);
			}
			resAcc = treasury;
		}

		return resAcc;
	}

	private float charityPayback(float taxAcc) {
		float res = taxAcc;
		List<Person> poor = new ArrayList<Person>();
		for (Person p : personsAll) {
			if (p.getCash() < taxPovertyThreshold) {
				poor.add(p);
			}
		}

		if (poor.size() == 0) {
			flatPayback(taxAcc);
		} else {
			float f = taxAcc / poor.size();
			for (Person p : poor) {
				p.increment(f);
			}
		}
		return res;
	}

	private float flatPayback(float taxAcc) {
		// TODO Auto-generated method stub
		// Flat payback
		float k = taxAcc / personCount;
		// System.out.println("incrementing by tax return=" + k);
		for (Person p : personsAll) {
			p.increment(k);
		}
		return taxAcc;
	}

	public List<Person> getAllList() {
		return personsAll;
	}

	float getCashOfTop20(ArrayList<Person> list) {
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
		return taxPovertyThreshold;
	}

	void init() {
		personCount = categoryCount * personsPerCategory;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		taxTreasury = 0;
		cashMax = personCount * startCach;
		personsActive = new ArrayList<Person>();
		personsAll = new ArrayList<Person>();
		personsDumped = new ArrayList<Person>();
		for (int i = 0; i < personCount; i++) {
			Person p = new Person(startCach);
			personsActive.add(p);
			personsAll.add(p);
		}

		// statArray = new int[cashMax];
		panel = new ParetoPanel(cashMax, personsAll, this);
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

	private float need(ArrayList<Person> persons) {
		float acc = 0;
		for (Person p : persons) {
			float c = p.getCash();
			if (c < taxPovertyThreshold) {
				acc += (taxPovertyThreshold - c);
			}
		}
		return acc;
	}

	private void paint() {
		panel.repaint();
	}

	private void play(int rounds) {

		for (int i = 0; i < rounds; i++) {

			Person p1 = getRandomPlayer(personsActive);
			Person p2 = getRandomPlayer(personsActive);
			p1.increment();
			p2.decrement();

			if (p2.getCash() < 1) {
				personsActive.remove(p2);
				personsDumped.add(p2);
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
	 * 
	 * @param taxAcc
	 */
	private float redistribute(float taxTreasury) {
		float res = 0;
		switch (taxPaybackMode) {
		case Flat:
			res = flatPayback(taxTreasury);
			break;
		case Charity:
			res = charityPayback(taxTreasury);
			break;
		case CharityMinimum:
			res = charityMinimumPayback(taxTreasury, redistNonNeedToAll);
			break;
		default:
			assert false;
			break;
		}
		return res;
	}

	private void resurectPersons() {
		List<Person> tmp = new ArrayList<Person>();
		for (Person p : personsDumped) {
			if (p.getCash() >= taxPovertyThreshold) {
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
					taxTreasury -= redistribute(taxTreasury);
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
		System.out.println("max " + taxMaxProc);
	}

	public void setTaxMaxProcLim(double d) {
		this.taxMaxProcLim = (float) d;
	}

	public void setTaxMinProc(double d) {
		this.taxMinProc = (float) d;
		System.out.println("min " + taxMinProc);
	}

	public void setTaxPovertyThreshold(float taxPovertyThreshold) {
		this.taxPovertyThreshold = taxPovertyThreshold;
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

	private float tax() {
		float taxAcc = 0;
		for (Person p : personsAll) {
			taxAcc += p.tax(taxPovertyThreshold, taxMinProc, taxMaxProc, taxMaxProcLim);
		}

		return taxAcc;
	}

	public int getPersonCount() {
		return personCount;
	}

	public void setPersonCount(int personCount) {
		this.personCount = personCount;
	}

}
