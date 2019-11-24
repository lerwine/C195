/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import entity.User;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.persistence.QueryTimeoutException;

/**
 * Current application context information.
 * @author Leonard T. Erwine
 */
public class SchedulerContext {
    /**
     * The default context object.
     */
    public static final SchedulerContext DEFAULT_CONTEXT = new SchedulerContext();

    @PersistenceUnit
    private EntityManagerFactory emf;
    
    private User CURRENT_USER = null;
    
    /**
     * Gets the currently logged in user.
     * @return The currently logged in user.
     */
    public User getCurrentUser() { return CURRENT_USER; }
    
    private Optional<EmDependency> latestEmDependency = Optional.empty();
    
    //<editor-fold defaultstate="collapsed" desc="User Lookup Methods">
    
    /**
     * Finds a user by the user name.
     * @param em        An {@link EntityManager} object used to retrieve data from the database.
     * @param userName  The user's login name.
     * @return The user that was found or empty if no user was found.
     */
    public static Optional<User> getUserByUserName(EntityManager em, String userName) {
        User user;
        try {
            user = (User)em.createNamedQuery(User.NAMED_QUERY_BY_USERNAME)
                    .setParameter(User.PARAMETER_NAME_USERNAME, userName).getSingleResult();
        } catch (NoResultException ex) {
            // No user found.
            user = null;
            Logger.getLogger(SchedulerContext.class.getName()).log(Level.WARNING, null, ex);
        }
        if (user == null)
            return Optional.empty();
        return Optional.of(user);
    }
    
    /**
     * Finds a user by the primary key value.
     * @param em        An {@link EntityManager} object used to retrieve data from the database.
     * @param userId    The database primary key value.
     * @return The user that was found or empty if no user was found.
     */
    public static Optional<User> getUserByUserId(EntityManager em, int userId) {
        User user;
        try {
            user = (User)em.createNamedQuery(User.NAMED_QUERY_BY_ID)
                    .setParameter(User.PARAMETER_NAME_USERID, userId).getSingleResult();
        } catch (NoResultException ex) {
            // No user found.
            user = null;
            Logger.getLogger(SchedulerContext.class.getName()).log(Level.WARNING, null, ex);
        }
        if (user == null)
            return Optional.empty();
        return Optional.of(user);
    }
    
    /**
     * Sets the currently logged in user if a user name and password match.
     * @param em        An {@link EntityManager} object used to retrieve data from the database.
     * @param userName  The user's login name.
     * @param password  The user's actual password (not password hash).
     * @return {@code true} if the user was logged in; otherwise {@code false}.
     */
    public boolean trySetCurrentUser(EntityManager em, String userName, String password) {
        Optional<User> user;
        try {
            user = getUserByUserName(em, userName);
        } catch (QueryTimeoutException | NonUniqueResultException ex) {
            utils.NotificationHelper.showNotificationDialog("Login", "Login Error", "Database access error", Alert.AlertType.ERROR);
            Logger.getLogger(SchedulerContext.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        if (user.isPresent()) {
            User u = user.get();
            try {
                // Check if we got a user from the DB and if the password hash matches.
                if ((new PwHash(u.getPassword(), false)).test(password)) {
                    CURRENT_USER = u;
                    return true;
                }
            } catch (InvalidArgumentException ex) {
                Logger.getLogger(SchedulerContext.class.getName()).log(Level.WARNING, null, ex);
            }
        } else {
            try {
                Logger.getLogger(SchedulerContext.class.getName()).log(Level.SEVERE, "Hash: {0}", (new PwHash(password, true)).toString());
            } catch (InvalidArgumentException ex) {
                Logger.getLogger(SchedulerContext.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        utils.NotificationHelper.showNotificationDialog("Login", "Invalid login", "Invalid username or password", Alert.AlertType.WARNING);
        return false;
    }
    
    //</editor-fold>
    
    /**
     * Class used to open an SQL connection dependency through an {@link EntityManagerFactory}
     * which will only be open while there is an active dependency.
     */
    public static class EmDependency {
        private SchedulerContext appContext;
        private EntityManager em;
        private Optional<EmDependency> previous;
        private Optional<EmDependency> next;

        /**
         * Creates a new unopened EmDependency instance.
         */
        public EmDependency() {
            previous = Optional.empty();
            next = Optional.empty();
        }

        /**
         * Opens a new SQL connection dependency and returns an {@link EntityManager}.
         * @param context The application context.
         * @return An {@link EntityManager}.
         * @throws InvalidOperationException 
         */
        public EntityManager open(SchedulerContext context) throws InvalidOperationException {
            if (previous.isPresent() || next.isPresent())
                throw new InvalidOperationException("Entity manager dependency is already open.");
            if ((previous = context.latestEmDependency).isPresent()) {
                EmDependency d = previous.get();
                if (d == this)
                    throw new InvalidOperationException("Entity manager dependency is already open.");
                (appContext = context).latestEmDependency = d.next = Optional.of(this);
            } else {
                context.emf = Persistence.createEntityManagerFactory("SchedulerPU");
                (appContext = context).latestEmDependency = Optional.of(this);
            }
            em = context.emf.createEntityManager();
            return em;
        }

        /**
         * Closes the SQL dependency and closes the {@link EntityManager} that was returned by the
         * {@link #open(utils.SchedulerContext)} method.
         * If this was the last remaining dependency, then the associated {@link EntityManagerFactory} will be closed as well.
         */
        public void close() {
            em.close();
            em = null;
            if (next.isPresent()) {
                if ((next.get().previous = previous).isPresent()) {
                    previous.get().next = next;
                    previous = Optional.empty();
                }
                next = Optional.empty();
            } else if ((appContext.latestEmDependency = previous).isPresent())
                previous = previous.get().next = Optional.empty();
            else
                appContext.emf.close();
                
            appContext = null;
        }
    }
}
