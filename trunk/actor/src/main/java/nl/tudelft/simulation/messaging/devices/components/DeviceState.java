package nl.tudelft.simulation.messaging.devices.components;

import java.io.Serializable;

/**
 * The state of the device as a resource, indicating whether it is working or not. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DeviceState implements Serializable
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** static state to indicate idle device, in other words ready to use */
    public static final DeviceState IDLE = new DeviceState("IDLE", true);

    /** static state to indicate busy device, operation has to wait */
    public static final DeviceState BUSY = new DeviceState("BUSY", false);

    /** internal state */
    private String description = null;

    /** working or not */
    private boolean working = true;

    /**
     * creates a state
     * @param description the description of this device
     * @param working indicates whether this state is a working state
     */
    public DeviceState(final String description, final boolean working)
    {
        super();
        this.description = description;
        this.working = working;
    }

    /**
     * @return Returns the state description.
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * @return Returns whether the device is working.
     */
    public boolean isWorking()
    {
        return this.working;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.description == null) ? 0 : this.description.hashCode());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DeviceState other = (DeviceState) obj;
        if (this.description == null)
        {
            if (other.description != null)
                return false;
        }
        else if (!this.description.equals(other.description))
            return false;
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return this.description;
    }
}
