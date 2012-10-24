/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2011, Red Hat Middleware                                         *
 * Regis Mazur <rmazur@redhat.com>                                            *
 * 21-09-2012                                                                 *
 ******************************************************************************/
package org.exoplatform.web.timeout.redirect;

import org.exoplatform.container.xml.InitParams;

/**
 * This service contains default URL to use when session timed-out.
 * 
 * @author <a href="mailto:rmazur@redhat.com">Regis Mazur</a>
 * @version $Revision$
 */
public class LoginRedirectService
{
   private final String defaultLogoutURL;
        
   public LoginRedirectService(InitParams params)
   {
      
      this.defaultLogoutURL = params.getValueParam("defaultLogoutURL").getValue();
      
   }
   
   /**
    * Return the page where particular user should be redirected after his session timed-out.
    * 
    * @return page to redirect
    */
   
   public String getLogoutRedirectURL()
   {
      return defaultLogoutURL;             
   }     

}

