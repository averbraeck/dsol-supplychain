package nl.tudelft.simulation.unit.simulator;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.eventlists.EventListInterface;
import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEventInterface;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;

/**
 * Typed extension of the DEVSSimulatorInterface without remote exceptions and using the Time and Duration arguments. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public interface DEVSSimulatorInterfaceUnit extends DEVSSimulatorInterface<Time, Duration, SimTimeUnit>, SimulatorInterfaceUnit
{
    /** {@inheritDoc} */
    @Override
    boolean cancelEvent(SimEventInterface<SimTimeUnit> event);

    /** {@inheritDoc} */
    @Override
    EventListInterface<SimTimeUnit> getEventList();

    /** {@inheritDoc} */
    @Override
    SimEventInterface<SimTimeUnit> scheduleEvent(SimEventInterface<SimTimeUnit> event) throws SimRuntimeException;

    /**
     * schedules a methodCall at a relative duration. The executionTime is thus simulator.getSimulatorTime()+relativeDuration.
     * @param relativeDelay the relativeDelay in timeUnits of the simulator.
     * @param priority the priority compared to other events scheduled at the same time.
     * @param source the source of the event
     * @param target the target
     * @param method the method
     * @param args the arguments.
     * @return the simulation event so it can be cancelled later
     * @throws SimRuntimeException whenever the event is scheduled in the past.
     */
    @Override
    SimEventInterface<SimTimeUnit> scheduleEventRel(Duration relativeDelay, short priority, Object source, Object target,
            String method, Object[] args) throws SimRuntimeException;

    /**
     * schedules a methodCall at a relative duration. The executionTime is thus simulator.getSimulatorTime()+relativeDuration.
     * @param relativeDelay the relativeDelay in timeUnits of the simulator.
     * @param source the source of the event
     * @param target the target
     * @param method the method
     * @param args the arguments.
     * @return the simulation event so it can be cancelled later
     * @throws SimRuntimeException whenever the event is scheduled in the past.
     */
    @Override
    SimEventInterface<SimTimeUnit> scheduleEventRel(Duration relativeDelay, Object source, Object target, String method,
            Object[] args) throws SimRuntimeException;

    /** {@inheritDoc} */
    @Override
    SimEventInterface<SimTimeUnit> scheduleEventAbs(SimTimeUnit absoluteTime, short priority, Object source, Object target,
            String method, Object[] args) throws SimRuntimeException;

    /**
     * schedules a methodCall at an absolute time.
     * @param absoluteTime the exact time to schedule the method on the simulator.
     * @param source the source of the event
     * @param target the target
     * @param method the method
     * @param args the arguments.
     * @return the simulation event so it can be cancelled later
     * @throws SimRuntimeException whenever the event is scheduled in the past.
     */
    @Override
    SimEventInterface<SimTimeUnit> scheduleEventAbs(Time absoluteTime, Object source, Object target, String method,
            Object[] args) throws SimRuntimeException;

    /**
     * schedules a methodCall at an absolute time.
     * @param absoluteTime the exact time to schedule the method on the simulator.
     * @param priority the priority compared to other events scheduled at the same time.
     * @param source the source of the event
     * @param target the target
     * @param method the method
     * @param args the arguments.
     * @return the simulation event so it can be cancelled later
     * @throws SimRuntimeException whenever the event is scheduled in the past.
     */
    @Override
    SimEventInterface<SimTimeUnit> scheduleEventAbs(Time absoluteTime, short priority, Object source, Object target,
            String method, Object[] args) throws SimRuntimeException;

    /** {@inheritDoc} */
    @Override
    SimEventInterface<SimTimeUnit> scheduleEventAbs(SimTimeUnit absoluteTime, Object source, Object target, String method,
            Object[] args) throws SimRuntimeException;

    /** {@inheritDoc} */
    @Override
    SimEventInterface<SimTimeUnit> scheduleEventNow(short priority, Object source, Object target, String method, Object[] args)
            throws SimRuntimeException;

    /** {@inheritDoc} */
    @Override
    SimEventInterface<SimTimeUnit> scheduleEventNow(Object source, Object target, String method, Object[] args)
            throws SimRuntimeException;

    /** {@inheritDoc} */
    @Override
    void setEventList(EventListInterface<SimTimeUnit> eventList) throws SimRuntimeException;

    /** {@inheritDoc} */
    @Override
    Replication<Time, Duration, SimTimeUnit> getReplication();

    /**
     * Runs the simulator up to a certain time; events at that time will not yet be executed.
     * @param when the absolute time till when we want to run the simulation
     * @throws SimRuntimeException whenever starting fails. Possible occasions include starting a started simulator
     */
    @Override
    void runUpTo(Time when) throws SimRuntimeException;
}
