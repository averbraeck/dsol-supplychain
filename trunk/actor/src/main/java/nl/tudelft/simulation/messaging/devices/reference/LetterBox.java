package nl.tudelft.simulation.messaging.devices.reference;

import nl.tudelft.simulation.messaging.comparators.FiFo;
import nl.tudelft.simulation.messaging.devices.components.ReceivingDevice;
import nl.tudelft.simulation.messaging.devices.types.DeviceType;
import nl.tudelft.simulation.messaging.queues.MessageQueue;

/**
 * A reference implementation of receiving regular mail. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class LetterBox extends ReceivingDevice
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** default name for the inbox * */
    private static final String DEFAULT_NAME = "Letter-box";

    /**
     * Constructs a new LetterBox with the default name "Letter-box". A LetterBox is used for receiving letters.
     */
    public LetterBox()
    {
        this(LetterBox.DEFAULT_NAME);
    }

    /**
     * Constructs a new LetterBox. A LetterBox is used for receiving letters.
     * @param name the name of the LetterBox
     */
    public LetterBox(final String name)
    {
        super(name, DeviceType.LETTER, new MessageQueue(new FiFo()));
    }
}
