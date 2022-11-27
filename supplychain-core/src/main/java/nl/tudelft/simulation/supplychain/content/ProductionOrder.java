package nl.tudelft.simulation.supplychain.content;

import java.io.Serializable;

import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.supplychain.actor.StockKeepingActor;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.finance.MoneyUnit;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * An ProductionOrder indicates: I want to produce a certain amount of products on a certain date. The attributes "product",
 * "amount", and "date" make up the production order. <br>
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ProductionOrder extends Content
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the internal date the product should be ready */
    private Time dateReady;

    /** the product we want */
    private Product product;

    /** the amount of the product, in units for that product */
    private double amount;

    /** the accumulated costs for gathered raw materials */
    private Money materialCost = new Money(0.0, MoneyUnit.USD);

    /**
     * The constructor for the ProductionOrder
     * @param owner the producer of the products
     * @param internalDemandID the internal demand for this order
     * @param dateReady the internal date the product should be ready
     * @param product the product that has to be produced
     * @param amount the amount of products to be produced, in the product's units
     */
    public ProductionOrder(final StockKeepingActor owner, final Serializable internalDemandID, final Time dateReady,
            final Product product, final double amount)
    {
        super(owner, owner, internalDemandID);
        this.dateReady = dateReady;
        this.product = product;
        this.amount = amount;
    }

    /** {@inheritDoc} */
    @Override
    public Product getProduct()
    {
        return this.product;
    }

    /**
     * @return Returns the amount.
     */
    public double getAmount()
    {
        return this.amount;
    }

    /**
     * @return Returns the the internal date the product should be ready.
     */
    public Time getDateReady()
    {
        return this.dateReady;
    }

    /**
     * @return Returns the materialCost.
     */
    public Money getMaterialCost()
    {
        return this.materialCost;
    }

    /**
     * @param cost The cost of materials to add.
     */
    public void addMaterialCost(final Money cost)
    {
        this.materialCost = this.materialCost.plus(cost);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return super.toString() + ", for " + this.getAmount() + " units of product " + this.getProduct().getName();
    }
}
