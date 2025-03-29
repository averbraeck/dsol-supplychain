package nl.tudelft.simulation.supplychain.animation;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;

import org.djutils.draw.point.OrientedPoint3d;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.dsol.animation.D2.Renderable2DInterface;
import nl.tudelft.simulation.dsol.swing.animation.D2.AnimationPanel;
import nl.tudelft.simulation.dsol.swing.animation.D2.InputListener;
import nl.tudelft.simulation.dsol.swing.introspection.gui.IntrospectionDialog;

/**
 * Editor for introspection. <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MouseEditor extends InputListener
{
    /**
     * constructs a new MouseEditor.
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
                double z = ((OrientedPoint3d) next.getSource().getLocation()).z;
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
            new IntrospectionDialog(source, source.toString());
        }
        catch (RemoteException exception)
        {
            Logger.warn(exception, "edit");
        }
    }
}
