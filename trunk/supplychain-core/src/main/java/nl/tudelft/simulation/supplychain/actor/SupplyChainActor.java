package nl.tudelft.simulation.supplychain.actor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Point3d;

import org.djunits.unit.DurationUnit;
import org.djunits.unit.LengthUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Money;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.actor.Actor;
import nl.tudelft.simulation.content.HandlerInterface;
import nl.tudelft.simulation.dsol.simulators.DEVSSimulatorInterface;
import nl.tudelft.simulation.event.EventType;
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

    /** the bank account of the actor */
    protected final BankAccount bankAccount;

    /** the store for the content to use */
    private final ContentStoreInterface contentStore;

    /** the roles for this actor; avoid roles to be registered multiple times (Set). */
    private Set<Role> roles = new HashSet<Role>();

    /** the fixed costs for this supply chain actor */
    private List<FixedCost> fixedCosts = new ArrayList<FixedCost>();

    /** the event to indicate that information has been sent. E.g., for animation. */
    public static EventType SEND_CONTENT_EVENT = new EventType("SEND_CONTENT_EVENT");

    /** DEBUG messages? */
    public static boolean DEBUG = false;

    /**
     * Constructs a new SupplyChainActor
     * @param name the name to display for this supply chain actor
     * @param simulator the simulator on which to schedule
     * @param position the location for transportation calculations, which can also be used for animation purposes
     * @param bank the bank
     * @param contentStore the contentStore for the messages
     */
    public SupplyChainActor(final String name, final DEVSSimulatorInterface.TimeDoubleUnit simulator, final Point3d position,
            final Bank bank, final ContentStoreInterface contentStore)
    {
        super(name, simulator, position);
        this.bankAccount = new BankAccount(this, bank);
        this.contentStore = contentStore;
    }

    /**
     * Constructs a new SupplyChainActor
     * @param name the name to display for this supply chain actor
     * @param simulator the simulator on which to schedule
     * @param position the location for transportation calculations, which can also be used for animation purposes
     * @param bank the bank
     * @param initialBankBalance the initial bank balance
     * @param contentStore the contentStore for the messages
     */
    public SupplyChainActor(final String name, final DEVSSimulatorInterface.TimeDoubleUnit simulator, final Point3d position,
            final Bank bank, final Money initialBankBalance, final ContentStoreInterface contentStore)
    {
        this(name, simulator, position, bank, contentStore);
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

    /** {@inheritDoc} */
    @Override
    public boolean handleContent(final Serializable content)
    {
        Logger.info(getName() + ".handleContent. id=" + ((Content) content).getInternalDemandID() + ": " + content.toString());
        // save content in the content store
        this.contentStore.addContent((Content) content, false);
        boolean success = false;
        for (Role role : this.roles)
        {
            success |= role.handleContent(content, false);
        }
        for (HandlerInterface handler : this.resolveContentHandlers(content.getClass()))
        {
            success |= handler.handleContent(content);
        }
        if (!success)
        {
            Logger.warn(
                    "handleContent - No supply chain content handler, or one of its roles successfully handled content type {}, actor {}",
                    content.getClass(), this.getName());
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
                Logger.error(e, "sendContent");
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
        fireEvent(SEND_CONTENT_EVENT, new Object[] { message.getBody(), new Duration(1.0, DurationUnit.HOUR) });
    }

    /**
     * Delayed sending of the content, wrapped in a message. This method is scheduled by sendContent.
     * @param message the message to send
     * @param sendingDevice the device to use for sending.
     * @param delay the delay
     */
    protected void scheduledSendContent(final Message message, final SendingDeviceInterface sendingDevice, final Duration delay)
    {
        fireEvent(SEND_CONTENT_EVENT, new Object[] { message.getBody(), delay });
        if (SupplyChainActor.DEBUG)
        {
            System.out.println("SupplyChainActor: scheduledSendContent: delay in days for content: " + message.getBody()
                    + " delay: " + delay);
        }

        // we schedule the delayed invocation of the send content
        try
        {
            Serializable[] args = { message };
            this.simulator.scheduleEventRel(delay, this, sendingDevice, "send", args);
        }
        catch (Exception exception)
        {
            Logger.error(exception, "scheduledSendContent");
        }
    }

    /**
     * Add a fixed cost for this actor
     * @param description the description of the fixed cost
     * @param interval the interval
     * @param amount the amount
     */
    public void addFixedCost(final String description, final Duration interval, final Money amount)
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
    public DEVSSimulatorInterface.TimeDoubleUnit getDEVSSimulator()
    {
        DEVSSimulatorInterface.TimeDoubleUnit _simulator = null;
        try
        {
            _simulator = super.getSimulator();
        }
        catch (Exception exception)
        {
            Logger.error(exception, "getSimulator");
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
