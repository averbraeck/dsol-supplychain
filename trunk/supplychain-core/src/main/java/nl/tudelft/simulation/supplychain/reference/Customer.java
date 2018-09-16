package nl.tudelft.simulation.supplychain.reference;

import java.io.Serializable;

import javax.vecmath.Point3d;

import org.djunits.value.vdouble.scalar.Money;

import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.actor.capabilities.BuyerInterface;
import nl.tudelft.simulation.supplychain.banking.Bank;
import nl.tudelft.simulation.supplychain.contentstore.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.demand.DemandGeneration;
import nl.tudelft.simulation.supplychain.roles.BuyingRole;

/**
 * A Customer is an actor which usually orders (pulls) products from a Distributor. <br>
 * TODO: implement push processes, such as when VMI (Vendor Managed Inventory) is used. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class Customer extends SupplyChainActor implements BuyerInterface
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** The role instance to generate demand */
    private DemandGeneration demandGeneration = null;

    /** The role to buy */
    private BuyingRole buyingRole = null;

    /**
     * @param name the name
     * @param simulator the simulator
     * @param position the position
     * @param bank the bank
     * @param initialBankAccount the initial bank account
     * @param contentStore the contentStore for the messages
     */
    public Customer(final String name, final DEVSSimulatorInterface.TimeDoubleUnit simulator, final Point3d position,
            final Bank bank, final Money initialBankAccount, final ContentStoreInterface contentStore)
    {
        super(name, simulator, position, bank, initialBankAccount, contentStore);
    }

    /**
     * @param name the name
     * @param simulator the simulator
     * @param position the position
     * @param bank the bank
     * @param contentStore the contentStore for the messages
     */
    public Customer(final String name, final DEVSSimulatorInterface.TimeDoubleUnit simulator, final Point3d position,
            final Bank bank, final ContentStoreInterface contentStore)
    {
        super(name, simulator, position, bank, contentStore);
    }

    /** {@inheritDoc} */
    @Override
    public BuyingRole getBuyingRole()
    {
        return this.buyingRole;
    }

    /**
     * @param buyingRole The buyingRole to set.
     */
    public void setBuyingRole(final BuyingRole buyingRole)
    {
        // remove the previous buying role
        if (this.buyingRole != null)
        {
            super.removeRole(this.buyingRole);
        }
        super.addRole(buyingRole);
        this.buyingRole = buyingRole;
    }

    /**
     * @return Returns the demandGenerationRole.
     */
    public DemandGeneration getDemandGeneration()
    {
        return this.demandGeneration;
    }

    /**
     * @param demandGenerationRole The demandGenerationRole to set.
     */
    public void setDemandGeneration(final DemandGeneration demandGenerationRole)
    {
        this.demandGeneration = demandGenerationRole;
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleContent(final Serializable content)
    {
        if (this.demandGeneration == null || this.buyingRole == null)
        {
            throw new RuntimeException("DemandGeneration or buyingRole not initialized for Customer: " + this.getName());
        }
        return super.handleContent(content);
    }
}
