package nl.tudelft.simulation.supplychain.reference;

import javax.vecmath.Point3d;

import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.supplychain.banking.Bank;
import nl.tudelft.simulation.supplychain.contentstore.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.finance.MoneyUnit;

/**
 * Reference implementation for a DC. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DistributionCenter extends Retailer
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /**
     * @param name the name of the Distribution Center
     * @param simulator the simulator to use
     * @param position the position on the map
     * @param bank the bank
     * @param contentStore the contentStore for the messages
     */
    public DistributionCenter(final String name, final DEVSSimulatorInterface.TimeDoubleUnit simulator, final Point3d position,
            final Bank bank, final ContentStoreInterface contentStore)
    {
        this(name, simulator, position, bank, new Money(0.0, MoneyUnit.USD), contentStore);
    }

    /**
     * @param name the name of the Distribution Center
     * @param simulator the simulator to use
     * @param position the position on the map
     * @param bank the bank
     * @param initialBankAccount the initial bank balance
     * @param contentStore the contentStore for the messages
     */
    public DistributionCenter(final String name, final DEVSSimulatorInterface.TimeDoubleUnit simulator, final Point3d position,
            final Bank bank, final Money initialBankAccount, final ContentStoreInterface contentStore)
    {
        super(name, simulator, position, bank, initialBankAccount, contentStore);
    }
}
