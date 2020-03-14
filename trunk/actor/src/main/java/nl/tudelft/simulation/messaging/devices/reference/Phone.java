package nl.tudelft.simulation.messaging.devices.reference;

import org.djunits.unit.DurationUnit;

import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistTriangular;
import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.messaging.comparators.FiFo;
import nl.tudelft.simulation.messaging.devices.components.DelaySendingDevice;
import nl.tudelft.simulation.messaging.devices.components.ReceivingDevice;
import nl.tudelft.simulation.messaging.devices.components.SendingReceivingDevice;
import nl.tudelft.simulation.messaging.devices.types.DeviceType;
import nl.tudelft.simulation.messaging.queues.MessageQueue;

/**
 * A reference implementation of a Phone for communication. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class Phone extends SendingReceivingDevice
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /**
     * Constructs a new Phone.
     * @param name the name or number of the fax
     * @param simulator the simulator to use
     */
    public Phone(final String name, final DEVSSimulatorInterface.TimeDoubleUnit simulator)
    {
        super(name, new ReceivingDevice(name + "-R", DeviceType.PHONE, new MessageQueue(new FiFo())),
                new DelaySendingDevice(name + "-S", DeviceType.PHONE, simulator,
                        new DistContinuousDuration(
                                new DistTriangular(simulator.getReplication().getStream("default"), 5.0, 10.0, 15.0),
                                DurationUnit.MINUTE)));
    }
}
