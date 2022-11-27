/*
 * @(#)Manufacturer.java Mar 3, 2004
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

package nl.tudelft.simulation.supplychain.reference;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.djutils.draw.point.OrientedPoint3d;

import nl.tudelft.simulation.supplychain.actor.capabilities.ProducerInterface;
import nl.tudelft.simulation.supplychain.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.supplychain.banking.Bank;
import nl.tudelft.simulation.supplychain.contentstore.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.finance.MoneyUnit;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.production.Production;
import nl.tudelft.simulation.supplychain.production.ProductionService;

/**
 * Reference implementation for a manufacturer. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class Manufacturer extends DistributionCenter implements ProducerInterface
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the production capabilities of this manufacturer */
    private Production production;

    /**
     * @param name the name of the manufacturer
     * @param simulator the simulator to use
     * @param position the position on the map
     * @param bank the bank
     * @param contentStore the contentStore for the messages
     */
    public Manufacturer(final String name, final SCSimulatorInterface simulator, final OrientedPoint3d position,
            final Bank bank, final ContentStoreInterface contentStore)
    {
        this(name, simulator, position, bank, new Money(0.0, MoneyUnit.USD), contentStore);
    }

    /**
     * @param name the name of the manufacturer
     * @param simulator the simulator to use
     * @param position the position on the map
     * @param bank the bank
     * @param initialBankAccount the initial bank balance
     * @param contentStore the contentStore for the messages
     */
    public Manufacturer(final String name, final SCSimulatorInterface simulator, final OrientedPoint3d position,
            final Bank bank, final Money initialBankAccount, final ContentStoreInterface contentStore)
    {
        super(name, simulator, position, bank, initialBankAccount, contentStore);
        this.production = new Production(this);
    }

    /** {@inheritDoc} */
    @Override
    public Production getProduction()
    {
        return this.production;
    }

    /**
     * Add a production service for the manufacturer
     * @param productionService the service to add
     */
    public void addProductionService(final ProductionService productionService)
    {
        this.production.addProductionService(productionService);
    }

    /**
     * @return the raw materials
     */
    public List<Product> getRawMaterials()
    {
        List<Product> rawMaterials = new ArrayList<Product>();
        Iterator<Product> it = super.stock.iterator();
        while (it.hasNext())
        {
            Product product = it.next();
            if (product.getBillOfMaterials().getMaterials().size() == 0)
            {
                rawMaterials.add(product);
            }
        }
        return rawMaterials;
    }

    /**
     * @return the end products
     */
    public List<Product> getEndProducts()
    {
        List<Product> endProducts = new ArrayList<Product>();
        Iterator<Product> it = super.stock.iterator();
        while (it.hasNext())
        {
            Product product = it.next();
            if (product.getBillOfMaterials().getMaterials().size() > 0)
            {
                endProducts.add(product);
            }
        }
        return endProducts;
    }
}
