package client.entityhandling.defs;

/**
 * Base class for objects containing data loaded from XML.
 *
 * The field names in this class (and child classes) reflect the named
 * parameters present in the game's XML files, so they cannot be changed
 * unless the corresponding XML files are changed as well.
 *
 * Similarly, any changes to the package or class name must be reflected in the
 * initialisation of the Resources class, where aliases are set up for the
 * XML parser.
 */
public abstract class EntityDef {

    /**
     * The name of the entity.
     */
    public String name;

    /**
     * The description of the entity.
     */
    public String description;

    /**
     * Returns the name of the entity.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the description of the entity.
     */
    public String getDescription() {
        return description;
    }

}
