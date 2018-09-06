/*
 * @(#)FactoryEditor.java May 11, 2004
 * 
 * Copyright (c) 2003-2006 Delft University of Technology, Jaffalaan 5, 2628 BX
 * Delft, the Netherlands. All rights reserved.
 * 
 * See for project information <a href="http://www.simulation.tudelft.nl/">
 * www.simulation.tudelft.nl </a>.
 * 
 * The source code and binary code of this software is proprietary information
 * of Delft University of Technology.
 */

package nl.tudelft.simulation.supplychain.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import nl.tudelft.simulation.supplychain.banking.BankAccount;

/**
 * <br>
 * Copyright (c) 2003-2006 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="http://www.simulation.tudelft.nl/"> www.simulation.tudelft.nl </a>. The source code and
 * binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="http://www.tbm.tudelft.nl/webstaf/stijnh/index.htm">Stijn-Pieter van Houten </a>
 * @version $$Revision: 1.1 $$ $$Date: 2009/03/10 22:54:03 $$
 */
public class FactoryEditor extends AbstractCellEditor implements TableCellEditor
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the value */
    protected Color value;

    /** the cellPanel */
    protected JPanel cellPanel = new JPanel();

    /**
     * constructs a new
     */
    public FactoryEditor()
    {
        super();
        System.out.println("FactoryEditor has been instantiated");
    }

    /** {@inheritDoc} */
    @Override
    public Component getTableCellEditorComponent(final JTable table, final Object _value, final boolean isSelected,
            final int row, final int column)
    {
        System.out.println("class of value: " + _value.getClass());
        BankAccount bankAccount = (BankAccount) _value;

        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        panel.add(new JTextField("Value of account: " + bankAccount.getBalance()), BorderLayout.CENTER);

        // this.cellPanel.setBackground(((Color) value).darker());
        // this.value = (Color) value;
        // JColorChooser chooser = new JColorChooser((Color) value);
        // JDialog dialog = JColorChooser.createDialog(table, "Color selection",
        // false, chooser, new OKListener(chooser), new CancelListener());
        // dialog.setVisible(true);

        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);

        return this.cellPanel;
    }

    /** {@inheritDoc} */
    @Override
    public Object getCellEditorValue()
    {
        return null;
    }

    /**
     * The OK listener
     */
    protected class OKListener implements ActionListener
    {
        /** the color chooser */
        private JColorChooser chooser;

        /**
         * constructs a new OKListener
         * @param chooser the color chooser.
         */
        public OKListener(final JColorChooser chooser)
        {
            this.chooser = chooser;
        }

        /** {@inheritDoc} */
        @Override
        public synchronized void actionPerformed(final ActionEvent event)
        {
            FactoryEditor.this.value = this.chooser.getColor();
            FactoryEditor.this.stopCellEditing();
            FactoryEditor.this.cellPanel.setBackground(FactoryEditor.this.value.darker());
            FactoryEditor.this.cellPanel.paintImmediately(FactoryEditor.this.cellPanel.getBounds());
        }
    }

    /**
     * The CancelListener
     */
    protected class CancelListener implements ActionListener
    {
        /** {@inheritDoc} */
        @Override
        public void actionPerformed(final ActionEvent e)
        {
            FactoryEditor.this.cancelCellEditing();
        }
    }

}
