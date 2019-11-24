/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import entity.DbUser;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.QueryTimeoutException;

/**
 *
 * @author Leonard T. Erwine
 */
public class AppContext {
    public static final AppContext DEFAULT_CONTEXT = new AppContext();

    @PersistenceUnit
    EntityManagerFactory emf;
    
    private Optional<EmDependency> latestEmDependency = Optional.empty();
    
    public static class EmDependency {
        private AppContext appContext;
        private EntityManager em;
        private Optional<EmDependency> previous;
        private Optional<EmDependency> next;

        public EmDependency() {
            previous = Optional.empty();
            next = Optional.empty();
        }

        public EntityManager open(AppContext context) throws InvalidOperationException {
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
    
    //<editor-fold defaultstate="collapsed" desc="User Lookup Methods">
    
    public static Optional<DbUser> getUserByUserName(EntityManager em, String userName) {
        DbUser user;
        try {
            user = (DbUser)em.createNamedQuery(DbUser.NAMED_QUERY_BY_USERNAME)
                    .setParameter(DbUser.PARAMETER_NAME_USERNAME, userName).getSingleResult();
        } catch (NoResultException ex) {
            // No user found.
            user = null;
            Logger.getLogger(AppContext.class.getName()).log(Level.WARNING, null, ex);
        }
        if (user == null)
            return Optional.empty();
        return Optional.of(user);
    }
    
    public static Optional<DbUser> getUserByUserId(EntityManager em, int userId) {
        DbUser user;
        try {
            user = (DbUser)em.createNamedQuery(DbUser.NAMED_QUERY_BY_USERID)
                    .setParameter(DbUser.PARAMETER_NAME_USERID, userId).getSingleResult();
        } catch (NoResultException ex) {
            // No user found.
            user = null;
            Logger.getLogger(AppContext.class.getName()).log(Level.WARNING, null, ex);
        }
        if (user == null)
            return Optional.empty();
        return Optional.of(user);
    }
    
    private DbUser CURRENT_USER = null;
    
    public DbUser getCurrentUser() { return CURRENT_USER; }
    
    public boolean trySetCurrentUser(EntityManager em, String userName, String password) {
        Optional<DbUser> user;
        try {
            user = getUserByUserName(em, userName);
        } catch (QueryTimeoutException | NonUniqueResultException ex) {
            utils.NotificationHelper.showNotificationDialog("Login", "Login Error", "Database access error", Alert.AlertType.ERROR);
            Logger.getLogger(AppContext.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        if (user.isPresent()) {
            DbUser u = user.get();
            try {
                // Check if we got a user from the DB and if the password hash matches.
                if ((new PwHash(u.getPassword(), false)).test(password)) {
                    CURRENT_USER = u;
                    return true;
                }
            } catch (InvalidArgumentException ex) {
                Logger.getLogger(AppContext.class.getName()).log(Level.WARNING, null, ex);
            }
        } else {
            try {
                Logger.getLogger(AppContext.class.getName()).log(Level.SEVERE, "Hash: {0}", (new PwHash(password, true)).toString());
            } catch (InvalidArgumentException ex) {
                Logger.getLogger(AppContext.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        utils.NotificationHelper.showNotificationDialog("Login", "Invalid login", "Invalid username or password", Alert.AlertType.WARNING);
        return false;
    }
    
    //</editor-fold>
}
