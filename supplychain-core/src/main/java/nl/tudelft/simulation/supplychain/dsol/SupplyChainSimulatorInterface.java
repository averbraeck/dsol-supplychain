package nl.tudelft.simulation.supplychain.dsol;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.Executable;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEventInterface;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;
import nl.tudelft.simulation.naming.context.Contextualized;

/**
 * SupplyChainSimulatorInterface adds a start time to the simulator, so the current simulator time can be printed as a date,
 * possibly with a time on that date. The SupplyChainSimulatorInterface also allows to schedule events on absolute times.
 * <p>
 * Copyright (c) 2022-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface SupplyChainSimulatorInterface extends DevsSimulatorInterface<Duration>, Contextualized
{
    /**
     * Schedules a methodCall at an absolute time.
     * @param absoluteTime Time; the exact time to schedule the method on the simulator.
     * @param priority short; the priority compared to other events scheduled at the same time.
     * @param target Object; the target
     * @param method String; the method
     * @param args Object[]; the arguments.
     * @return the simulation event so it can be cancelled later
     * @throws SimRuntimeException whenever the event is scheduled in the past.
     */
    default SimEventInterface<Duration> scheduleEventAbs(final Time absoluteTime, final short priority, final Object target,
            final String method, final Object[] args) throws SimRuntimeException
    {
        return scheduleEventAbs(absoluteTime.minus(getAbsStartTime()), priority, target, method, args);
    }

    /**
     * Schedules a methodCall at an absolute time.
     * @param absoluteTime Time; the exact time to schedule the method on the simulator.
     * @param target Object; the target
     * @param method String; the method
     * @param args Object[]; the arguments.
     * @return the simulation event so it can be cancelled later
     * @throws SimRuntimeException whenever the event is scheduled in the past.
     */
    default SimEventInterface<Duration> scheduleEventAbs(final Time absoluteTime, final Object target, final String method,
            final Object[] args) throws SimRuntimeException
    {
        return scheduleEventAbs(absoluteTime.minus(getAbsStartTime()), target, method, args);
    }

    /**
     * schedules a lambda expression at an absolute time.
     * @param absoluteTime T; the exact time to schedule the method on the simulator.
     * @param priority short; the priority compared to other events scheduled at the same time.
     * @param executable Executable; the lambda expression to execute
     * @return the simulation event so it can be cancelled later
     * @throws SimRuntimeException whenever the event is scheduled in the past.
     */
    default SimEventInterface<Duration> scheduleEventAbs(final Time absoluteTime, final short priority,
            final Executable executable) throws SimRuntimeException
    {
        return scheduleEventAbs(absoluteTime.minus(getAbsStartTime()), priority, executable);
    }

    /**
     * schedules a lambda expression at an absolute time.
     * @param absoluteTime T; the exact time to schedule the method on the simulator.
     * @param executable Executable; the lambda expression to execute
     * @return the simulation event so it can be cancelled later
     * @throws SimRuntimeException whenever the event is scheduled in the past.
     */
    default SimEventInterface<Duration> scheduleEventAbs(final Time absoluteTime, final Executable executable)
            throws SimRuntimeException
    {
        return scheduleEventAbs(absoluteTime.minus(getAbsStartTime()), executable);
    }

    /**
     * Return the absolute start time .
     * @return Time; the absolute start time
     */
    Time getAbsStartTime();

    /**
     * Return the absolute simulation time.
     * @return Time; the absolute simulation time
     */
    default Time getAbsSimulatorTime()
    {
        return getAbsStartTime().plus(getSimulatorTime());
    }

    /** {@inheritDoc} */
    @Override
    SupplyChainModelInterface getModel();

}
