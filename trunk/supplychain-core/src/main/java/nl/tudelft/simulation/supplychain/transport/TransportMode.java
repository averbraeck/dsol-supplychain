package nl.tudelft.simulation.supplychain.transport;

import java.io.Serializable;

import org.djunits.unit.DurationUnit;
import org.djunits.unit.LengthUnit;
import org.djunits.unit.MassUnit;
import org.djunits.unit.SpeedUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Mass;
import org.djunits.value.vdouble.scalar.Speed;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.finance.MoneyUnit;

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
    protected Money fixedCost;

    /** the cost per km per kg */
    protected Money costKmKg;

    /**
     * predefined transport mode: plane including transport to/from the airport 250 dollar handling costs, taxes, and transport
     * to/from airport. For the price of 0.0007 per kg per km, see conf/data/AirRates.xls
     */
    public static final TransportMode PLANE = new TransportMode("Plane", new Duration(8.0, DurationUnit.HOUR),
            new Speed(800.0, SpeedUnit.KM_PER_HOUR), new Money(250.0, MoneyUnit.USD), new Money(0.0007, MoneyUnit.USD));

    /**
     * predefined transport mode: truck including transport to/from the airport 50 dollar handling costs, taxes
     * The price is $1.50 per km, with 10000 kg on board -> 0.00015 $/km/kg.
     */
    public static final TransportMode TRUCK = new TransportMode("Truck", new Duration(2.0, DurationUnit.HOUR),
            new Speed(80.0, SpeedUnit.KM_PER_HOUR), new Money(50.0, MoneyUnit.USD), new Money(0.00015, MoneyUnit.USD));

    /**
     * Constructor for TransportMode.
     * @param name the name of the transport mode
     * @param fixedTime the fixed time for loading and unloading in hrs
     * @param speed the average transportation speed of the mode in km/hr
     * @param fixedCost the fixed costs
     * @param costKmKg the cost per km and kg
     */
    public TransportMode(final String name, final Duration fixedTime, final Speed speed, final Money fixedCost,
            final Money costKmKg)
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
            Logger.error(exception, "transportTime");
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
        return this.fixedDuration.plus(distance.divide(this.speed));
    }

    /**
     * calculate the transport costs for weight and distance
     * @param distance the distance in kms
     * @param weight the weight in kgs
     * @return the costs for transportation
     */
    public Money transportCosts(final Length distance, final Mass weight)
    {
        return this.fixedCost
                .plus(this.costKmKg.multiplyBy(distance.getInUnit(LengthUnit.KILOMETER) * weight.getInUnit(MassUnit.KILOGRAM)));
    }

    /**
     * calculate the transport costs for weight and distance per unit
     * @param distance the distance
     * @param unitWeight the weight
     * @return the costs for transportation per unit weight
     */
    public Money transportCostsPerUnit(final Length distance, final Mass unitWeight)
    {
        return this.costKmKg.multiplyBy(distance.getInUnit(LengthUnit.KILOMETER) * unitWeight.getInUnit(MassUnit.KILOGRAM));
    }

    /**
     * calculate the transport costs for weight and distance
     * @param actor1 the first actor
     * @param actor2 the second actor
     * @param weight the weight in kgs
     * @return the costs for transportation
     */
    public Money transportCosts(final SupplyChainActor actor1, final SupplyChainActor actor2, final Mass weight)
    {
        return this.transportCosts(actor1.calculateDistance(actor2), weight);
    }

    /**
     * @return returns the fixed costs
     */
    public Money getFixedCost()
    {
        return this.fixedCost;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return this.name;
    }
}
