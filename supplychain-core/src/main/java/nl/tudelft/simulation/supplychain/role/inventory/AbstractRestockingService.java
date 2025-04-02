package nl.tudelft.simulation.supplychain.role.inventory;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Duration;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.content.Demand;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulatorInterface;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * Generic restocking service as the parent of different implementations. It contains the product, inventory, and interval for
 * checking the inventory levels or ordering.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class AbstractRestockingService implements RestockingServiceInterface
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the simulator on which to schedule. */
    private SupplyChainSimulatorInterface simulator;

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
     * @param inventory the inventory for which the service holds
     * @param product the product that has to be restocked
     * @param checkInterval the distribution of the interval for restocking or checking
     * @param maxDeliveryDuration the maximum delivery time to use
     */
    public AbstractRestockingService(final Inventory inventory, final Product product,
            final DistContinuousDuration checkInterval, final Duration maxDeliveryDuration)
    {
        this.simulator = inventory.getOwner().getSimulator();
        this.inventory = inventory;
        this.product = product;
        this.checkInterval = checkInterval;
        this.maxDeliveryDuration = maxDeliveryDuration;
        try
        {
            this.simulator.scheduleEventRel(checkInterval.draw(), this, "checkLoop", new Serializable[] {});
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
            this.simulator.scheduleEventRel(this.checkInterval.draw(), this, "checkLoop", new Serializable[] {});
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
        Actor owner = this.inventory.getOwner();
        Demand demand = new Demand(owner, this.product, orderAmount, owner.getSimulatorTime(),
                owner.getSimulatorTime().plus(this.maxDeliveryDuration));
        owner.sendContent(demand, Duration.ZERO);
    }

    /**
     * @return the frequency distribution.
     */
    protected DistContinuousDuration getFrequency()
    {
        return this.checkInterval;
    }

    @Override
    public Product getProduct()
    {
        return this.product;
    }

    /**
     * @return simulator
     */
    protected SupplyChainSimulatorInterface getSimulator()
    {
        return this.simulator;
    }

    @Override
    public Inventory getInventory()
    {
        return this.inventory;
    }

    /**
     * @return checkInterval
     */
    protected DistContinuousDuration getCheckInterval()
    {
        return this.checkInterval;
    }

    /**
     * @return maxDeliveryDuration
     */
    protected Duration getMaxDeliveryDuration()
    {
        return this.maxDeliveryDuration;
    }

}
