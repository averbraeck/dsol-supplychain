package nl.tudelft.simulation.supplychain.demo;

import java.rmi.RemoteException;

import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.event.EventInterface;

import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.animation.ContentAnimation;
import nl.tudelft.simulation.supplychain.animation.ContentAnimator;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulatorInterface;
import nl.tudelft.simulation.supplychain.message.trade.TradeMessage;
import nl.tudelft.simulation.supplychain.product.Shipment;

/**
 * DemoContentAnimator.java. <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DemoContentAnimator extends ContentAnimator
{

    /**
     * @param simulator
     */
    public DemoContentAnimator(SupplyChainSimulatorInterface simulator)
    {
        super(simulator);
    }

    @Override
    public void notify(EventInterface event) throws RemoteException
    {
        if (event.getType().equals(Actor.SEND_MESSAGE_EVENT))
        {
            if (getSimulator() instanceof AnimatorInterface)
            {
                Object[] eventContent = (Object[]) event.getContent();
                TradeMessage content = (TradeMessage) eventContent[0];
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
