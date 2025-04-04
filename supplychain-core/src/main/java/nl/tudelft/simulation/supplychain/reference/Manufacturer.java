package nl.tudelft.simulation.supplychain.reference;

import java.util.ArrayList;
import java.util.List;

import org.djutils.draw.point.DirectedPoint2d;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.content.Message;
import nl.tudelft.simulation.supplychain.content.store.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.money.Bank;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.manufacturing.ManufacturingRole;

/**
 * Reference implementation for a manufacturer.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Manufacturer extends DistributionCenter
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** the production capabilities of this manufacturer. */
    private ManufacturingRole producingRole;

    /**
     * @param id String, the unique id of the supplier
     * @param name the longer name of the supplier
     * @param model the model
     * @param location the location of the actor
     * @param locationDescription the location description of the actor (e.g., a city, country)
     * @param bank the bank for the BankAccount
     * @param initialBalance the initial balance for the actor
     * @param messageStore the message store for messages
     * @throws ActorAlreadyDefinedException when the actor was already registered in the model
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public Manufacturer(final String id, final String name, final SupplyChainModelInterface model,
            final DirectedPoint2d location, final String locationDescription, final Bank bank, final Money initialBalance,
            final ContentStoreInterface messageStore) throws ActorAlreadyDefinedException
    {
        super(id, name, model, location, locationDescription, bank, initialBalance, messageStore);
    }

    /**
     * Return the producing role.
     * @return the producing role
     */
    public ManufacturingRole getProducingRole()
    {
        return this.producingRole;
    }

    /**
     * Set the producing role.
     * @param producingRole the new producing role
     */
    public void setProducingRole(final ManufacturingRole producingRole)
    {
        Throw.whenNull(this.producingRole, "producingRole cannot be null");
        Throw.when(this.producingRole != null, IllegalStateException.class, "producingRole already initialized");
        addRole(this.producingRole);
        this.producingRole = producingRole;
    }

    @Override
    public void receiveMessage(final Message message)
    {
        Throw.whenNull(this.producingRole, "ProducingRole not initialized for actor: " + this.getName());
        super.receiveMessage(message);
    }

    /**
     * @return the raw materials
     */
    public List<Product> getRawMaterials()
    {
        List<Product> rawMaterials = new ArrayList<Product>();
        for (Product product : getInventoryRole().getInventory().getProducts())
        {
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
        for (Product product : getInventoryRole().getInventory().getProducts())
        {
            if (product.getBillOfMaterials().getMaterials().size() > 0)
            {
                endProducts.add(product);
            }
        }
        return endProducts;
    }

}
