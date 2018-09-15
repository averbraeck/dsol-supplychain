package nl.tudelft.simulation.messaging.devices.components;

import java.io.Serializable;

import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.messaging.Message;
import nl.tudelft.simulation.messaging.devices.types.DeviceType;
import nl.tudelft.simulation.unit.dist.DistContinuousDurationUnit;

/**
 * The DelaySendingDevice device is a device that sends out a message, which will arrive after a certain delay. There is no
 * resource behavior in the device. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DelaySendingDevice extends SendingDevice
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the simulator to schedule on */
    protected DEVSSimulatorInterface.TimeDoubleUnit simulator = null;

    /** the delay of the sender */
    protected DistContinuousDurationUnit delay = null;

    /**
     * constructs a new DelaySendingDevice, take the transmission delay and frequency from the DeviceType.
     * @param name the name or description of the device
     * @param deviceType the type of device
     * @param simulator the simulator
     * @param delay the delay
     */
    public DelaySendingDevice(final String name, final DeviceType deviceType, final DEVSSimulatorInterface.TimeDoubleUnit simulator,
            final DistContinuousDurationUnit delay)
    {
        super(name, deviceType);
        this.simulator = simulator;
        this.delay = delay;
    }

    /**
     * constructs a new DelaySendingDevice, override the default transmission delay and frequency from the DeviceType.
     * @param name the name or description of the device
     * @param deviceType the type of device
     * @param transmissionDelay the default logarithmic transmission delay of the device.
     * @param transmissionFrequency the maximum transmission frequency of the device.
     * @param simulator the simulator
     * @param delay the delay
     */
    public DelaySendingDevice(final String name, final DeviceType deviceType, final int transmissionDelay,
            final double transmissionFrequency, final DEVSSimulatorInterface.TimeDoubleUnit simulator,
            final DistContinuousDurationUnit delay)
    {
        super(name, deviceType, transmissionDelay, transmissionFrequency);
        this.simulator = simulator;
        this.delay = delay;
    }

    /** {@inheritDoc} */
    @Override
    public Serializable send(final Message message)
    {
        ReceivingDeviceInterface receiver = message.getReceiver().getReceivingDevices(this.getDeviceType())[0];
        try
        {
            this.simulator.scheduleEventRel(this.delay.draw(), this, receiver, "receive", new Serializable[] { message });
        }
        catch (Exception exception)
        {
            Logger.warn(exception, "send");
        }
        return Boolean.TRUE;
    }
}
