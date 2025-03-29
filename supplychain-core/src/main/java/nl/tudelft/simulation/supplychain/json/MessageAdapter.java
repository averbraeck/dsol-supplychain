package nl.tudelft.simulation.supplychain.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import nl.tudelft.simulation.supplychain.content.Message;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;

/**
 * Serialize and deserialize a Message (sub)class with GSON.
 * <p>
 * Copyright (c) 2023-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MessageAdapter implements JsonSerializer<Message>, JsonDeserializer<Message>
{
    /** the model to get, e.g., the Actor map to (de)serialize the message sender and receiver. */
    private final SupplyChainModelInterface model;

    /**
     * Create the Message adapter.
     * @param model the model to get the JsonActorFactory
     */
    public MessageAdapter(final SupplyChainModelInterface model)
    {
        this.model = model;
    }

    /** {@inheritDoc} */
    @Override
    public Message deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
            throws JsonParseException
    {
        try
        {
            JsonObject wrapper = (JsonObject) json;
            JsonElement type = wrapper.get("messageType");
            JsonElement content = wrapper.get("content");
            Type actualType = Class.forName(type.getAsString());
            return JsonActorFactory.instance(this.model).fromJson(content, actualType);
        }
        catch (ClassNotFoundException e)
        {
            throw new JsonParseException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public JsonElement serialize(final Message src, final Type typeOfSrc, final JsonSerializationContext context)
    {
        final JsonObject wrapper = new JsonObject();
        wrapper.addProperty("messageType", src.getClass().getName());
        wrapper.add("content", JsonActorFactory.instance(this.model).toJsonTree(src));
        return wrapper;
    }
}
