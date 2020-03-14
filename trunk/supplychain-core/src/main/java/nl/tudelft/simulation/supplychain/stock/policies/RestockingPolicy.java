package nl.tudelft.simulation.supplychain.stock.policies;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Duration;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.StockKeepingActor;
import nl.tudelft.simulation.supplychain.content.InternalDemand;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.stock.StockInterface;
import nl.tudelft.simulation.unit.dist.DistConstantDuration;

/**
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public abstract class RestockingPolicy implements Serializable
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the simulator on which to schedule */
    protected DEVSSimulatorInterface.TimeDoubleUnit simulator;

    /** the stock for which the policy holds */
    protected StockInterface stock;

    /** the product that has to be restocked */
    protected Product product;

    // TODO: See if this should be a Duration or a Frequency
    /** the frequency distribution for restocking or checking the stock */
    protected DistContinuousDuration frequency;

    /** the maximum delivery time */
    protected Duration maxDeliveryDuration = Duration.ZERO;

    /**
     * Construct a new restocking policy, with the basic parameters that every restocking policy has.
     * @param stock the stock for which the policy holds
     * @param product the product that has to be restocked
     * @param frequency the frequency distribution for restocking or checking
     * @param maxDeliveryDuration the maximum delivery time to use
     */
    public RestockingPolicy(final StockInterface stock, final Product product, final DistContinuousDuration frequency,
            final Duration maxDeliveryDuration)
    {
        super();
        this.simulator = stock.getOwner().getSimulator();
        this.stock = stock;
        this.product = product;
        this.frequency = frequency;
        this.maxDeliveryDuration = maxDeliveryDuration;
        try
        {
            this.simulator.scheduleEventRel(frequency.draw(), this, this, "checkLoop", new Serializable[] {});
        }
        catch (Exception e)
        {
            Logger.error(e, "RestockingPolicy");
        }
    }

    /**
     * Construct a new restocking policy, with the basic parameters that every restocking policy has.
     * @param stock the stock for which the policy holds
     * @param product the product that has to be restocked
     * @param frequency the constant frequency for restocking or checking
     * @param maxDeliveryDuration the maximum delivery time to use
     */
    public RestockingPolicy(final StockInterface stock, final Product product, final Duration frequency,
            final Duration maxDeliveryDuration)
    {
        this(stock, product, new DistConstantDuration(frequency), maxDeliveryDuration);
    }

    /**
     * The main loop for checking or refilling stock
     */
    protected void checkLoop()
    {
        checkStockLevel();
        try
        {
            this.simulator.scheduleEventRel(this.frequency.draw(), this, this, "checkLoop", new Serializable[] {});
        }
        catch (Exception e)
        {
            Logger.error(e, "checkLoop");
        }
    }

    /**
     * Checks the stock level and takes action if needed
     */
    protected abstract void checkStockLevel();

    /**
     * Creates an internal demand order
     * @param orderAmount the amount to order or manufacture
     */
    protected void createInternalDemand(final double orderAmount)
    {
        StockKeepingActor owner = this.stock.getOwner();
        InternalDemand internalDemand = new InternalDemand(owner, this.product, orderAmount, owner.getSimulatorTime(),
                owner.getSimulatorTime().plus(this.maxDeliveryDuration));
        owner.sendContent(internalDemand, Duration.ZERO);
    }

    /**
     * @return Returns the frequency distribution.
     */
    public DistContinuousDuration getFrequency()
    {
        return this.frequency;
    }

    /**
     * @param frequency The frequency distribution to set.
     */
    public void setFrequency(final DistContinuousDuration frequency)
    {
        this.frequency = frequency;
    }

    /**
     * @param frequency The constant frequency to set.
     */
    public void setFrequency(final Duration frequency)
    {
        this.frequency = new DistConstantDuration(frequency);
    }

    /**
     * @return Returns the product.
     */
    public Product getProduct()
    {
        return this.product;
    }
}
