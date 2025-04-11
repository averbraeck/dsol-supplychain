package nl.tudelft.simulation.supplychain.role.directing;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.receiver.ContentReceiverDirect;
import nl.tudelft.simulation.supplychain.process.AutonomousProcess;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * DirectingRoleSelling contains the most important variables for sales for the organization. What profit margins do we use?
 * What products do we sell? In which markets?.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DirectingRoleSelling extends Role<DirectingRoleSelling>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the default profit margin for products that have not been registered. */
    private double defaultProfitMargin = 0.2;

    /** a map of the products we sell, each with their profit margin. */
    private final Map<Product, Double> productProfitMarginMap = new LinkedHashMap<>();

    /** an indication whether we sell to all landmasses. */
    private boolean salesToAllLandmasses = true;

    /** the landmasses to which we sell in case salesToAllLandmasses is false. */
    private final Set<String> salesToLandmass = new LinkedHashSet<>();

    /** the necessary content handlers. */
    private static Set<Class<? extends Content>> necessaryContentHandlers = Set.of();

    /** the necessary autonomous processes. */
    private static Set<Class<? extends AutonomousProcess<DirectingRoleSelling>>> necessaryAutonomousProcesses = Set.of();

    /**
     * Create a new Directing role for sales.
     * @param owner the actor that owns the Directing role
     */
    public DirectingRoleSelling(final DirectingActorSelling owner)
    {
        super("directing-selling", owner, new ContentReceiverDirect());
    }

    /**
     * Get the profit margin for a product, or the default profit margin when the product is not in the map.
     * @param product the product for which to look up the profit margin
     * @return the profit margin for a product, or NaN when not found
     */
    public double getProfitMargin(final Product product)
    {
        return this.productProfitMarginMap.containsKey(product) ? this.productProfitMarginMap.get(product)
                : this.defaultProfitMargin;
    }

    /**
     * Set the profit margin for a product.
     * @param product the product for which to set a (new) profit margin
     * @param profitMargin the (new) profit margin for the product
     */
    public void setProfitMargin(final Product product, final double profitMargin)
    {
        this.productProfitMarginMap.put(product, profitMargin);
    }

    /**
     * Remove the profit margin for a product. No error will be given if the product was not present.
     * @param product the product for which the profit margin will be removed
     */
    public void removeProfitMargin(final Product product)
    {
        this.productProfitMarginMap.remove(product);
    }

    /**
     * Return the default profit margin for non-registered products.
     * @return the default profit margin for non-registered products
     */
    public double getDefaultProfitMargin()
    {
        return this.defaultProfitMargin;
    }

    /**
     * Set a new the default profit margin for non-registered products.
     * @param defaultProfitMargin the new the default profit margin for non-registered products
     */
    public void setDefaultProfitMargin(final double defaultProfitMargin)
    {
        this.defaultProfitMargin = defaultProfitMargin;
    }

    /**
     * Return whether we do business on this landmass.
     * @param landmass the landmass to look up
     * @return whether we do business on this landmass
     */
    public boolean isSalesToLandmass(final String landmass)
    {
        return this.salesToAllLandmasses ? true : this.salesToLandmass.contains(landmass);
    }

    /**
     * Add this landmass for doing business. We turn salesToAllLandmasses automatically to false when data is entered into the
     * set of landmasses where we do business.
     * @param landmass the landmass to add for business
     */
    public void addSalesToLandmass(final String landmass)
    {
        this.salesToLandmass.add(landmass);
        this.salesToAllLandmasses = false;
    }

    /**
     * Remove this landmass for doing business. No error will be given if the landmass was not present. We do NOT turn
     * salesToAllLandmasses automatically to true in case the set is empty; one might want to stop sales for a while.
     * @param landmass the landmass to remove for business
     */
    public void removeSalesToLandmass(final String landmass)
    {
        this.salesToLandmass.remove(landmass);
    }

    /**
     * Return whether we sell to all landmasses.
     * @return whether we sell to all landmasses
     */
    public boolean isSalesToAllLandmasses()
    {
        return this.salesToAllLandmasses;
    }

    /**
     * Set whether we sell to all landmasses.
     * @param salesToAllLandmasses true when we sell to all landmasses
     */
    public void setSalesToAllLandmasses(final boolean salesToAllLandmasses)
    {
        this.salesToAllLandmasses = salesToAllLandmasses;
    }

    @Override
    protected Set<Class<? extends Content>> getNecessaryContentHandlers()
    {
        return necessaryContentHandlers;
    }

    @Override
    protected Set<Class<? extends AutonomousProcess<DirectingRoleSelling>>> getNecessaryAutonomousProcesses()
    {
        return necessaryAutonomousProcesses;
    }
}
