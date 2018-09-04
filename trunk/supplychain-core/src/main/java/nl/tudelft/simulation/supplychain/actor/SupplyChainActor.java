package nl.tudelft.simulation.supplychain.actor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Point3d;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.djunits.unit.DurationUnit;
import org.djunits.unit.LengthUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;

import nl.tudelft.simulation.actor.Actor;
import nl.tudelft.simulation.content.HandlerInterface;
import nl.tudelft.simulation.messaging.Message;
import nl.tudelft.simulation.messaging.devices.components.SendingDeviceInterface;
import nl.tudelft.simulation.messaging.devices.components.SendingReceivingDevice;
import nl.tudelft.simulation.supplychain.banking.Bank;
import nl.tudelft.simulation.supplychain.banking.BankAccount;
import nl.tudelft.simulation.supplychain.banking.FixedCost;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.content.Shipment;
import nl.tudelft.simulation.supplychain.roles.Role;
import nl.tudelft.simulation.unit.simulator.DEVSSimulatorInterfaceUnit;

/**
 * A SupplyChainActor is an Actor from the Actor package with a bank account, and a way to keep track of its messages. It can
 * play certain roles, to which it can delegate the handling of its messages. It can also choose to handle messages itself. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */

public abstract class SupplyChainActor extends Actor
{
    /** */
    private static final long serialVersionUID = 1L;

    /** true for debugging */
    private static final boolean DEBUG = false;

    /** the bank account of the actor */
    protected BankAccount bankAccount = null;

    /** the store for the content to use */
    private ContentStoreInterface contentStore = null;

    /** the roles for this actor */
    private List<Role> roles = new ArrayList<Role>();

    /** the fixed costs for this supply chain actor */
    private List<FixedCost> fixedCosts = new ArrayList<FixedCost>();

    /** the logger. */
    private static Logger logger = LogManager.getLogger(SupplyChainActor.class);

    /**
     * Constructs a new SupplyChainActor
     * @param name the name to display for this supply chain actor
     * @param simulator the simulator on which to schedule
     * @param position the location for transportation calculations, which can also be used for animation purposes
     * @param roles the roles of the supply chain actor, might be null
     * @param bank the bank
     */
    public SupplyChainActor(final String name, final DEVSSimulatorInterfaceUnit simulator, final Point3d position,
            final Role[] roles, final Bank bank)
    {
        super(name, simulator, position);
        this.bankAccount = new BankAccount(this, bank);
        if (roles != null)
        {
            for (int i = 0; i < roles.length; i++)
            {
                this.roles.add(roles[i]);
            }
        }
    }

    /**
     * Constructs a new SupplyChainActor
     * @param name the name to display for this supply chain actor
     * @param simulator the simulator on which to schedule
     * @param position the location for transportation calculations, which can also be used for animation purposes
     * @param roles the roles of the supply chain actor, might be null
     * @param bank the bank
     * @param initialBankBalance the initial bank balance
     */
    public SupplyChainActor(final String name, final DEVSSimulatorInterfaceUnit simulator, final Point3d position,
            final Role[] roles, final Bank bank, final double initialBankBalance)
    {
        this(name, simulator, position, roles, bank);
        this.bankAccount.addToBalance(initialBankBalance);
    }

    /**
     * A Role wraps a set of handlers within a SupplyChainActor. A SupplyChainActor can have several roles. When handling
     * messages, all handlers of all Roles and of the SupplyChainActor itself will be checked to see which ones can handle the
     * content of the received message.
     * @param role the role to add
     */
    public void addRole(final Role role)
    {
        this.roles.add(role);
    }

    /**
     * Remove an existing role
     * @param role the role to remove
     */
    public void removeRole(final Role role)
    {
        this.roles.remove(role);
    }

    /**
     * @see nl.tudelft.simulation.content.HandlerInterface #handleContent(java.io.Serializable)
     */
    @Override
    public boolean handleContent(final Serializable content)
    {
        // save content in the content store
        this.contentStore.addContent((Content) content, false);
        Iterator<Role> roleIterator = this.roles.iterator();
        boolean success = false;
        while (roleIterator.hasNext())
        {
            success |= ((HandlerInterface) roleIterator.next()).handleContent(content);
        }
        // send content also to actor itself for possible handling
        success |= super.handleContent(content);
        if (!success)
        {
            logger.warn("handleContent", "No supply chain content handler, or one of its roles "
                    + "successfully handled content type " + content.getClass() + ", actor " + this.getName());
        }
        return success;
    }

    /**
     * Basic implementation of the sending of a message using the fastest device available at both the sender's and the
     * receiver's side. The delay used here is the delay BEFORE sending will take place; in other words it is the processing
     * time, preparation time, etc. The transfer of the message from the sending supply chain actor to the receiving supply
     * chain actor is fully determined by the properties and state of the devices, and it will use the standard transmission
     * delay as indicated by the device.
     * @param content the content to pack in a Message and send
     * @param administrativeDelay the time it will take to transmit the message
     */
    public void sendContent(final Content content, final Duration administrativeDelay)
    {
        Message message = new Message(this, content.getReceiver(), content);
        SendingDeviceInterface sendingDevice = resolveFastestDevice(this, content.getReceiver());
        if (sendingDevice != null)
        {
            try
            {
                if (content.getReceiver().equals(content.getSender()) && (sendingDevice instanceof SendingReceivingDevice))
                {
                    Serializable[] args = { message };
                    this.simulator.scheduleEventRel(administrativeDelay, this, sendingDevice, "receive", args);
                }
                else
                {
                    if (content instanceof Shipment)
                    {
                        Serializable[] args = { message, sendingDevice, administrativeDelay };
                        this.simulator.scheduleEventRel(new Duration(0.0001, DurationUnit.SECOND), this, this,
                                "scheduledSendContent", args);
                    }
                    else
                    {
                        Serializable[] args = { message, sendingDevice };
                        this.simulator.scheduleEventRel(new Duration(administrativeDelay.si + 0.0001, DurationUnit.SI), this,
                                this, "scheduledSendContent", args);
                    }
                }
            }
            catch (Exception e)
            {
                logger.warn("sendContent", e);
            }
        }
        // save content
        this.contentStore.addContent(content, true);
    }

    /**
     * Delayed sending of the content, wrapped in a message. This method is scheduled by sendContent.
     * @param message the message to send
     * @param sendingDevice the device to use for sending.
     */
    protected void scheduledSendContent(final Message message, final SendingDeviceInterface sendingDevice)
    {
        sendingDevice.send(message); // ignore success or failure
        // TODO: create animation if necessary
        /*-
        if (this.simulator instanceof AnimatorInterface)
        {
            double hour = 1.0;
            try
            {
                hour = TimeUnit.convert(1.0, TimeUnit.HOUR, this.simulator);
            }
            catch (RemoteException exception)
            {
                logger.fatal("scheduledSendContent", exception);
            }
            new ContentAnimation((Content) message.getBody(), hour);
        }
        */
    }

    /**
     * Delayed sending of the content, wrapped in a message. This method is scheduled by sendContent.
     * @param message the message to send
     * @param sendingDevice the device to use for sending.
     * @param delay the delay
     */
    protected void scheduledSendContent(final Message message, final SendingDeviceInterface sendingDevice, final Duration delay)
    {
        // TODO: create animation if necessary
        /*-
        double delayInDays = 0.0;
        if (this.simulator instanceof AnimatorInterface)
        {
            delayInDays = 0.0;
            delayInDays = TimeUnit.convert(delay, TimeUnit.HOUR, TimeUnit.DAY);
            if (SupplyChainActor.DEBUG)
            {
                System.out.println("SupplyChainActor: scheduledSendContent: delay in days for content: " + message.getBody()
                        + " delay: " + delayInDays);
            }
            new ContentAnimation((Content) message.getBody(), delayInDays);
        }
        */

        // we schedule the delayed invocation of the send content
        try
        {
            Serializable[] args = { message };
            this.simulator.scheduleEventRel(delay, this, sendingDevice, "send", args);
        }
        catch (Exception exception)
        {
            logger.fatal("scheduledSendContent", exception);
        }
    }

    /**
     * Add a fixed cost for this actor
     * @param description the description of the fixed cost
     * @param interval the interval
     * @param amount the amount
     */
    public void addFixedCost(final String description, final Duration interval, final double amount)
    {
        FixedCost fixedCost = new FixedCost(this, this.bankAccount, description, interval, amount);
        this.fixedCosts.add(fixedCost);

    }

    /**
     * @return Returns the contentStore.
     */
    public ContentStoreInterface getContentStore()
    {
        return this.contentStore;
    }

    /**
     * @param contentStore The contentStore to set.
     */
    public void setContentStore(final ContentStoreInterface contentStore)
    {
        this.contentStore = contentStore;
    }

    /**
     * @return Returns the bankAccount.
     */
    public BankAccount getBankAccount()
    {
        return this.bankAccount;
    }

    /**
     * @return Returns the fixed costs.
     */
    public List<FixedCost> getFixedCosts()
    {
        return this.fixedCosts;
    }

    /**
     * Calculates the distance to another actor
     * @param actor the other actor
     * @return the distance (might be overridden if the geography is known)
     */
    public Length calculateDistance(final SupplyChainActor actor)
    {
        // TODO: Assume kilometers for now.
        return new Length(this.location.distance(actor.getLocation()), LengthUnit.KILOMETER);
    }

    /**
     * @return the simulator without throwing an exception
     */
    public DEVSSimulatorInterfaceUnit getDEVSSimulator()
    {
        DEVSSimulatorInterfaceUnit _simulator = null;
        try
        {
            _simulator = super.getSimulator();
        }
        catch (Exception exception)
        {
            logger.fatal("getSimulator", exception);
        }
        return _simulator;
    }

    /**
     * Method getContentHandlers
     * @return returns the map with content handlers
     */
    public Map<Class<?>, Set<HandlerInterface>> getContentHandlers()
    {
        return super.contentHandlers;
    }

}
