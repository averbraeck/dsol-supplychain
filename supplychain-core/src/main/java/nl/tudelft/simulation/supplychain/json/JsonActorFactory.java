package nl.tudelft.simulation.supplychain.json;

import java.util.LinkedHashMap;
import java.util.Map;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;

/**
 * JsonMessageFactory allows serializing and deserializing of any Message using the GSON library. This class can be extended to
 * add more type adapters.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public final class JsonActorFactory
{
    /** The default GSON object with the specialized type adapters. */
    private final Gson gson;

    /** the model to get, e.g., the Actor map to (de)serialize the message sender and receiver. */
    private final SupplyChainModelInterface model;

    /** static map of JsonMessageFactory singletons per model. */
    private static final Map<SupplyChainModelInterface, JsonActorFactory> INSTANCE_MAP = new LinkedHashMap<>();

    /**
     * Create a JsonMessageFactory with the special type adapters.
     * @param model the model for which to register the type adapters
     */
    public JsonActorFactory(final SupplyChainModelInterface model)
    {
        this.model = model;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.registerTypeAdapter(Time.class, new TimeAdapter());
        gsonBuilder.registerTypeHierarchyAdapter(Actor.class, new ActorAdapter(this.model));
        registerTypeAdapters();
        this.gson = gsonBuilder.create();
    }

    /**
     * Return the Gson (de)serializer.
     * @return the Gson (de)serializer
     */
    public Gson getGson()
    {
        return this.gson;
    }

    /**
     * Register other type adapters in subclasses. Override method to do so.
     */
    public void registerTypeAdapters()
    {
        // no content yet, overide method
    }

    /**
     * Return a singleton instance of the Gson object that is unique to the model.
     * @param model the model for which to return or create the Gson object
     * @return the unique Gson object (created) for this model
     */
    public static Gson instance(final SupplyChainModelInterface model)
    {
        if (!INSTANCE_MAP.containsKey(model))
        {
            JsonActorFactory factory = new JsonActorFactory(model);
            INSTANCE_MAP.put(model, factory);
        }
        return INSTANCE_MAP.get(model).getGson();
    }

}
