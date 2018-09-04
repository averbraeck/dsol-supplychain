/*
 * @(#)ShipmentHandlerStock.java Mar 13, 2004
 * 
 * Copyright (c) 2003-2006 Delft University of Technology, Jaffalaan 5, 2628 BX
 * Delft, the Netherlands. All rights reserved.
 * 
 * See for project information <a href="http://www.simulation.tudelft.nl/">
 * www.simulation.tudelft.nl </a>.
 * 
 * The source code and binary code of this software is proprietary information
 * of Delft University of Technology.
 */

package nl.tudelft.simulation.supplychain.handlers;

import java.io.Serializable;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.content.Shipment;

/**
 * When a Shipment comes in, consume it. In other words and in terms of the
 * supply chain simultion: do nothing... <br>
 * <br>
 * Copyright (c) 2003-2006 Delft University of Technology, Jaffalaan 5, 2628 BX
 * Delft, the Netherlands. All rights reserved.
 * 
 * See for project information <a href="http://www.simulation.tudelft.nl/">
 * www.simulation.tudelft.nl </a>.
 * 
 * The source code and binary code of this software is proprietary information
 * of Delft University of Technology.
 * 
 * @author <a
 *         href="http://www.tbm.tudelft.nl/webstaf/alexandv/index.htm">Alexander
 *         Verbraeck </a>
 * @version $$Revision: 1.1 $$ $$Date: 2009/03/10 22:54:03 $$
 */
public class ShipmentHandlerConsume extends SupplyChainHandler
{
	/** the serial version uid */
	private static final long serialVersionUID = 12L;

	/**
	 * Construct a new ShipmentHandlerConsume handler.
	 * 
	 * @param owner the owner of the handler
	 */
	public ShipmentHandlerConsume(final SupplyChainActor owner)
	{
		super(owner);
	}

	/**
	 * Do nothing with the incoming cargo. <br>
     * {@inheritDoc} */
    @Override
	public boolean handleContent(final Serializable content)
	{
		Shipment shipment = (Shipment) checkContent(content);
		if (!isValidContent(shipment))
		{
			return false;
		}
		shipment.setInTransit(false);
		shipment.setDelivered(true);
		// do nothing
		return true;
	}

    /** {@inheritDoc} */
    @Override
	protected boolean checkContentClass(final Serializable content)
	{
		return (content instanceof Shipment);
	}
}