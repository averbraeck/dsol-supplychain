package nl.tudelft.simulation.supplychain.banking;

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

/**
 * <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class BankAccountEditor extends AbstractCellEditor implements TableCellEditor
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
    public BankAccountEditor()
    {
        super();
        System.out.println("BankAccountEditor has been instantiated");
    }

    /**
     * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int,
     *      int)
     */
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
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);

        return this.cellPanel;
    }

    /**
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
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

        /**
         * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
         */
        public synchronized void actionPerformed(final ActionEvent event)
        {
            BankAccountEditor.this.value = this.chooser.getColor();
            BankAccountEditor.this.stopCellEditing();
            BankAccountEditor.this.cellPanel.setBackground(BankAccountEditor.this.value.darker());
            BankAccountEditor.this.cellPanel.paintImmediately(BankAccountEditor.this.cellPanel.getBounds());
        }
    }

    /**
     * The CancelListener
     */
    protected class CancelListener implements ActionListener
    {
        /**
         * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
         */
        public void actionPerformed(final ActionEvent e)
        {
            BankAccountEditor.this.cancelCellEditing();
        }
    }

}
