package scheduler.dao.filter.value;

import java.util.logging.Level;
import java.util.logging.Logger;
import scheduler.util.LogHelper;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@SuppressWarnings("ClassWithMultipleLoggers")
class Loggers {

    static final Logger BVF_LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(BooleanValueFilter.class.getName()), Level.FINER);
//        static final Logger LOG = Logger.getLogger(BooleanValueFilter.class.getName());

    static final Logger IVF_LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(IntValueFilter.class.getName()), Level.FINER);
//        static final Logger LOG = Logger.getLogger(IntValueFilter.class.getName());

    static final Logger SVF_LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(StringValueFilter.class.getName()), Level.FINER);
//        static final Logger LOG = Logger.getLogger(StringValueFilter.class.getName());

    static final Logger TVF_LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(TimestampValueFilter.class.getName()), Level.FINER);
//        static final Logger LOG = Logger.getLogger(TimestampValueFilter.class.getName());

    private Loggers() {
    }

}
