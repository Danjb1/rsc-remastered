package client.entityhandling.defs;

/**
 * Data relating to a prayer.
 */
public class PrayerDef extends EntityDef {

    /**
     * The minimum Prayer level required to use the prayer.
     */
    public int reqLevel;

    /**
     * The drain rate of the prayer (units unknown - possibly points per min).
     */
    public int drainRate;

    public int getReqLevel() {
        return reqLevel;
    }

    public int getDrainRate() {
        return drainRate;
    }
}
