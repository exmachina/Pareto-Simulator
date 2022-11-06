package com.musifier.pareto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.musifier.pareto.ParetoPanel.PaintModes;
import com.musifier.pareto.Paretodemo.WelfareModes;
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
	private int yBase = 900;
	private JTextField minCashField;
	private JTextField maxCashField;
	private JTextField personCountField;
	private Paretodemo demo;
	private JTextField top20ProcentRatioField;
	private JTextField bottom20PercentRatioField;
	/**
	 * 
	 */
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
	private PaintModes paintMode = PaintModes.WealthPerPerson;
	private int assertCount;
	private JTextField catCountField;
	private int perWealthColumnWidthMax = 40;
	private JComboBox<PaintModes> paintModeCB;
	/**
	 * Dispalys the median cash over all persons
	 */
	private JTextField medianCashField;

	/**
	 * 
	 */
	JComboBox<WelfareModes> welfareModeCB;
	private JTextField personsPerCategoyField;
	private NumberField abilityStartField;
	private NumberField abilityMinField;
	private NumberField abilityMaxField;
	private NumberField abilityDeltaField;
	private JCheckBox welfareAbilityBoostCB;

	public ParetoPanel(float cachMax, List<Person> personsAll, Paretodemo demo) {
		this.demo = demo;
		// this.map = statArray;
		this.max = cachMax;

		setLayout(new BorderLayout());
//		add(buildPanel(), BorderLayout.SOUTH);

		add(buildSidePanel(), BorderLayout.EAST);
	}

	private JPanel buildSidePanel() {
		JPanel p = new JPanel();
//		p.setBorder(BorderFactory.createTitledBorder("Side"));
		p.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = c.gridy = 0;
		c.weightx = c.weighty = 5;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(3, 0, 3, 0);

		p.add(new JLabel("Persons"), c);

		c.gridx++;
		personCountField = new JTextField(6);
		p.add(personCountField, c);

		c.gridx = 0;
		c.gridy++;

		p.add(new JLabel("Categories"), c);
		c.gridx++;
		catCountField = new JTextField(8);
		p.add(catCountField, c);

		c.gridx = 0;
		c.gridy++;

		p.add(new JLabel("Persons/Categoy"), c);
		c.gridx++;
		personsPerCategoyField = new JTextField(8);
		p.add(personsPerCategoyField, c);

		c.gridx = 0;
		c.gridy++;

		p.add(new JLabel("Poverty threshold"), c);

		c.gridx++;
		povertyThreshholdNumField = new NumberField().setColumns(2).setStepDefault(1);
		p.add(povertyThreshholdNumField.getGuiComponent(), c);
		povertyThreshholdNumField.addNumberFieldListener(this);
		povertyThreshholdNumField.setValueL((long) demo.getTaxPovertyThreshold()).setMin(-10);

		c.gridx = 0;
		c.gridy++;
		p.add(new JLabel("Low tax"), c);

		c.gridx++;
		lowTaxField = new NumberField().setColumns(6).setMode(NumberField.floatMode).setStepDefault(1, 100)
				.setStepShift(1, 1000).setStepAlt(1, 10000).setMin(0).setMax(1);
		p.add(lowTaxField.getGuiComponent(), c);
		lowTaxField.addNumberFieldListener(this);
		lowTaxField.setValueD(demo.getTaxMinProc());

		c.gridx = 0;
		c.gridy++;
		p.add(new JLabel("High tax"), c);

		c.gridx++;
		highTaxField = new NumberField().setColumns(6).setMode(NumberField.floatMode).setStepDefault(1, 100)
				.setStepShift(1, 1000).setStepAlt(1, 10000).setMin(0).setMax(1);
		p.add(highTaxField.getGuiComponent(), c);
		highTaxField.addNumberFieldListener(this);
		highTaxField.setValueD(demo.getTaxMaxProc());

		c.gridx = 0;
		c.gridy++;
		p.add(new JLabel("Welfare"), c);

		c.gridx++;
		WelfareModes[] m = WelfareModes.values();
		welfareModeCB = new JComboBox<>(m);
		welfareModeCB.setSelectedItem(demo.welfareMode);
		welfareModeCB.addActionListener(this);
		p.add(welfareModeCB, c);

		c.gridx = 0;
		c.gridy++;
		p.add(new JLabel("Ability boost"), c);
		c.gridx++;
		welfareAbilityBoostCB = new JCheckBox();
		welfareAbilityBoostCB.setSelected(demo.welfareAbilityBoost);
		p.add(welfareAbilityBoostCB, c);

//		c.gridy++;
//		c.weighty = 10;
//		p.add(Box.createGlue(), c);

		c.gridx = 0;
		c.gridy++;
		p.add(new JLabel("Ability"), c);
		c.gridy++;
		p.add(new JLabel("Start"), c);

		c.gridx++;
		abilityStartField = new NumberField().setColumns(6).setMode(NumberField.floatMode).setStepDefault(1, 100)
				.setStepShift(1, 1000).setStepAlt(1, 10000).setMin(0).setMax(10).addNumberFieldListener(this)
				.setValueD(demo.abilityStart);
		p.add(abilityStartField.getGuiComponent(), c);

		c.gridx = 0;
		c.gridy++;
		p.add(new JLabel("Delta"), c);

		c.gridx++;
		abilityDeltaField = new NumberField().setMin(demo.abilityMin).setColumns(6).setMode(NumberField.floatMode)
				.setStepDefault(1, 100).setStepShift(1, 1000).setStepAlt(1, 10000).setMin(0).setMax(1)
				.addNumberFieldListener(this).setValueD(demo.abilityDelta);
		p.add(abilityDeltaField.getGuiComponent(), c);

		c.gridx = 0;
		c.gridy++;
		p.add(new JLabel("Min"), c);

		c.gridx++;
		abilityMinField = new NumberField().setColumns(6).setMode(NumberField.intMode).setMin(0).setMax(10)
				.addNumberFieldListener(this).setValueD(demo.abilityMin);
		p.add(abilityMinField.getGuiComponent(), c);

		c.gridx = 0;
		c.gridy++;
		p.add(new JLabel("Max"), c);
		c.gridx++;
		abilityMaxField = new NumberField().setMin(0).setColumns(6).setMode(NumberField.intMode).setMax(10)
				.addNumberFieldListener(this).setValueD(demo.abilityMax);
		p.add(abilityMaxField.getGuiComponent(), c);

		c.gridx = 0;
		c.gridy++;

		p.add(new JLabel("Display"), c);
		c.gridx++;
		PaintModes[] pms = PaintModes.values();
		paintModeCB = new JComboBox<PaintModes>(pms);
		paintModeCB.setSelectedItem(paintMode);
		p.add(paintModeCB, c);
		paintModeCB.addActionListener(this);

		/*
		 * p.add(new JLabel("RNNTA")); redistNonNeedToAllCB = new JCheckBox();
		 * p.add(redistNonNeedToAllCB); redistNonNeedToAllCB.addActionListener(this);
		 * redistNonNeedToAllCB.setSelected(demo.isRedistNonNeedToAll());
		 */

		c.gridx = 0;
		c.gridy++;

		p.add(new JLabel("High tax lim"), c);

		c.gridx++;
		highTaxLimField = new NumberField().setColumns(4).setMode(NumberField.floatMode).setStepShift(10)
				.setStepAlt(100).setMin(1);
		p.add(highTaxLimField.getGuiComponent(), c);
		highTaxLimField.addNumberFieldListener(this);
		highTaxLimField.setValueD(demo.getTaxMaxProcLim());

		c.gridx = 0;
		c.gridy++;
		p.add(new JLabel("Sleep ms"), c);

		c.gridx++;
		sleepMsField = new NumberField().setColumns(6).setMode(NumberField.intMode).setStepDefault(1000)
				.setStepShift(100).setStepAlt(10).setMin(0);
		p.add(sleepMsField.getGuiComponent(), c);
		sleepMsField.addNumberFieldListener(this);
		sleepMsField.setValueD(demo.getSleepMs());

		c.gridy++;
		c.gridx = 0;

		startStopButton = new JButton(START);
		p.add(startStopButton, c);
		startStopButton.addActionListener(this);

		c.gridx++;
		resetButton = new JButton("Reset");
		p.add(resetButton, c);
		resetButton.addActionListener(this);

		c.gridx = 0;
		c.gridy++;

		p.add(new JLabel("Cash:"), c);

		c.gridy++;

		p.add(new JLabel("Lowest"), c);

		c.gridx++;
		minCashField = new JTextField(5);
		minCashField.setToolTipText("The lowest cash of any person");
		p.add(minCashField, c);

		c.gridx = 0;
		c.gridy++;
		p.add(new JLabel("Median"), c);

		c.gridx++;
		medianCashField = new JTextField(8);
		medianCashField.setToolTipText("Half has more, half has less. ");
		p.add(medianCashField, c);

		c.gridx = 0;
		c.gridy++;
		p.add(new JLabel("Highest"), c);

		c.gridx++;
		maxCashField = new JTextField(6);
		maxCashField.setToolTipText("The highest cash of any person");
		p.add(maxCashField, c);

		c.gridx = 0;
		c.gridy++;

		p.add(new JLabel("Of top 20%"), c);
		c.gridx++;
		top20ProcentRatioField = new JTextField(8);
		top20ProcentRatioField.setToolTipText("Cash of the richest 20%, as partition of all cash.");
		p.add(top20ProcentRatioField, c);

		c.gridx = 0;
		c.gridy++;
		p.add(new JLabel("Of bottom 20%"), c);
		c.gridx++;
		bottom20PercentRatioField = new JTextField(8);
		bottom20PercentRatioField.setToolTipText("Cash of the poorest 20%, as partition of all cash.");
		p.add(bottom20PercentRatioField, c);

		c.gridx = 0;
		c.gridy++;
		p.add(new JLabel("Need"), c);
		c.gridx++;
		needField = new JTextField(8);
		needField.setToolTipText("The sum of personal cash less than povertyLim over all persons. ");
		p.add(needField, c);

		c.gridx = 0;
		c.gridy++;
		p.add(new JLabel("Treasury"), c);

		c.gridx++;
		treasuryField = new JTextField(8);
		treasuryField.setToolTipText("The national treasury.");
		p.add(treasuryField, c);
		return p;
	}

	private JPanel buildPanel() {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(buildDisplayPanel());

		return p;
	}

	private Component buildDisplayPanel() {
		JPanel p = new JPanel();
		p.setBorder(BorderFactory.createTitledBorder("Display"));

		return p;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		synchronized (demo) {
			Collections.sort(demo.personsAll);
			Collections.sort(demo.personsActive);
			stats();

			g.drawLine(0, yBase, 1200, yBase);

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
					g.drawRect(x, yBase - n, width, n);
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

			assert f0 <= f1
					: "i=" + i + ", f0=" + f0 + ", f1=" + f1 + " at count " + assertCount + " in all=" + all.toString();
		}
	}

	private void paintWealthPerPerson(Graphics gr) {
		List<Person> all = demo.getAllList();
		int catCount = demo.getCategoryCount();
		int personsPerCategory = demo.getPersonsPerCategory();
		int width = 1200 / catCount;
		int x = 20;
		float abilityMax = personsPerCategory * demo.abilityMax;
		float abilityMin = personsPerCategory * demo.abilityMin;
		int pStart = 0;
		for (int cat = 0; cat < catCount; cat++) {
			boolean hasPersonX = false;
			int pEnd = pStart + personsPerCategory;
			float nf = 0;
			float abilityAcc = 0;
			for (int i = pStart; i < pEnd; i++) {
				Person p = all.get(i);
				nf += p.getCash();
				hasPersonX |= p == demo.personX;

				abilityAcc += p.getAbility();
			}
			int n = (int) nf;

			Color oldColor = gr.getColor();

			if (hasPersonX) {
				gr.setColor(Color.red);
			} else {
				float r = 0;// (abilityAcc - abilityMin) / (abilityMax - abilityMin);
				float g = (abilityAcc - abilityMin) / (abilityMax - abilityMin);
				float b = (abilityAcc - abilityMin) / (abilityMax - abilityMin);

//				System.out.println("New color: r" + r + ", g: " + g + ", b: " + b);
				Color c = new Color(r, g, b);
				gr.setColor(c);
			}

			gr.drawRect(x, yBase - n, width, n);
			gr.setColor(oldColor);

			pStart += personsPerCategory;
			x += width;
		}

		x += 20;
		gr.drawLine(x, yBase, x, 0);

		int x0 = x;
		int y = yBase;
		int x1 = x0 + 12;
		int n = 0;

		while (0 < y) {

			gr.drawLine(x0, y, x1, y);
			gr.drawString("" + n, x1 + 2, y + 4);
			n += 10;
			y -= 10 * personsPerCategory;

		}

	}

	public void setMinCash(float minCash) {
		minCashField.setText(Float.toString(minCash));
	}

	public void setMaxCash(float maxCash) {
		maxCashField.setText(Float.toString(maxCash));
	}

	public void setPersonCount(int size) {
		personCountField.setText(Integer.toString(size));
	}

	public void setPartitionOfRichest20(float q) {
		top20ProcentRatioField.setText(Float.toString(q));
	}

	public void setPartitionOfPoorest20(float q) {
		bottom20PercentRatioField.setText(Float.toString(q));
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
		} else if (source == welfareModeCB) {
			demo.welfareMode = (WelfareModes) welfareModeCB.getSelectedItem();
			switch (demo.welfareMode) {
			case Flat:
				welfareModeCB.setToolTipText("Distribute equally to all persons");
				break;
			case Charity:
				welfareModeCB.setToolTipText("Distribute equally to to all poor persons");
				break;
			case CharityMinimum:
				welfareModeCB.setToolTipText("Distribute equally to all persons");
				break;
			case CharityMinimumRDNTA:
				welfareModeCB.setToolTipText("Distribute equally to all persons");
				break;

			default:
				assert false;
				break;
			}
		} else if (source == welfareAbilityBoostCB) {
			demo.welfareAbilityBoost = welfareAbilityBoostCB.isSelected();
		}

		else {
			assert false : "unknown source";
		}

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
		} else if (nf == abilityDeltaField) {
			demo.abilityDelta = (float) nf.getDoubleValue();
		} else if (nf == abilityMaxField) {
			demo.abilityMax = nf.getIntValue();
		} else if (nf == abilityMinField) {
			demo.abilityMin = nf.getIntValue();
		} else if (nf == abilityStartField) {
			demo.abilityStart = (float) nf.getDoubleValue();
		}

		else {
			assert false : "unknown source";
		}

	}

	@Override
	public void enterPressed() {
		// TODO Auto-generated method stub

	}

	public void setMedianCash(float cash) {
		medianCashField.setText(Float.toString(cash));
	}

	/**
	 * Pre: lists must be sorted
	 */
	private void stats() {

		float minCash = demo.cashTotal;
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

		float n = demo.getCashOfRichest20Percent(demo.personsAll);
		float q = (n * 100) / demo.cashTotal;
		setPartitionOfRichest20(q);

		n = demo.getCashOfPoorest20Percent(demo.personsAll);
		q = (n * 100) / demo.cashTotal;
		setPartitionOfPoorest20(q);

		int i = demo.personCount / 2;
		setMedianCash(demo.personsAll.get(i).getCash());

	}

	public void setConstants(int personCount, int categoryCount, int personsPerCategory) {
		catCountField.setText(Integer.toString(categoryCount));
		personsPerCategoyField.setText(Integer.toString(personsPerCategory));
	}

}
