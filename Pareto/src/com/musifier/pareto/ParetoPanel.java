package com.musifier.pareto;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.musifier.pareto.ParetoPanel.PaintModes;
import com.musifier.utils.gui.NumberField;
import com.musifier.utils.gui.NumberFieldListener;

public class ParetoPanel extends JPanel implements ActionListener, NumberFieldListener {

	public enum PaintModes {
		WealthPerPerson, PersonsPerWealth

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String START = "Start";
	private int count = 0;
	// private int[] map;
	private float max;
	private int base = 900;
	private JTextField minField;
	private JTextField maxField;
	private JTextField personCountField;
	private Paretodemo demo;
	private JTextField allPartio20Field;
	private JTextField activePartio20Field;
	private JTextField treasuryField;
	private JTextField needField;
	private JCheckBox redistNonNeedToAllCB;
	private NumberField povertyThreshholdNumField;
	private JButton startStopButton;
	private NumberField lowTaxField;
	private NumberField highTaxField;
	private JButton resetButton;
	private NumberField highTaxLimField;
	private NumberField sleepMsField;
	private PaintModes paintMode = PaintModes.PersonsPerWealth;
	private int assertCount;
	private JTextField catCountField;
	private int perWealthColumnWidthMax = 40;
	private JComboBox<PaintModes> paintModeCB;
	private JTextField normalCashField;

	public ParetoPanel(float cachMax, List<Person> personsAll, Paretodemo demo) {
		this.demo = demo;
		// this.map = statArray;
		this.max = cachMax;

		setLayout(new BorderLayout());
		add(buildPanel(), BorderLayout.SOUTH);
	}

	private JPanel buildPanel() {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(buildControlPanel());
		p.add(buildDisplayPanel());

		return p;
	}

	private Component buildControlPanel() {
		JPanel p = new JPanel();

		startStopButton = new JButton(START);
		p.add(startStopButton);
		startStopButton.addActionListener(this);

		resetButton = new JButton("Reset");
		p.add(resetButton);
		resetButton.addActionListener(this);

		PaintModes[] pms = PaintModes.values();
		paintModeCB = new JComboBox<PaintModes>(pms);
		paintModeCB.setSelectedItem(paintMode);
		p.add(paintModeCB);
		paintModeCB.addActionListener(this);

		p.add(new JLabel("RNNTA"));
		redistNonNeedToAllCB = new JCheckBox();
		p.add(redistNonNeedToAllCB);
		redistNonNeedToAllCB.addActionListener(this);
		redistNonNeedToAllCB.setSelected(demo.isRedistNonNeedToAll());

		p.add(new JLabel("Poverty threshold"));
		povertyThreshholdNumField = new NumberField().setColumns(2).setStepDefault(1);
		p.add(povertyThreshholdNumField.getGuiComponent());
		povertyThreshholdNumField.addNumberFieldListener(this);
		povertyThreshholdNumField.setValueL((long) demo.getTaxPovertyThreshold()).setMin(-10);

		p.add(new JLabel("Low tax"));
		lowTaxField = new NumberField().setColumns(4).setMode(NumberField.floatMode).setStepDefault(1, 100)
				.setStepShift(1, 1000).setStepAlt(1, 10000).setMin(0);
		p.add(lowTaxField.getGuiComponent());
		lowTaxField.addNumberFieldListener(this);
		lowTaxField.setValueD(demo.getTaxMinProc());

		p.add(new JLabel("High tax"));
		highTaxField = new NumberField().setColumns(4).setMode(NumberField.floatMode).setStepDefault(1, 100)
				.setStepShift(1, 1000).setStepAlt(1, 10000).setMin(0);
		p.add(highTaxField.getGuiComponent());
		highTaxField.addNumberFieldListener(this);
		highTaxField.setValueD(demo.getTaxMaxProc());

		p.add(new JLabel("High tax lim"));
		highTaxLimField = new NumberField().setColumns(4).setMode(NumberField.floatMode).setStepShift(10)
				.setStepAlt(100).setMin(1);
		p.add(highTaxLimField.getGuiComponent());
		highTaxLimField.addNumberFieldListener(this);
		highTaxLimField.setValueD(demo.getTaxMaxProcLim());

		p.add(new JLabel("Sleep ms"));
		sleepMsField = new NumberField().setColumns(6).setMode(NumberField.intMode).setStepDefault(1000)
				.setStepShift(100).setStepAlt(10).setMin(0);
		p.add(sleepMsField.getGuiComponent());
		sleepMsField.addNumberFieldListener(this);
		sleepMsField.setValueD(demo.getSleepMs());

		return p;
	}

	private Component buildDisplayPanel() {
		JPanel p = new JPanel();

		p.add(new JLabel("Persons"));
		personCountField = new JTextField(6);
		p.add(personCountField);

		p.add(new JLabel("Min"));
		minField = new JTextField(2);
		p.add(minField);

		p.add(new JLabel("Max"));
		maxField = new JTextField(4);
		p.add(maxField);

		p.add(new JLabel("20% of all"));
		allPartio20Field = new JTextField(8);
		p.add(allPartio20Field);

		p.add(new JLabel("20% of active"));
		activePartio20Field = new JTextField(8);
		p.add(activePartio20Field);

		p.add(new JLabel("cash normal"));
		normalCashField = new JTextField(8);
		p.add(normalCashField);

		p.add(new JLabel("Need"));
		needField = new JTextField(8);
		p.add(needField);

		p.add(new JLabel("Treasury"));
		treasuryField = new JTextField(8);
		p.add(treasuryField);

		p.add(new JLabel("Categories"));
		catCountField = new JTextField(8);
		p.add(catCountField);

		return p;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		synchronized (demo) {
			Collections.sort(demo.personsAll);
			Collections.sort(demo.personsActive);
			stats();
			switch (paintMode) {
			case WealthPerPerson:
				paintWealthPerPerson(g);
				break;
			case PersonsPerWealth:
				synchronized (demo) {
					paintPersonsPerWealth(g);
				}

				break;
			default:
				assert false;
				break;
			}
		}

		// g.drawLine(30, 20, 100, 100);

	}

	private void paintPersonsPerWealth(Graphics g) {
		// TODO Auto-generated method stuba

		List<Person> all = demo.getAllList();
		assertWellSorted(all);

		int catsDemo = demo.getCategoryCount();
		int pSize = all.size();

		float w = -Float.MAX_VALUE;
		int cats = 0;
		for (Person p : all) {
			float pw = p.getCash();
			if (w == pw) {
			} else if (w < pw) {
				cats++;
				w = pw;
			} else {
				assert false : "w=" + w + ", pw=" + pw;
			}
		}
		int catMax = 40;
		if (catMax < cats) {
			cats = catMax;
		}
		float wLo = all.get(0).getCash();
		float wHi = all.get(pSize - 1).getCash();
		float wRange = (wHi - wLo) / cats;

		int width = Math.round(1000f / cats);
		if (width > perWealthColumnWidthMax) {
			width = perWealthColumnWidthMax;
		}
		float scale = 1000f / demo.getPersonCount();
		int x = 20;

		catCountField.setText(Integer.toString(cats));

		w = -Float.MAX_VALUE;
		int pStart = 0;
		float wStart = wLo;

		for (int cat = 0; cat < cats; cat++) {
			float wEnd = wStart + wRange;
			int persons = 0;
			for (int i = pStart; i < all.size() && w <= wEnd; i++) {
				w = all.get(i).getCash();
				if (w <= wEnd) {
					persons++;
				} else {
					int n = Math.round(persons * scale);
					g.drawRect(x, base - n, width, n);
					pStart = i;
				}
			}
			wStart = wEnd;
			x += width;
		}

	}

	private void assertWellSorted(List<Person> all) {
		// System.out.println("all=" + all.toString());
		assertCount++;
		for (int i = 1; i < all.size(); i++) {
			float f0 = all.get(i - 1).getCash();
			float f1 = all.get(i).getCash();

			assert f0 <= f1 : "i=" + i + ", f0=" + f0 + ", f1=" + f1 + " at count " + assertCount + " in all="
					+ all.toString();
		}
	}

	private void paintWealthPerPerson(Graphics g) {
		List<Person> all = demo.getAllList();
		int catCount = demo.getCategoryCount();
		int personsPerCategory = demo.getPersonsPerCategory();
		int width = 1000 / catCount;
		int x = 20;

		int pStart = 0;
		for (int cat = 0; cat < catCount; cat++) {
			int pEnd = pStart + personsPerCategory;
			float nf = 0;
			for (int i = pStart; i < pEnd; i++) {
				nf += all.get(i).getCash();
			}
			int n = (int) nf;
			g.drawRect(x, base - n, width, n);

			// System.out.println("drawRect " + x + ", " + base + ", " + width + ", " +
			// (base - n));

			pStart += personsPerCategory;
			x += width;
		}

	}

	public void setMinCash(float minCash) {
		minField.setText(Float.toString(minCash));
	}

	public void setMaxCash(float maxCash) {
		maxField.setText(Float.toString(maxCash));
	}

	public void setPersonCount(int size) {
		personCountField.setText(Integer.toString(size));
	}

	public void setCashOfTop20All(float q) {
		allPartio20Field.setText(Float.toString(q));
	}

	public void setCashOfTop20Active(float q) {
		activePartio20Field.setText(Float.toString(q));
	}

	public void setTreasury(float taxTreasury) {
		treasuryField.setText(Float.toString(taxTreasury));
		// TODO Auto-generated method stub

	}

	public void setNeed(float need) {
		needField.setText(Float.toString(need));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == redistNonNeedToAllCB) {
			demo.setRedistNonNeedToAll(redistNonNeedToAllCB.isSelected());
		} else if (source == startStopButton) {
			if (demo.isRunning()) {
				demo.stop(startStopButton);
				resetButton.setEnabled(true);
			} else {
				demo.start(startStopButton);
				resetButton.setEnabled(false);
			}
		} else if (source == resetButton) {
			demo.init();
		}

		else if (source == paintModeCB) {
			paintMode = (PaintModes) paintModeCB.getSelectedItem();
			repaint();
		}

		else {
			assert false : "unknown source";
		}
		// TODO Auto-generated method stub

	}

	@Override
	public void numberChanged(NumberField nf) {
		if (nf == povertyThreshholdNumField) {
			demo.setTaxPovertyThreshold(nf.getIntValue());
		}

		else if (nf == lowTaxField) {
			double d = nf.getDoubleValue();
			if (demo.getTaxMaxProc() < d) {
				demo.setTaxMaxProc(d);
				highTaxField.setValueD(d);
			}
			demo.setTaxMinProc(d);
		}

		else if (nf == highTaxField) {
			double d = nf.getDoubleValue();
			if (d < demo.getTaxMinProc()) {
				demo.setTaxMinProc(d);
				lowTaxField.setValueD(d);
			}
			demo.setTaxMaxProc(d);
		}

		else if (nf == highTaxLimField) {
			demo.setTaxMaxProcLim(nf.getDoubleValue());
		} else if (nf == sleepMsField) {
			demo.setSleepMs(nf.getLongValue());
		}

		else {
			assert false : "unknown source";
		}

	}

	@Override
	public void enterPressed() {
		// TODO Auto-generated method stub

	}

	public void setNormalCash(float cash) {
		normalCashField.setText(Float.toString(cash));
	}

	/**
	 * Pre: lists must be sorted
	 */
	private void stats() {

		float minCash = demo.cashMax;
		float maxCash = 0;

		for (Person p : demo.getAllList()) {
			float c = p.getCash();
			if (c < minCash) {
				minCash = c;
			}
			if (maxCash < c) {
				maxCash = c;
			}
		}

		setMinCash(minCash);
		setMaxCash(maxCash);
		setPersonCount(demo.personsActive.size());

		float n = demo.getCashOfTop20(demo.personsAll);
		float q = (n * 100) / demo.cashMax;
		setCashOfTop20All(q);

		n = demo.getCashOfTop20(demo.personsActive);
		q = (n * 100) / demo.cashMax;
		setCashOfTop20Active(q);

		int i = demo.personCount / 2;
		setNormalCash(demo.personsAll.get(i).getCash());

	}

}
