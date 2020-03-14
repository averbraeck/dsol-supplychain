package nl.tudelft.simulation.yellowpage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import nl.tudelft.simulation.actor.ActorInterface;

/**
 * A YellowPage implementation. <br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class YellowPage implements YellowPageInterface
{
    /** the serial version uid */
    public static final long serialVersionUID = 12L;

    /** a map of entries */
    protected Map<Category, List<ActorInterface>> entries = new LinkedHashMap<Category, List<ActorInterface>>();

    /** the parent of this yellowPage */
    protected YellowPage parent = null;

    /**
     * constructs a new YellowPage
     */
    public YellowPage()
    {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public List<ActorInterface> findActor(final String regex)
    {
        List<ActorInterface> result = new ArrayList<ActorInterface>();
        for (List<ActorInterface> actors : this.entries.values())
        {
            for (ActorInterface actor : actors)
            {
                if (actor.getName().matches(regex))
                {
                    result.add(actor);
                }
            }
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public List<ActorInterface> findActor(final String regex, final Category category)
    {
        List<ActorInterface> result = new ArrayList<ActorInterface>();
        for (Category cat : this.entries.keySet())
        {
            if (Category.specializationOf(category, cat))
            {
                List<ActorInterface> actors = this.entries.get(cat);
                for (ActorInterface actor : actors)
                {
                    if (actor.getName().matches(regex))
                    {
                        result.add(actor);
                    }
                }
            }
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public List<ActorInterface> findActor(final Category category)
    {
        List<ActorInterface> actors = new ArrayList<ActorInterface>();
        for (Category cat : this.entries.keySet())
        {
            if (Category.specializationOf(category, cat))
            {
                actors = this.entries.get(cat);
            }
        }
        return actors;
    }

    /** {@inheritDoc} */
    @Override
    public boolean register(final ActorInterface actor, final Category category)
    {
        List<ActorInterface> actors = this.entries.get(category);
        if (actors == null)
        {
            actors = new ArrayList<ActorInterface>();
            this.entries.put(category, actors);
        }
        return actors.add(actor);
    }
}
