package webarch.com.hablar.WidgetHelpers;

/**
 * Created by ajitesh on 4/11/16.
 */

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * WidgetService is the {@link RemoteViewsService} that will return our RemoteViewsFactory
 */
public class HablarWidgetRemoteViewService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetDataProvider(this, intent);
    }
}
