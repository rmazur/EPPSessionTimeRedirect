/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2011, Red Hat Middleware                                         *
 * Regis Mazur <rmazur@redhat.com>                                            *
 * 21-09-2012                                                                 *
 ******************************************************************************/
package org.exoplatform.web.timeout.redirect;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.exoplatform.container.web.AbstractFilter;
import org.gatein.common.logging.Logger;
import org.gatein.common.logging.LoggerFactory;

/**
 * Filter can be used to redirect users to particular page after session timeout.<br />
 * The exact page location to redirect is performed by LoginRedirectService.  
 * 
 * @author <a href="mailto:rmazur@redhat.com">Regis Mazur</a>
 * @version $Revision$
 */
public class LoginRedirectFilter extends AbstractFilter
{
   private static final Logger log = LoggerFactory.getLogger(LoginRedirectFilter.class);

   // We need this helper registry because userId can't be obtained from HTTP session when logout is detected because session is already invalidated.
   // Key is sessionId, Value is userId (HTTP session is invalidated during logout and all attributes are cleaned but session ID is still the same)
   private ConcurrentMap<String, String> loggedUsersRegistry = new ConcurrentHashMap<String, String>();

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
         ServletException
   {      
      HttpServletRequest httpRequest = (HttpServletRequest) request;
      HttpServletResponse httpResponse = (HttpServletResponse) response;

      String newUserName = httpRequest.getRemoteUser();
      String oldUserName = loggedUsersRegistry.get(httpRequest.getSession().getId());
      
      String oldSessionId = null;
      
      Set<String> keys = loggedUsersRegistry.keySet();
      Iterator<String> it = keys.iterator();
      
      while(it.hasNext()){
    	  oldSessionId =(String) it.next(); 
      }
      
      if(oldSessionId != null && !oldSessionId.equals(httpRequest.getSession().getId()) && !(httpRequest.getRequestURI().indexOf("SessionTimeout.jsp") > 0))
      {
    	  log.debug("Detected timeout");
    	  loggedUsersRegistry.remove(oldSessionId);
    	  String location = getLocationAfterLogout();
          location = httpResponse.encodeRedirectURL(location);
          httpResponse.sendRedirect(location);
    	  return;
      }
      // ---------
      
      if ((newUserName != null) && ((oldUserName == null) || (!oldUserName.equals(newUserName))))
      {
         log.debug("Detected login of user " + newUserName);

         if(loggedUsersRegistry.get(httpRequest.getSession().getId()) == null)
         {
         loggedUsersRegistry.put(httpRequest.getSession().getId(), newUserName);
         }
         // ------------
         
      }
      else if (isLogoutRequest(httpRequest))
      {
         log.debug("Detected logout request of user " + oldUserName);
         String sessionId = httpRequest.getSession().getId();
         
         // Remove attribute from local map.
         loggedUsersRegistry.remove(sessionId);
         
      }

      // continue with filter chain for case that login or logout detection did not happen during this HTTP request
      chain.doFilter(request, response);
   }

   // Return true if logout request is in progress
   private boolean isLogoutRequest(HttpServletRequest req)
   {
      String portalComponentId = req.getParameter("portal:componentId");
      String portalAction = req.getParameter("portal:action");
      if (("UIPortal".equals(portalComponentId)) && ("Logout".equals(portalAction)))
      {
         return true;
      }
      else
      {
         return false;
      }
   }

   
   private String getLocationAfterLogout()
   {
      LoginRedirectService loginService = (LoginRedirectService)getContainer().getComponentInstanceOfType(LoginRedirectService.class);
      return loginService.getLogoutRedirectURL();      
   }
  
   public void destroy()
   {
   }
   
}

