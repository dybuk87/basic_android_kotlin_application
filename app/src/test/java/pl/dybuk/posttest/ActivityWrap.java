package pl.dybuk.posttest;

import android.app.Activity;
import org.mockito.Mockito;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.android.controller.ComponentController;
import org.robolectric.util.ReflectionHelpers;

import java.lang.reflect.Field;

public class ActivityWrap {


    public static void replaceComponentInActivityController(ActivityController<?> activityController, Activity activity)
            throws NoSuchFieldException, IllegalAccessException {
        Field componentField = ComponentController.class.getDeclaredField("component");
        componentField.setAccessible(true);
        componentField.set(activityController, activity);
    }

    public static <T extends Activity> T getSpy(ActivityController<T> activityController) {
        T spy = Mockito.spy(activityController.get());
        ReflectionHelpers.setField(activityController, "component", spy);
        return spy;
    }


}
