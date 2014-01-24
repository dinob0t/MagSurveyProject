package au.csiro.magsurvey;

import android.support.v4.app.FragmentActivity;

/**
 * A simple POJO that holds the details about the demo that are used by the List Adapter.
 */
public class MainMenuDetails {
    /**
     * The resource id of the title of the demo.
     */
    public final int titleId;

    /**
     * The resources id of the description of the demo.
     */
    public final int descriptionId;

    /**
     * The demo activity's class.
     */
    public final Class<? extends FragmentActivity> activityClass;

    public MainMenuDetails(
            int titleId, int descriptionId, Class<? extends FragmentActivity> activityClass) {
        this.titleId = titleId;
        this.descriptionId = descriptionId;
        this.activityClass = activityClass;
    }
}
