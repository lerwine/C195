package scheduler.dao;

import static scheduler.AppResourceBundleConstants.RESOURCEKEY_READINGFROMDB;
import scheduler.AppResources;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface LoadingMessageProvider {
    default String getLoadingTitle() {
        return AppResources.getResourceString(RESOURCEKEY_READINGFROMDB);
    }
    
    String getLoadingMessage();
    
}
