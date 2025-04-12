package nl.tudelft.simulation.supplychain.role.warehousing.process;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Duration;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.content.Demand;
import nl.tudelft.simulation.supplychain.process.AutonomousProcess;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.warehousing.Inventory;
import nl.tudelft.simulation.supplychain.role.warehousing.WarehousingRole;

/**
 * Generic restocking service as the parent of different implementations. It contains the product, inventory, and interval for
 * checking the inventory levels or ordering.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class RestockingProcess extends AutonomousProcess<WarehousingRole> implements Serializable
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the inventory for which the service holds. */
    private Inventory inventory;

    /** the product that has to be restocked. */
    private Product product;

    /** the frequency distribution for restocking or checking the inventory levels. */
    private DistContinuousDuration checkInterval;

    /** the maximum delivery time. */
    private Duration maxDeliveryDuration = Duration.ZERO;

    /**
     * Construct a new restocking service, with the basic parameters that every restocking service has.
     * @param role the warehousing role to which the restocking process belongs
     * @param inventory the inventory for which the service holds
     * @param product the product that has to be restocked
     * @param checkInterval the distribution of the interval for restocking or checking
     * @param maxDeliveryDuration the maximum delivery time to use
     */
    public RestockingProcess(final WarehousingRole role, final Inventory inventory, final Product product,
            final DistContinuousDuration checkInterval, final Duration maxDeliveryDuration)
    {
        super(role);
        this.inventory = inventory;
        this.product = product;
        this.checkInterval = checkInterval;
        this.maxDeliveryDuration = maxDeliveryDuration;
        try
        {
            getSimulator().scheduleEventRel(checkInterval.draw(), this, "checkLoop", new Serializable[] {});
        }
        catch (Exception e)
        {
            Logger.error(e, "RestockingService");
        }
    }

    /**
     * The main loop for checking or refilling inventory.
     */
    protected void checkLoop()
    {
        checkInventoryLevel();
        try
        {
            getSimulator().scheduleEventRel(this.checkInterval.draw(), this, "checkLoop", new Serializable[] {});
        }
        catch (Exception e)
        {
            Logger.error(e, "checkLoop");
        }
    }

    /**
     * Check the inventory level and take action if needed.
     */
    protected abstract void checkInventoryLevel();

    /**
     * Creates an demand order.
     * @param orderAmount the amount to order or manufacture
     */
    protected void createDemand(final double orderAmount)
    {
        Actor owner = this.inventory.getActor();
        Demand demand = new Demand(owner, this.product, orderAmount, getSimulatorTime(),
                getSimulatorTime().plus(this.maxDeliveryDuration));
        owner.sendContent(demand, Duration.ZERO);
    }

    /**
     * Return the product for which this is the restocking service.
     * @return the product for which this is the restocking service
     */
    public Product getProduct()
    {
        return this.product;
    }

    /**
     * Return the inventory that needs to be checked for restocking.
     * @return the inventory that needs to be checked for restocking
     */
    public Inventory getInventory()
    {
        return this.inventory;
    }

    /**
     * Return the frequency distribution for restocking or checking the inventory levels.
     * @return the frequency distribution for restocking or checking the inventory levels
     */
    protected DistContinuousDuration getCheckInterval()
    {
        return this.checkInterval;
    }

    /**
     * Return the maximum delivery time.
     * @return the maximum delivery time
     */
    protected Duration getMaxDeliveryDuration()
    {
        return this.maxDeliveryDuration;
    }

}
