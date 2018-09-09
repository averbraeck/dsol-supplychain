/*
 * @(#)Supplier.java Mar 3, 2004
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

package nl.tudelft.simulation.supplychain.reference;

import javax.vecmath.Point3d;

import org.djunits.unit.MoneyUnit;
import org.djunits.value.vdouble.scalar.Money;

import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.supplychain.actor.Trader;
import nl.tudelft.simulation.supplychain.banking.Bank;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.roles.Role;

/**
 * Reference implementation for a Supplier. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class Supplier extends Trader
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /**
     * @param name the name of the supplier
     * @param simulator the simulator to use
     * @param position the position on the map
     * @param roles the initial roles (if any)
     * @param bank the bank
     */
    public Supplier(final String name, final DEVSSimulatorInterface.TimeDoubleUnit simulator, final Point3d position,
            final Role[] roles, final Bank bank)
    {
        this(name, simulator, position, roles, bank, new Money(0.0, MoneyUnit.USD));
    }

    /**
     * @param name the name of the supplier
     * @param simulator the simulator to use
     * @param position the position on the map
     * @param roles the initial roles (if any)
     * @param bank the bank
     * @param initialBankAccount the initial bank balance
     */
    public Supplier(final String name, final DEVSSimulatorInterface.TimeDoubleUnit simulator, final Point3d position, final Role[] roles,
            final Bank bank, final Money initialBankAccount)
    {
        super(name, simulator, position, roles, bank, initialBankAccount);
    }

    /** {@inheritDoc} */
    @Override
    public void checkStock(final Product product)
    {
        // TODO: implement checkStock
    }
}
