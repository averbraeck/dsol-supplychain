package nl.tudelft.simulation.supplychain.reference;

import java.io.Serializable;

import javax.vecmath.Point3d;

import org.djunits.unit.MoneyUnit;
import org.djunits.value.vdouble.scalar.Money;

import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.supplychain.actor.Trader;
import nl.tudelft.simulation.supplychain.banking.Bank;
import nl.tudelft.simulation.supplychain.content.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.roles.BuyingRole;
import nl.tudelft.simulation.supplychain.roles.SellingRole;

/**
 * Reference implementation for a Retailer. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class Retailer extends Trader
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** The role to buy */
    private BuyingRole buyingRole = null;

    /** The role to sell */
    private SellingRole sellingRole = null;

    /**
     * @param name the name of the retailer
     * @param simulator the simulator to use
     * @param position the position on the map
     * @param bank the bank
     * @param contentStore the contentStore for the messages
     */
    public Retailer(final String name, final DEVSSimulatorInterface.TimeDoubleUnit simulator, final Point3d position,
            final Bank bank, final ContentStoreInterface contentStore)
    {
        this(name, simulator, position, bank, new Money(0.0, MoneyUnit.USD), contentStore);
    }

    /**
     * @param name the name of the retailer
     * @param simulator the simulator to use
     * @param position the position on the map
     * @param bank the bank
     * @param initialBankAccount the initial bank balance
     * @param contentStore the contentStore for the messages
     */
    public Retailer(final String name, final DEVSSimulatorInterface.TimeDoubleUnit simulator, final Point3d position,
            final Bank bank, final Money initialBankAccount, final ContentStoreInterface contentStore)
    {
        super(name, simulator, position, bank, initialBankAccount, contentStore);
    }

    /** {@inheritDoc} */
    @Override
    public void checkStock(final Product product)
    {
        // TODO: to implement...
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
     * @return Returns the sellingRole.
     */
    public SellingRole getSellingRole()
    {
        return this.sellingRole;
    }

    /**
     * @param sellingRole The sellingRole to set.
     */
    public void setSellingRole(final SellingRole sellingRole)
    {
        // remove the previous selling role
        if (this.sellingRole != null)
        {
            super.removeRole(this.sellingRole);
        }
        super.addRole(sellingRole);
        this.sellingRole = sellingRole;
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleContent(final Serializable content)
    {
        if (this.buyingRole == null || this.sellingRole == null)
        {
            throw new RuntimeException("buyingRole or sellingRole not initialized for Retailer: " + this.getName());
        }
        return super.handleContent(content);
    }
}
