package nl.tudelft.simulation.supplychain.transporting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.djutils.base.Identifiable;
import org.djutils.exceptions.Throw;
import org.djutils.immutablecollections.ImmutableArrayList;
import org.djutils.immutablecollections.ImmutableList;

/**
 * StochasticTransportOption describes a way to get goods from A to B, with stochastically described times and costs. The class
 * can incicate a singular transport mode that transports the goods from A to B, e.g., trucking, or a multimodal option that
 * involves, e.g., a truck to the Port, a containrship to another port, and trucking to the final destination. Each of the modes
 * has a different speed, and each of the transfers will take time (and possibly cost money),
 * <p>
 * Copyright (c) 2022-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class StochasticTransportOption implements Identifiable, Serializable
{
    /** */
    private static final long serialVersionUID = 20221202L;

    /** the id of the TransportOption. */
    private final String id;

    /** the sequence of TransportSteps. */
    private ImmutableList<StochasticTransportOptionStep> transportSteps = new ImmutableArrayList<>(new ArrayList<>());

    /**
     * make a new TransportOption.
     * @param id the id of the TransportOption
     */
    public StochasticTransportOption(final String id)
    {
        Throw.whenNull(id, "id cannot be null");
        this.id = id;
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    /**
     * Return the transport steps.
     * @return the transport steps
     */
    public ImmutableList<StochasticTransportOptionStep> getTransportSteps()
    {
        return this.transportSteps;
    }

    /**
     * Add a transport step.
     * @param transportOptionStep the new transport step
     */
    public void addTransportStep(final StochasticTransportOptionStep transportOptionStep)
    {
        Throw.whenNull(transportOptionStep, "transportOptionStep cannot be null");
        List<StochasticTransportOptionStep> steps = this.transportSteps.toList();
        steps.add(transportOptionStep);
        this.transportSteps = new ImmutableArrayList<>(steps);
    }

    /**
     * Add a number of transport steps.
     * @param steps the new transport steps
     */
    public void addTransportSteps(final List<StochasticTransportOptionStep> steps)
    {
        Throw.whenNull(steps, "steps cannot be null");
        for (StochasticTransportOptionStep transportOptionStep : steps)
        {
            addTransportStep(transportOptionStep);
        }
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.id, this.transportSteps);
    }

    @Override
    @SuppressWarnings("checkstyle:needbraces")
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StochasticTransportOption other = (StochasticTransportOption) obj;
        return Objects.equals(this.id, other.id) && Objects.equals(this.transportSteps, other.transportSteps);
    }

    @Override
    public String toString()
    {
        return "TransportOption [id=" + this.id + ", transportSteps=" + this.transportSteps + "]";
    }

}
