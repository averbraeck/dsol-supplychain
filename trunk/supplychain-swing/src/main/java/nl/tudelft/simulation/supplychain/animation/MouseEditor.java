package nl.tudelft.simulation.supplychain.animation;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;

import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.dsol.animation.D2.Renderable2DInterface;
import nl.tudelft.simulation.dsol.swing.animation.D2.AnimationPanel;
import nl.tudelft.simulation.dsol.swing.animation.D2.mouse.InputListener;
import nl.tudelft.simulation.introspection.gui.IntroSpectionDialog;

/**
 * Editor for introspection. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class MouseEditor extends InputListener
{
    /**
     * constructs a new MouseEditor
     * @param panel the animation panel
     */
    public MouseEditor(final AnimationPanel panel)
    {
        super(panel);
    }

    /**
     * @param targets list of targets
     */
    protected void edit(final List targets)
    {
        try
        {
            double zValue = -Double.MAX_VALUE;
            Renderable2DInterface selected = null;
            for (Iterator i = targets.iterator(); i.hasNext();)
            {
                Renderable2DInterface next = (Renderable2DInterface) i.next();
                double z = next.getSource().getLocation().z;
                if (z > zValue)
                {
                    zValue = z;
                    selected = next;
                }
            }
            Object source = selected.getSource();
            if (source instanceof ContentAnimation)
            {
                source = ((ContentAnimation) source).getContent();
            }
            new IntroSpectionDialog(source, source.toString());
        }
        catch (RemoteException exception)
        {
            Logger.warn(exception, "edit");
        }
    }
}
