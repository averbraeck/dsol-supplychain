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

import javax.vecmath.Point3d;

import nl.tudelft.simulation.supplychain.banking.Bank;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.production.Production;
import nl.tudelft.simulation.supplychain.production.ProductionService;
import nl.tudelft.simulation.supplychain.roles.Role;
import nl.tudelft.simulation.unit.simulator.DEVSSimulatorInterfaceUnit;

/**
 * Reference implementation for a manufacturer. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class Manufacturer extends DistributionCenter
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the production capabilities of this manufacturer */
    private Production production;

    /**
     * @param name the name of the manufacturer
     * @param simulator the simulator to use
     * @param position the position on the map
     * @param roles the initial roles (if any)
     * @param bank the bank
     */
    public Manufacturer(final String name, final DEVSSimulatorInterfaceUnit simulator, final Point3d position,
            final Role[] roles, final Bank bank)
    {
        this(name, simulator, position, roles, bank, 0.0);
    }

    /**
     * @param name the name of the manufacturer
     * @param simulator the simulator to use
     * @param position the position on the map
     * @param roles the initial roles (if any)
     * @param bank the bank
     * @param initialBankAccount the initial bank balance
     */
    public Manufacturer(final String name, final DEVSSimulatorInterfaceUnit simulator, final Point3d position,
            final Role[] roles, final Bank bank, final double initialBankAccount)
    {
        super(name, simulator, position, roles, bank, initialBankAccount);
        this.production = new Production(this);
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
