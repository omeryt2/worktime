package eu.vranckaert.worktime.utils.view.actionbar;

import android.app.Activity;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 30/10/13
 * Time: 10:09
 *
 * @author Dirk Vranckaert
 */
public class SyncDelegate {
    private static SyncDelegate instance;
    private List<SyncDelegateListener> syncDelegates;

    private SyncDelegate() {}

    public static SyncDelegate get() {
        if (instance == null) {
            instance = new SyncDelegate();
            instance.syncDelegates = new ArrayList<SyncDelegateListener>();
        }
        return instance;
    }

    public void registerDelegate(Activity activity) {
        if (activity instanceof SyncDelegateListener) {
            syncDelegates.add((SyncDelegateListener) activity);
        }
    }

    public void unregisterDelegate(Activity activity) {
        if (activity instanceof SyncDelegateListener) {
            syncDelegates.remove((SyncDelegateListener) activity);
        }
    }

    public void delegateEndOfSync(final boolean success) {
        for (final SyncDelegateListener syncDelegate : syncDelegates) {
            Runnable syncDelegateCompleted = new Runnable() {
                @Override
                public void run() {
                    syncDelegate.onSyncCompleted(success);
                }
            };

            if (syncDelegate instanceof Activity) {
                ((Activity)syncDelegate).runOnUiThread(syncDelegateCompleted);
            } else if (syncDelegate instanceof Fragment) {
                ((Fragment)syncDelegate).getActivity().runOnUiThread(syncDelegateCompleted);
            } else {
                syncDelegateCompleted.run();
            }
        }
    }
}
