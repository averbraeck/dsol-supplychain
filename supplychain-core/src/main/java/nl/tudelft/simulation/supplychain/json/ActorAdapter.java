package nl.tudelft.simulation.supplychain.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.actor.ActorNotFoundException;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;

/**
 * Serialize and deserialize Actor with GSON, without reinstantiating the Actor.
 * <p>
 * Copyright (c) 2023-2025 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ActorAdapter implements JsonSerializer<Actor>, JsonDeserializer<Actor>
{
    /** the model to get, e.g., the Actor map to (de)serialize the message sender and receiver. */
    private final SupplyChainModelInterface model;

    /**
     * Create the Actor adapter.
     * @param model the model to get the Actor map to deserialize the Actor
     */
    public ActorAdapter(final SupplyChainModelInterface model)
    {
        this.model = model;
    }

    @Override
    public Actor deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
            throws JsonParseException
    {
        try
        {
            return this.model.getActor(json.getAsJsonPrimitive().getAsString());
        }
        catch (ActorNotFoundException e)
        {
            throw new JsonParseException(e);
        }
    }

    @Override
    public JsonElement serialize(final Actor src, final Type typeOfSrc, final JsonSerializationContext context)
    {
        return new JsonPrimitive(src.getId());
    }
}
