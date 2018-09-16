package nl.tudelft.simulation.messaging.devices.reference;

import org.djunits.unit.DurationUnit;

import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistTriangular;
import nl.tudelft.simulation.messaging.comparators.FiFo;
import nl.tudelft.simulation.messaging.devices.components.DelaySendingDevice;
import nl.tudelft.simulation.messaging.devices.components.ReceivingDevice;
import nl.tudelft.simulation.messaging.devices.components.SendingReceivingDevice;
import nl.tudelft.simulation.messaging.devices.types.DeviceType;
import nl.tudelft.simulation.messaging.queues.MessageQueue;
import nl.tudelft.simulation.unit.dist.DistContinuousDurationUnit;

/**
 * A reference implementation of an email application. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class EmailApplication extends SendingReceivingDevice
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /**
     * Constructs a new EmailApplication.
     * @param name the name of the email application
     * @param simulator the simulator to use
     */
    public EmailApplication(final String name, final DEVSSimulatorInterface.TimeDoubleUnit simulator)
    {
        super(name, new ReceivingDevice(name + "-R", DeviceType.EMAIL, new MessageQueue(new FiFo())),
                new DelaySendingDevice(name + "-S", DeviceType.EMAIL, simulator,
                        new DistContinuousDurationUnit(
                                new DistTriangular(simulator.getReplication().getStream("default"), 1.0, 5.0, 15.0),
                                DurationUnit.SECOND)));
    }
}
