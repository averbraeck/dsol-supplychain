package nl.tudelft.simulation.supplychain.test;

import java.io.Serializable;
import java.rmi.RemoteException;

import org.djunits.unit.DurationUnit;
import org.djunits.unit.MassUnit;
import org.djunits.unit.VolumeUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Mass;
import org.djunits.value.vdouble.scalar.Volume;
import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.OrientedPoint3d;
import org.djutils.draw.point.Point;
import org.djutils.draw.point.Point2d;
import org.djutils.event.Event;
import org.djutils.event.EventListener;

import nl.tudelft.simulation.dsol.animation.d2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.actor.Geography;
import nl.tudelft.simulation.supplychain.animation.ContentAnimator;
import nl.tudelft.simulation.supplychain.content.Content;
import nl.tudelft.simulation.supplychain.content.GroupedContent;
import nl.tudelft.simulation.supplychain.content.store.ContentStoreEmpty;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModel;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainSimulatorInterface;
import nl.tudelft.simulation.supplychain.money.Money;
import nl.tudelft.simulation.supplychain.money.MoneyUnit;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.product.Sku;
import nl.tudelft.simulation.supplychain.reference.Bank;
import nl.tudelft.simulation.supplychain.reference.Transporter;
import nl.tudelft.simulation.supplychain.role.banking.BankingRole;
import nl.tudelft.simulation.supplychain.role.banking.handler.BankTransferHandler;
import nl.tudelft.simulation.supplychain.role.banking.process.InterestProcess;

/**
 * The TestModel for the supplychain package.
 * <p>
 * Copyright (c) 2003-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TestModel extends SupplyChainModel implements EventListener
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221201L;

    /** timing run-time. */
    private long startTimeMs = 0;

    /** the simulator. */
    private SupplyChainSimulatorInterface devsSimulator;

    /** */
    private Product laptop;

    /** */
    private Factory factory;

    /** */
    private PCShop pcShop;

    /** */
    private Client client;

    /** */
    private Transporter trucking;

    /** */
    private Bank bank;

    /**
     * constructs a new TestModel.
     * @param simulator the simulator
     */
    public TestModel(final SupplyChainSimulatorInterface simulator)
    {
        super(simulator);
        // We don't do anything to prevent state-based replications.
    }

    @Override
    public void constructModel()
    {
        try
        {
            this.startTimeMs = System.currentTimeMillis();
            this.devsSimulator = (SupplyChainSimulatorInterface) this.simulator;
            if (this.devsSimulator instanceof AnimatorInterface)
            {
                // First we create some background. We set the zValue to -Double.Min value to ensure that it is actually drawn
                // "below" our actors and messages.
                new SingleImageRenderable<>(new OrientedPoint3d(0.0, 0.0, -Double.MIN_VALUE), new Bounds3d(1618, 716, 0),
                        this.devsSimulator,
                        TestModel.class.getResource("/nl/tudelft/simulation/supplychain/images/worldmap.gif"));
            }

            // create the bank
            this.bank = new GlobalBank("bank", "Bank", this, new Point2d(0, 0), "Bank", "Europe");

            // create a product
            this.laptop = new Product(this, "Laptop", Sku.PIECE, new Money(1400.0, MoneyUnit.USD),
                    new Mass(6.5, MassUnit.KILOGRAM), new Volume(0.05, VolumeUnit.CUBIC_METER), 0.0);

            // create a transporter
            this.trucking = new Trucking("transporter", "Transporter", this, new Point2d(200, 20), "Maastricht", "Europe",
                    this.bank, new Money(50000.0, MoneyUnit.USD));

            // create a manufacturer
            Geography factoryGeography = new Geography(new Point2d(200, 200), "Delft", "Europe");
            this.factory = new Factory("factory", "Factory", this, factoryGeography, this.bank,
                    new Money(50000.0, MoneyUnit.USD), new ContentStoreEmpty(), this.laptop, 1000);

            // create a retailer
            Geography pcShopGeography = new Geography(new Point2d(20, 200), "Rotterdam", "Europe");
            this.pcShop = new PCShop("pcShop", "PCshop", this, pcShopGeography, this.bank, new Money(50000.0, MoneyUnit.USD),
                    new ContentStoreEmpty(), this.laptop, 10, this.factory);

            // create a customer
            Geography clientGeography = new Geography(new Point2d(100, 100), "Amsterdam", "Europe");
            this.client = new Client("client", "Client", this, clientGeography, this.bank, new Money(1500000.0, MoneyUnit.USD),
                    new ContentStoreEmpty(), this.laptop, this.pcShop);

            // schedule a remark that the simulation is ready
            Duration endTime =
                    new Duration(this.simulator.getReplication().getRunLength().doubleValue() - 0.001, DurationUnit.SI);
            this.devsSimulator.scheduleEventRel(endTime, this, "endSimulation", new Serializable[] {});

            subscribeToMessages();

            // Create the animation.
            if (this.simulator instanceof AnimatorInterface)
            {
                ContentAnimator contentAnimator = new ContentAnimator(this.devsSimulator);
                contentAnimator.subscribe(this.factory);
                contentAnimator.subscribe(this.pcShop);
                contentAnimator.subscribe(this.client);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Subscribe to messages and print them.
     */
    private void subscribeToMessages()
    {
        this.client.addListener(this, Actor.SEND_CONTENT_EVENT);
        this.factory.addListener(this, Actor.SEND_CONTENT_EVENT);
        this.pcShop.addListener(this, Actor.SEND_CONTENT_EVENT);
        this.bank.addListener(this, Actor.SEND_CONTENT_EVENT);
        this.trucking.addListener(this, Actor.SEND_CONTENT_EVENT);
    }

    /**
     * end of simulation -- display a message.
     */
    protected void endSimulation()
    {
        System.out.println("End of TestModel replication");
        System.out.println("Runtime = " + ((System.currentTimeMillis() - this.startTimeMs) / 1000) + " seconds.");
        System.out.println("Simulation time = " + this.devsSimulator.getSimulatorTime().toString(DurationUnit.HOUR));
    }

    @Override
    public Length calculateDistance(final Point<?> loc1, final Point<?> loc2)
    {
        double dx = loc2.getX() - loc1.getX();
        double dy = loc2.getY() - loc1.getY();
        return Length.instantiateSI(Math.sqrt(dx * dx + dy * dy));
    }

    @Override
    public void notify(final Event event) throws RemoteException
    {
        if (event.getType().equals(Actor.SEND_CONTENT_EVENT))
        {
            Content content = (Content) event.getContent();
            long groupingId = (content instanceof GroupedContent gc) ? gc.groupingId() : -1L;
            System.out.println(getSimulator().getSimulatorTime().toString(DurationUnit.HOUR) + " - "
                    + content.getClass().getSimpleName() + " from " + content.sender() + " to " + content.receiver() + " id="
                    + content.uniqueId() + ", groupingId=" + groupingId);
        }
    }

    /**
     * @return laptop
     */
    public Product getLaptop()
    {
        return this.laptop;
    }

    /**
     * @return factory
     */
    public Factory getFactory()
    {
        return this.factory;
    }

    /**
     * @return pcShop
     */
    public PCShop getPcShop()
    {
        return this.pcShop;
    }

    /**
     * @return client
     */
    public Client getClient()
    {
        return this.client;
    }

    /**
     * @return bank
     */
    public Bank getBank()
    {
        return this.bank;
    }

    /**
     * @return trucking
     */
    public Transporter getTrucking()
    {
        return this.trucking;
    }

}
