package org.openrsc.model.data.definitions;

/**
 * The abstract class EntityDef implements methods for return values which are
 * shared between entities.
 */
public abstract class EntityDef {

    /**
     * The name of the entity
     */
    public String name;
    /**
     * The description of the entity
     */
    public String description;

    /**
     * Returns the name of the entity
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the description of the entity
     *
     * @return
     */
    public String getDescription() {
        return description;
    }
}
