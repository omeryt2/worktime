package eu.vranckaert.worktime.service;

import android.app.Activity;
import eu.vranckaert.worktime.exceptions.GooglePlayServiceRequiredException;

/**
 * Date: 29/10/13
 * Time: 09:20
 *
 * @author Dirk Vranckaert
 */
public interface GCMService {
    void updateGCMConfiguration() throws GooglePlayServiceRequiredException;
}
