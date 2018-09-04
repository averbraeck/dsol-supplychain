package nl.tudelft.simulation.messaging.devices.components;

import java.io.Serializable;

import nl.tudelft.simulation.messaging.Message;

/**
 * The SendingDeviceInterface, which extends the normal DeviceInterface with a 'send' method. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public interface SendingDeviceInterface extends DeviceInterface
{
    /**
     * sends a message
     * @param message the message to be send
     * @return acknowledgement
     */
    Serializable send(Message message);
}
