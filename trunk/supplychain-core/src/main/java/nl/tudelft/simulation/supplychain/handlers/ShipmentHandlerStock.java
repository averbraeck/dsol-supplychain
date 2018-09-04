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
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.stock.StockInterface;

/**
 * When a Shipment comes in, it just has to be added to the Stock. <br>
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
public class ShipmentHandlerStock extends SupplyChainHandler
{
	/** the serial version uid */
	private static final long serialVersionUID = 12L;

	/** access to the owner's stock to look at availability of products */
	protected StockInterface stock;

	/**
	 * Construct a new ShipmentHandlerStock handler.
	 * 
	 * @param owner the owner of the handler
	 * @param stock the stock to use for storing the incoming cargo
	 */
	public ShipmentHandlerStock(final SupplyChainActor owner,
			final StockInterface stock)
	{
		super(owner);
		this.stock = stock;
	}

    /** {@inheritDoc} */
    @Override
	public boolean handleContent(final Serializable content)
	{
		Shipment shipment = (Shipment) checkContent(content);
		if (!isValidContent(shipment))
		{
			return false;
		}
		// get the cargo from the shipment, and add its contents to the stock
		Product product = shipment.getProduct();
		double amount = shipment.getAmount();
		this.stock.addStock(product, amount, shipment.getTotalCargoValue());
		// update the administration
		this.stock.changeOrderedAmount(product, -amount);
		shipment.setInTransit(false);
		shipment.setDelivered(true);
		return true;
	}

    /** {@inheritDoc} */
    @Override
	protected boolean checkContentClass(final Serializable content)
	{
		return (content instanceof Shipment);
	}
}