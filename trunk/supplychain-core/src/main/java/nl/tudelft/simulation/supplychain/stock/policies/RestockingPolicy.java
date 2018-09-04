package nl.tudelft.simulation.supplychain.stock.policies;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEvent;
import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.jstats.streams.Java2Random;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.actor.Trader;
import nl.tudelft.simulation.supplychain.content.InternalDemand;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.stock.StockInterface;
import nl.tudelft.simulation.unit.simulator.DEVSSimulatorInterfaceUnit;

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
    protected DEVSSimulatorInterfaceUnit simulator;

    /** the stock for which the policy holds */
    protected StockInterface stock;

    /** the product that has to be restocked */
    protected Product product;

    /** the frquency distribution for restocking or checking the stock */
    protected DistContinuous frequency;

    /** the maximum delivery time */
    protected double maxDeliveryTime = 0.0;

    /** the logger. */
    private static Logger logger = LogManager.getLogger(RestockingPolicy.class);

    /**
     * Construct a new restocking policy, with the basic parameters that every restocking policy has.
     * @param stock the stock for which the policy holds
     * @param product the product that has to be restocked
     * @param frequency the frequency distribution for restocking or checking
     * @param maxDeliveryTime the maximum delivery time to use
     */
    public RestockingPolicy(final StockInterface stock, final Product product, final DistContinuous frequency,
            final double maxDeliveryTime)
    {
        super();
        this.simulator = stock.getOwner().getSimulator();
        this.stock = stock;
        this.product = product;
        this.frequency = frequency;
        this.maxDeliveryTime = maxDeliveryTime;
        try
        {
            SimEvent simEvent = new SimEvent(this.simulator.getSimulatorTime() + frequency.draw(), this, this, "checkLoop",
                    new Serializable[] {});
            this.simulator.scheduleEvent(simEvent);
        }
        catch (Exception e)
        {
            logger.fatal("RestockingPolicy", e);
        }
    }

    /**
     * Construct a new restocking policy, with the basic parameters that every restocking policy has.
     * @param stock the stock for which the policy holds
     * @param product the product that has to be restocked
     * @param frequency the constant frequency for restocking or checking
     * @param maxDeliveryTime the maximum delivery time to use
     */
    public RestockingPolicy(final StockInterface stock, final Product product, final double frequency,
            final double maxDeliveryTime)
    {
        this(stock, product, new DistConstant(new Java2Random(), frequency), maxDeliveryTime);
    }

    /**
     * The main loop for checking or refilling stock
     */
    protected void checkLoop()
    {
        checkStockLevel();
        try
        {
            SimEvent simEvent = new SimEvent(this.simulator.getSimulatorTime() + this.frequency.draw(), this, this, "checkLoop",
                    new Serializable[] {});
            this.simulator.scheduleEvent(simEvent);
        }
        catch (Exception e)
        {
            logger.fatal("checkLoop", e);
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
        Trader owner = this.stock.getOwner();
        InternalDemand internalDemand = new InternalDemand(owner, this.product, orderAmount, owner.getSimulatorTime(),
                owner.getSimulatorTime() + this.maxDeliveryTime);
        owner.sendContent(internalDemand, 0.0);
    }

    /**
     * @return Returns the frequency distribution.
     */
    public DistContinuous getFrequency()
    {
        return this.frequency;
    }

    /**
     * @param frequency The frequency distribution to set.
     */
    public void setFrequency(final DistContinuous frequency)
    {
        this.frequency = frequency;
    }

    /**
     * @param frequency The constant frequency to set.
     */
    public void setFrequency(final double frequency)
    {
        this.frequency = new DistConstant(new Java2Random(), frequency);
    }

    /**
     * @return Returns the product.
     */
    public Product getProduct()
    {
        return this.product;
    }
}
