package nl.tudelft.simulation.supplychain.reference;

import java.util.ArrayList;
import java.util.List;

import org.djutils.draw.point.OrientedPoint2d;
import org.djutils.exceptions.Throw;

import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.content.Message;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;
import nl.tudelft.simulation.supplychain.finance.Bank;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.message.store.trade.TradeMessageStoreInterface;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.producing.ProducingRole;

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
    private ProducingRole producingRole;

    /**
     * @param id String, the unique id of the supplier
     * @param name String; the longer name of the supplier
     * @param model SupplyChainModelInterface; the model
     * @param location OrientedPoint2d; the location of the actor
     * @param locationDescription String; the location description of the actor (e.g., a city, country)
     * @param bank Bank; the bank for the BankAccount
     * @param initialBalance Money; the initial balance for the actor
     * @param messageStore TradeMessageStoreInterface; the message store for messages
     * @throws ActorAlreadyDefinedException when the actor was already registered in the model
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public Manufacturer(final String id, final String name, final SupplyChainModelInterface model,
            final OrientedPoint2d location, final String locationDescription, final Bank bank, final Money initialBalance,
            final TradeMessageStoreInterface messageStore) throws ActorAlreadyDefinedException
    {
        super(id, name, model, location, locationDescription, bank, initialBalance, messageStore);
    }

    /**
     * Return the producing role.
     * @return ProducingRole; the producing role
     */
    public ProducingRole getProducingRole()
    {
        return this.producingRole;
    }

    /**
     * Set the producing role.
     * @param producingRole ProducingRole; the new producing role
     */
    public void setProducingRole(final ProducingRole producingRole)
    {
        Throw.whenNull(this.producingRole, "producingRole cannot be null");
        Throw.when(this.producingRole != null, IllegalStateException.class, "producingRole already initialized");
        addRole(this.producingRole);
        this.producingRole = producingRole;
    }

    /** {@inheritDoc} */
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
