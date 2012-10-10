How to redirect a user to certain page on HTTP session timeout.
------------------------------------------------------------------------------------------
This is based on another plugin named "exo.portal.redirectplugin"
https://community.jboss.org/wiki/RedirectAUserToPageOnLoginlogoutBasedOnRole

How to build and deploy the application:

1) Plugin can be compiled simply by command:

mvn clean install

2) File target/exo.portal.session.timeout.redirectplugin-3.2.0-SNAPSHOT.jar  needs to be added to GateIn portal into ../deploy/gatein.ear/lib/
This JAR contains needed java classes, especially LoginRedirectFilter and LoginRedirectService.

3) Another step is to configure HTTP filter in portal web.xml . This can be done by adding Filter definition and Filter mapping into particular sections of file $GATEIN_HOME/server/default/deploy/gatein.ear/02portal.war/WEB-INF/web.xml
Assumption is that "filter" is inserted after all other filter definitions: 

   <filter>
      <filter-name>LoginRedirectFilter</filter-name>
      <filter-class>org.exoplatform.web.timeout.redirect.LoginRedirectFilter</filter-class>
   </filter>

And another assumption is that "filter-mapping" is inserted after all other filter-mapping definitions: 

   <filter-mapping>
      <filter-name>LoginRedirectFilter</filter-name>
      <url-pattern>/*</url-pattern>
   </filter-mapping>

4) LoginRedirectFilter is using LoginRedirectService to find the correct URL to redirect users. 
Update existing file under ../deploy/gatein.ear/02portal.war/WEB-INF/conf/common/common-configuration.xml.
Example configuration is below.
- defaultLogoutURL  - URL to redirect after session timeout is detected

So example of mapping is here:

  <!-- Component used by LoginRedirectFilter Filter -->
  <component>
        <key>org.exoplatform.web.timeout.redirect.LoginRedirectService</key>
        <type>org.exoplatform.web.timeout.redirect.LoginRedirectService</type>
        <init-params>
            <value-param>
                <name>defaultLogoutURL</name>
                <description>logout URL to redirect if group mapping can't be found for particular user</description>
                <value>/portal/timeout/jsp/SessionTimeout.jsp</value>
            </value-param>
        </init-params>
  </component>
  
5) So after update of mapping configuration in common-configuration.xml and filter configuration in web.xml, we can start the portal.
We can try predefined users and we will see that:

NOTE: This version is working with GateIn 3.2 or EPP 5.2.