package nl.tudelft.simulation.supplychain.demo;

import java.rmi.RemoteException;

import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.event.EventInterface;

import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface.TimeDoubleUnit;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.animation.ContentAnimation;
import nl.tudelft.simulation.supplychain.animation.ContentAnimator;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.Shipment;

/**
 * DemoContentAnimator.java. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class DemoContentAnimator extends ContentAnimator
{

    /**
     * @param simulator
     */
    public DemoContentAnimator(TimeDoubleUnit simulator)
    {
        super(simulator);
    }

    /** {@inheritDoc} */
    @Override
    public void notify(EventInterface event) throws RemoteException
    {
        if (event.getType().equals(SupplyChainActor.SEND_CONTENT_EVENT))
        {
            if (getSimulator() instanceof AnimatorInterface)
            {
                Object[] eventContent = (Object[]) event.getContent();
                Content content = (Content) eventContent[0];
                Duration delay = (Duration) eventContent[1];

                if (content instanceof Shipment)
                {
                    Shipment shipment = (Shipment) content;
                    switch (shipment.getProduct().getName())
                    {
                        case "PC":
                            new ContentAnimation(content, delay, DemoContentAnimator.class
                                    .getResource("/nl/tudelft/simulation/supplychain/demo/images/computer.gif"));
                            return;

                        case "keyboard":
                            new ContentAnimation(content, delay, DemoContentAnimator.class
                                    .getResource("/nl/tudelft/simulation/supplychain/demo/images/keyboard.gif"));
                            return;

                        case "casing":
                            new ContentAnimation(content, delay, DemoContentAnimator.class
                                    .getResource("/nl/tudelft/simulation/supplychain/demo/images/casing.gif"));
                            return;

                        case "mouse":
                            new ContentAnimation(content, delay, DemoContentAnimator.class
                                    .getResource("/nl/tudelft/simulation/supplychain/demo/images/mouse.gif"));
                            return;

                        case "monitor":
                            new ContentAnimation(content, delay, DemoContentAnimator.class
                                    .getResource("/nl/tudelft/simulation/supplychain/demo/images/monitor.gif"));
                            return;

                        default:
                            break;
                    }
                }
                
                new ContentAnimation(content, delay);
            }
        }

    }

}
