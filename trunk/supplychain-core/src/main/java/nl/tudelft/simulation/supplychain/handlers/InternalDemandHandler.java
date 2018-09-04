package nl.tudelft.simulation.supplychain.handlers;

import java.io.Serializable;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.InternalDemand;
import nl.tudelft.simulation.supplychain.stock.StockInterface;
import nl.tudelft.simulation.unit.dist.DistContinuousDurationUnit;

/**
 * The abstract InternalDemandHandler class provides the general methods that
 * all InternalDemandHandler classes need, such as checking whether the message
 * is really an InternalDemand. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public abstract class InternalDemandHandler extends SupplyChainHandler
{
	/** */
    private static final long serialVersionUID = 1L;

    /** the handling time distribution to handle internal demand */
	protected DistContinuousDurationUnit handlingTime;

	/** the stock for changing 'ordered amount' */
	protected StockInterface stock;

	/**
	 * Construct a new InternalDemandHandler.
	 * 
	 * @param owner the SupplyChainActor that has this policy.
	 * @param handlingTime the distribution of the time to handle an internal
	 *        demand
	 * @param stock the stock for being able to change the ordered amount
	 */
	public InternalDemandHandler(final SupplyChainActor owner,
			final DistContinuousDurationUnit handlingTime, final StockInterface stock)
	{
		super(owner);
		this.handlingTime = handlingTime;
		this.stock = stock;
	}

	/**
	 * @param handlingTime The handlingTime to set.
	 */
	public void setHandlingTime(final DistContinuousDurationUnit handlingTime)
	{
		this.handlingTime = handlingTime;
	}

    /** {@inheritDoc} */
    @Override
	public abstract boolean handleContent(final Serializable content);

    /** {@inheritDoc} */
    @Override
	protected boolean checkContentClass(final Serializable content)
	{
		return (content instanceof InternalDemand);
	}
}
