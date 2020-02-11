package net.icsl.ledar.web.servlets;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import net.icsl.ledar.ejb.model.Logindetails;
import net.icsl.ledar.ejb.service.RegisteredUsersDataService;

/**
 *
 * @author sfagade
 * @date May 19, 2017
 */
public class LedarSessionListener implements HttpSessionListener {

    private static int totalActiveSessions;
    @Inject
    private RegisteredUsersDataService regService;

    public static int getTotalActiveSession() {
        return totalActiveSessions;
    }

    @Override
    public void sessionCreated(HttpSessionEvent arg0) {
        totalActiveSessions++;
        HttpSession session = arg0.getSession();
        Logger.getLogger(LedarSessionListener.class.getName()).log(Level.INFO, "User Joined. Count: {0} -- {1}", new Object[] {totalActiveSessions, session.getAttribute("person")});
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent arg0) {
        totalActiveSessions--;
        HttpSession session = arg0.getSession();

        String session_id = session.getId() + "";
        if(!session_id.isEmpty()) {
        Logindetails loginData = regService.findUserBySessionId(session_id);

        if (loginData != null) {
            loginData.setActiveSessionId(null);
            loginData.setIsLoggedIn(Boolean.FALSE);

            regService.update(loginData, null);
        }
    }
        Logger.getLogger(LedarSessionListener.class.getName()).log(Level.INFO, "User Session destroyed: {0} -- {1}", new Object[] {session_id, session.getAttribute("person")});
    }
}
