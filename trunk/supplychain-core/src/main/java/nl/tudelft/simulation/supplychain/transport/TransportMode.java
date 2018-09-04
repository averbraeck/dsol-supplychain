package nl.tudelft.simulation.supplychain.transport;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.djunits.unit.LengthUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Mass;
import org.djunits.value.vdouble.scalar.Speed;

import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;

/**
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class TransportMode implements Serializable
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** description of the transport mode */
    protected String name;

    /** the fixed time for loading and unloading in hours */
    protected Duration fixedDuration;

    /** average transportation speed in kilometers per hours */
    protected Speed speed;

    /** the fixed cost for a transport */
    protected double fixedCost;

    /** the cost per km per kg */
    protected double costKmKg;

    /** the logger. */
    private static Logger logger = LogManager.getLogger(TransportMode.class);

    /**
     * predefined transport mode: plane including transport to/from the airport 250 dollar handling costs, taxes, and transport
     * to/from airport. For the price of 0.0007 per kg per km, see conf/data/AirRates.xls
     */
    public static final TransportMode PLANE = new TransportMode("Plane", 48.0, 800, 250, 0.0007);

    /**
     * Constructor for TransportMode.
     * @param name the name of the transport mode
     * @param fixedTime the fixed time for loading and unloading in hrs
     * @param speed the average transportation speed of the mode in km/hr
     * @param fixedCost the fixed costs
     * @param costKmKg the cost per km and kg
     */
    public TransportMode(final String name, final Duration fixedTime, final Speed speed, final double fixedCost,
            final double costKmKg)
    {
        super();
        this.name = name;
        this.fixedDuration = fixedTime;
        this.speed = speed;
        this.fixedCost = fixedCost;
        this.costKmKg = costKmKg;
    }

    /**
     * @param loc1 the origin
     * @param loc2 the destination
     * @return the transportation time in hours
     */
    public Duration transportTime(final Locatable loc1, final Locatable loc2)
    {
        Length distance;
        try
        {
            // TODO: assume km for now...
            distance = new Length(loc1.getLocation().distance(loc2.getLocation()), LengthUnit.KILOMETER);
        }
        catch (Exception exception)
        {
            logger.fatal("transportTime", exception);
            return Duration.ZERO;
        }
        return this.transportTime(distance);
    }

    /**
     * @param actor1 the first actor
     * @param actor2 the second actor
     * @return the transportation time in hours
     */
    public Duration transportTime(final SupplyChainActor actor1, final SupplyChainActor actor2)
    {
        Length distance = actor1.calculateDistance(actor2);
        return this.transportTime(distance);
    }

    /**
     * @param distance the distance in kilometers
     * @return the transportation time in hours
     */
    public Duration transportTime(final Length distance)
    {
        return this.fixedDuration.plus(distance.divideBy(this.speed));
    }

    /**
     * calculate the transport costs for weight and distance
     * @param distance the distance in kms
     * @param weight the weight in kgs
     * @return the costs for transportation
     */
    public double transportCosts(final Length distance, final Mass weight)
    {
        return this.fixedCost + this.costKmKg * distance * weight;
    }

    /**
     * calculate the transport costs for weight and distance per unit
     * @param distance the distance
     * @param unitWeight the weight
     * @return the costs for transportation per unit weight
     */
    public double transportCostsPerUnit(final Length distance, final Mass unitWeight)
    {
        return this.costKmKg * distance * unitWeight;
    }

    /**
     * calculate the transport costs for weight and distance
     * @param actor1 the first actor
     * @param actor2 the second actor
     * @param weight the weight in kgs
     * @return the costs for transportation
     */
    public double transportCosts(final SupplyChainActor actor1, final SupplyChainActor actor2, final Mass weight)
    {
        return this.transportCosts(actor1.calculateDistance(actor2), weight);
    }

    /**
     * @return returns the fixed costs
     */
    public double getFixedCost()
    {
        return this.fixedCost;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return this.name;
    }
}
