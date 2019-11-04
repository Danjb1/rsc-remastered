package client.entityhandling.defs;

/**
 * Data relating to an animation.
 */
public class AnimationDef {

    public String name;
    public int charColour;
    public int genderModel;
    public boolean hasA;
    public boolean hasF;
    public int number;

    public String getName() {
        return name;
    }

    public int getCharColour() {
        return charColour;
    }

    public int getGenderModel() {
        return genderModel;
    }

    public boolean hasA() {
        return hasA;
    }

    public boolean hasF() {
        return hasF;
    }

    public int getNumber() {
        return number;
    }

}