package nl.tudelft.simulation.supplychain.reference;

import java.io.Serializable;

import javax.vecmath.Point3d;

import org.djunits.value.vdouble.scalar.Money;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.banking.Bank;
import nl.tudelft.simulation.supplychain.roles.BuyingRole;
import nl.tudelft.simulation.supplychain.roles.DemandGenerationRole;
import nl.tudelft.simulation.supplychain.roles.Role;
import nl.tudelft.simulation.unit.simulator.DEVSSimulatorInterfaceUnit;

/**
 * A Customer is an actor which usually orders (pull) products from a Distributor. However, its behavior depends on the type of
 * supply chain. Is it push or pull? <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class Customer extends SupplyChainActor
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** The role instance to generate demand */
    private DemandGenerationRole demandGenerationRole = null;

    /** The role to buy */
    private BuyingRole buyingRole = null;

    /**
     * @param name the name
     * @param simulator the simulator
     * @param position the position
     * @param bank the bank
     * @param initialBankAccount the initial bank account
     */
    public Customer(final String name, final DEVSSimulatorInterfaceUnit simulator, final Point3d position, final Bank bank,
            final Money initialBankAccount)
    {
        super(name, simulator, position, new Role[] {}, bank, initialBankAccount);
    }

    /**
     * @return Returns the buyingRole.
     */
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
    public DemandGenerationRole getDemandGenerationRole()
    {
        return this.demandGenerationRole;
    }

    /**
     * @param demandGenerationRole The demandGenerationRole to set.
     */
    public void setDemandGenerationRole(final DemandGenerationRole demandGenerationRole)
    {
        // remove the previous demand generation role
        if (this.demandGenerationRole != null)
        {
            super.removeRole(this.demandGenerationRole);
        }
        super.addRole(demandGenerationRole);
        this.demandGenerationRole = demandGenerationRole;
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleContent(final Serializable content)
    {
        if (this.demandGenerationRole == null || this.buyingRole == null)
        {
            throw new RuntimeException("DemandGenerationRole or buyingRole not initialized for Customer: " + this.getName());
        }
        return super.handleContent(content);
    }
}
