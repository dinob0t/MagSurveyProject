package au.csiro.magsurvey;

/**
 * A list of all the demos we have available.
 */
public final class MainMenuDetailsList {

    /** This class should not be instantiated. */
    private MainMenuDetailsList() {}

    public static final MainMenuDetails[] MAINMENU = {
            new MainMenuDetails(R.string.map_activity_label,
                    R.string.map_activity_description,
                    MapActivity.class),
            new MainMenuDetails(R.string.displaymap_activity_label,
            R.string.displaymap_activity_description,
            DisplayMapActivity.class)
    };
}
