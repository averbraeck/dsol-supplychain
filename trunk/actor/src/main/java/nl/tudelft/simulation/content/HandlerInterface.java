package nl.tudelft.simulation.content;

import java.io.Serializable;

/**
 * The HandlerInterface defines what any Handler should be able to do: handle some Serializable content. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public interface HandlerInterface extends Serializable
{
    /**
     * Handle the content of the message.
     * @param content the content to be handled, can be of any type
     * @return a boolean acknowledgement; true or false
     */
    boolean handleContent(Serializable content);
}
