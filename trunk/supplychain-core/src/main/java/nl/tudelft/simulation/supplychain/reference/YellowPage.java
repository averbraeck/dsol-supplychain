package nl.tudelft.simulation.supplychain.reference;

import javax.vecmath.Point3d;

import org.djunits.value.vdouble.scalar.Money;

import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.supplychain.banking.Bank;
import nl.tudelft.simulation.supplychain.contentstore.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.yellowpage.SupplyChainYellowPage;

/**
 * YellowPage.java. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class YellowPage extends SupplyChainYellowPage
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * @param name
     * @param simulator
     * @param position
     * @param bank
     * @param contentStore
     */
    public YellowPage(String name, DEVSSimulatorInterface.TimeDoubleUnit simulator, Point3d position, Bank bank,
            ContentStoreInterface contentStore)
    {
        super(name, simulator, position, bank, contentStore);
    }

    /**
     * @param name
     * @param simulator
     * @param position
     * @param bank
     * @param initialBankBalance
     * @param contentStore
     */
    public YellowPage(String name, DEVSSimulatorInterface.TimeDoubleUnit simulator, Point3d position, Bank bank,
            Money initialBankBalance, ContentStoreInterface contentStore)
    {
        super(name, simulator, position, bank, initialBankBalance, contentStore);
    }

}
