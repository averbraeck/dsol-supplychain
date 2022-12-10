package nl.tudelft.simulation.supplychain.role.inventory;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Duration;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.supplychain.inventory.InventoryInterface;
import nl.tudelft.simulation.supplychain.message.trade.InternalDemand;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * Generic restocking service as the parent of different implementations. It contains the product, inventory, and interval for
 * checking the inventory levels or ordering.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class AbstractRestockingService implements RestockingServiceInterface
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the simulator on which to schedule. */
    private SCSimulatorInterface simulator;

    /** the inventory for which the service holds. */
    private InventoryInterface inventory;

    /** the product that has to be restocked. */
    private Product product;

    /** the frequency distribution for restocking or checking the inventory levels. */
    private DistContinuousDuration checkInterval;

    /** the maximum delivery time. */
    private Duration maxDeliveryDuration = Duration.ZERO;

    /**
     * Construct a new restocking service, with the basic parameters that every restocking service has.
     * @param inventory the inventory for which the service holds
     * @param product Product; the product that has to be restocked
     * @param checkInterval the distribution of the interval for restocking or checking
     * @param maxDeliveryDuration the maximum delivery time to use
     */
    public AbstractRestockingService(final InventoryInterface inventory, final Product product,
            final DistContinuousDuration checkInterval, final Duration maxDeliveryDuration)
    {
        this.simulator = inventory.getOwner().getSimulator();
        this.inventory = inventory;
        this.product = product;
        this.checkInterval = checkInterval;
        this.maxDeliveryDuration = maxDeliveryDuration;
        try
        {
            this.simulator.scheduleEventRel(checkInterval.draw(), this, this, "checkLoop", new Serializable[] {});
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
            this.simulator.scheduleEventRel(this.checkInterval.draw(), this, this, "checkLoop", new Serializable[] {});
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
     * Creates an internal demand order.
     * @param orderAmount the amount to order or manufacture
     */
    protected void createInternalDemand(final double orderAmount)
    {
        InventoryActorInterface owner = this.inventory.getOwner();
        InternalDemand internalDemand = new InternalDemand(owner, this.product, orderAmount, owner.getSimulatorTime(),
                owner.getSimulatorTime().plus(this.maxDeliveryDuration));
        owner.sendMessage(internalDemand, Duration.ZERO);
    }

    /**
     * @return the frequency distribution.
     */
    protected DistContinuousDuration getFrequency()
    {
        return this.checkInterval;
    }

    /**
     * @return the product.
     */
    protected Product getProduct()
    {
        return this.product;
    }

    /**
     * @return serialversionuid
     */
    protected static long getSerialversionuid()
    {
        return serialVersionUID;
    }

    /**
     * @return simulator
     */
    protected SCSimulatorInterface getSimulator()
    {
        return this.simulator;
    }

    /**
     * @return inventory
     */
    protected InventoryInterface getInventory()
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
