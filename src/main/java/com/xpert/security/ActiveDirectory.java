package com.xpert.security;

import java.io.IOException;
import java.util.Hashtable;
import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import static javax.naming.directory.SearchControls.SUBTREE_SCOPE;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.InitialLdapContext;

//Imports for changing password
import javax.naming.directory.ModificationItem;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.InvalidAttributeValueException;
import javax.naming.ldap.StartTlsResponse;
import javax.naming.ldap.StartTlsRequest;
import javax.net.ssl.*;

/**
 * Provides static methods to authenticate users, change passwords, etc. Its
 * based on this work:
 * http://www.javaxt.com/Tutorials/Windows/How_to_Authenticate_Users_with_Active_Directory
 *
 */
public class ActiveDirectory {

    private static final String[] userAttributes = {
        "distinguishedName", "cn", "name", "uid",
        "sn", "givenname", "memberOf", "samaccountname",
        "userPrincipalName"
    };

    private ActiveDirectory() {
    }

    /**
     * Used to authenticate a user given a username/password. Domain name is
     * derived from the fully qualified domain name of the host machine.
     *
     * @param username
     * @param password
     * @return
     * @throws javax.naming.CommunicationException
     * @throws javax.naming.AuthenticationException
     */
    public static LdapContext getConnection(String username, String password) throws CommunicationException, AuthenticationException {
        return getConnection(username, password, null, null);
    }

    /**
     * Used to authenticate a user given a username/password and domain name.
     *
     * @param username
     * @param password
     * @param domainName
     * @return
     * @throws javax.naming.CommunicationException
     * @throws javax.naming.AuthenticationException
     */
    public static LdapContext getConnection(String username, String password, String domainName) throws CommunicationException, AuthenticationException {
        return getConnection(username, password, domainName, null);
    }

    /**
     * Used to authenticate a user given a username/password and domain name.
     * Provides an option to identify a specific a Active Directory server.
     *
     * @param username
     * @param password
     * @param domainName
     * @param serverName
     * @return
     * @throws javax.naming.CommunicationException
     * @throws javax.naming.AuthenticationException
     */
    public static LdapContext getConnection(String username, String password, String domainName, String serverName) throws CommunicationException, AuthenticationException {

        if (domainName == null) {
            try {
                String fqdn = java.net.InetAddress.getLocalHost().getCanonicalHostName();
                if (fqdn.split("\\.").length > 1) {
                    domainName = fqdn.substring(fqdn.indexOf(".") + 1);
                }
            } catch (java.net.UnknownHostException e) {
            }
        }

        //System.out.println("Authenticating " + username + "@" + domainName + " through " + serverName);
        if (password != null) {
            password = password.trim();
            if (password.length() == 0) {
                password = null;
            }
        }

        //bind by using the specified username/password
        Hashtable props = new Hashtable();
        String principalName = username + "@" + domainName;
        props.put(Context.SECURITY_PRINCIPAL, principalName);
        if (password != null) {
            props.put(Context.SECURITY_CREDENTIALS, password);
        }

        String ldapURL = "ldap://" + ((serverName == null) ? domainName : serverName + "." + domainName) + '/';
        props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        props.put(Context.PROVIDER_URL, ldapURL);
        try {
            return new InitialLdapContext(props, null);
        } catch (CommunicationException e) {
            throw new CommunicationException("Failed to connect to " + domainName + ((serverName == null) ? "" : " through " + serverName));
        } catch (AuthenticationException e) {
            throw new AuthenticationException("Failed to authenticate " + username + "@" + domainName + ((serverName == null) ? "" : " through " + serverName));
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Used to check whether a username is valid.
     *
     * @param username A username to validate (e.g. "peter", "peter@acme.com",
     * or "ACME\peter").
     * @param context
     * @return
     */
    public static User getUser(String username, LdapContext context) {
        try {
            String domainName = null;
            if (username.contains("@")) {
                username = username.substring(0, username.indexOf("@"));
                domainName = username.substring(username.indexOf("@") + 1);
            } else if (username.contains("\\")) {
                username = username.substring(0, username.indexOf("\\"));
                domainName = username.substring(username.indexOf("\\") + 1);
            } else {
                String authenticatedUser = (String) context.getEnvironment().get(Context.SECURITY_PRINCIPAL);
                if (authenticatedUser.contains("@")) {
                    domainName = authenticatedUser.substring(authenticatedUser.indexOf("@") + 1);
                }
            }

            if (domainName != null) {
                String principalName = username + "@" + domainName;
                SearchControls controls = new SearchControls();
                controls.setSearchScope(SUBTREE_SCOPE);
                controls.setReturningAttributes(userAttributes);
                NamingEnumeration<SearchResult> answer = context.search(toDC(domainName), "(& (userPrincipalName=" + principalName + ")(objectClass=user))", controls);
                if (answer.hasMore()) {
                    Attributes attr = answer.next().getAttributes();
                    Attribute user = attr.get("userPrincipalName");
                    if (user != null) {
                        return new User(attr);
                    }
                }
            }
        } catch (NamingException e) {
            //e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns a list of users in the domain.
     *
     * @param context
     * @return
     * @throws javax.naming.NamingException
     */
    public static User[] getUsers(LdapContext context) throws NamingException {

        java.util.ArrayList<User> users = new java.util.ArrayList<User>();
        String authenticatedUser = (String) context.getEnvironment().get(Context.SECURITY_PRINCIPAL);
        if (authenticatedUser.contains("@")) {
            String domainName = authenticatedUser.substring(authenticatedUser.indexOf("@") + 1);
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SUBTREE_SCOPE);
            controls.setReturningAttributes(userAttributes);
            NamingEnumeration answer = context.search(toDC(domainName), "(objectClass=user)", controls);
            try {
                while (answer.hasMore()) {
                    Attributes attr = ((SearchResult) answer.next()).getAttributes();
                    Attribute user = attr.get("userPrincipalName");
                    if (user != null) {
                        users.add(new User(attr));
                    }
                }
            } catch (Exception e) {
            }
        }
        return users.toArray(new User[users.size()]);
    }

    private static String toDC(String domainName) {
        StringBuilder buf = new StringBuilder();
        for (String token : domainName.split("\\.")) {
            if (token.length() == 0) {
                continue;   // defensive check
            }
            if (buf.length() > 0) {
                buf.append(",");
            }
            buf.append("DC=").append(token);
        }
        return buf.toString();
    }

    //** User Class
    //*************************************************************************/
    /**
     * Used to represent a User in Active Directory
     */
    public static class User {

        private final String distinguishedName;
        private final String userPrincipal;
        private final String commonName;
        private final Attributes attributes;

        public User(Attributes attr) throws javax.naming.NamingException {
            userPrincipal = (String) attr.get("userPrincipalName").get();
            commonName = (String) attr.get("cn").get();
            distinguishedName = (String) attr.get("distinguishedName").get();
            attributes = attr;
        }

        public Attributes getAttributes() {
            return attributes;
        }

        public String getUserPrincipal() {
            return userPrincipal;
        }

        public String getCommonName() {
            return commonName;
        }

        public String getDistinguishedName() {
            return distinguishedName;
        }

        public String toString() {
            return getDistinguishedName();
        }

        /**
         * Used to change the user password. Throws an IOException if the Domain
         * Controller is not LDAPS enabled.
         *
         *
         * @param oldPass
         * @param newPass
         * @param context
         * @throws java.io.IOException
         * @throws NamingException
         */
        public void changePassword(String oldPass, String newPass, LdapContext context)
                throws java.io.IOException, NamingException {
            changePassword(oldPass, newPass, true, context);
        }

        /**
         * Used to change the user password. Throws an IOException if the Domain
         * Controller is not LDAPS enabled.
         *
         * @param oldPass
         * @param newPass
         * @param trustAllCerts If true, bypasses all certificate and host name
         * validation. If false, ensure that the LDAPS certificate has been
         * imported into a trust store and sourced before calling this method.
         * Example: String keystore =
         * "/usr/java/jdk1.5.0_01/jre/lib/security/cacerts";
         * System.setProperty("javax.net.ssl.trustStore",keystore);
         *
         *
         * @param context
         * @throws java.io.IOException
         * @throws javax.naming.directory.InvalidAttributeValueException
         * @throws javax.naming.NamingException
         */
        public void changePassword(String oldPass, String newPass, boolean trustAllCerts, LdapContext context)
                throws IOException, InvalidAttributeValueException, NamingException {
            String dn = getDistinguishedName();

            //Switch to SSL/TLS
            StartTlsResponse tls = null;
            try {
                tls = (StartTlsResponse) context.extendedOperation(new StartTlsRequest());
            } catch (Exception e) {
                //"Problem creating object: javax.naming.ServiceUnavailableException: [LDAP: error code 52 - 00000000: LdapErr: DSID-0C090E09, comment: Error initializing SSL/TLS, data 0, v1db0"
                throw new IOException("Failed to establish SSL connection to the Domain Controller. Is LDAPS enabled?");
            }

            //Exchange certificates
            if (trustAllCerts) {
                tls.setHostnameVerifier(DO_NOT_VERIFY);
                SSLSocketFactory sf = null;
                try {
                    SSLContext sc = SSLContext.getInstance("TLS");
                    sc.init(null, TRUST_ALL_CERTS, null);
                    sf = sc.getSocketFactory();
                } catch (java.security.NoSuchAlgorithmException e) {
                } catch (java.security.KeyManagementException e) {
                }
                tls.negotiate(sf);
            } else {
                tls.negotiate();
            }

            //Change password
            try {
                //ModificationItem[] modificationItems = new ModificationItem[1];
                //modificationItems[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("unicodePwd", getPassword(newPass)));

                ModificationItem[] modificationItems = new ModificationItem[2];
                modificationItems[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute("unicodePwd", getPassword(oldPass)));
                modificationItems[1] = new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute("unicodePwd", getPassword(newPass)));
                context.modifyAttributes(dn, modificationItems);
            } catch (InvalidAttributeValueException e) {
                String error = e.getMessage().trim();
                if (error.startsWith("[") && error.endsWith("]")) {
                    error = error.substring(1, error.length() - 1);
                }
                tls.close();
                throw new InvalidAttributeValueException(
                        "New password does not meet Active Directory requirements. "
                        + "Please ensure that the new password meets password complexity, "
                        + "length, minimum password age, and password history requirements."
                );
            } catch (NamingException e) {
                tls.close();
                throw e;
            }

            //Close the TLS/SSL session
            tls.close();
        }

        private static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        private static TrustManager[] TRUST_ALL_CERTS = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };

        private byte[] getPassword(String newPass) {
            String quotedPassword = "\"" + newPass + "\"";
            //return quotedPassword.getBytes("UTF-16LE");
            char unicodePwd[] = quotedPassword.toCharArray();
            byte pwdArray[] = new byte[unicodePwd.length * 2];
            for (int i = 0; i < unicodePwd.length; i++) {
                pwdArray[i * 2 + 1] = (byte) (unicodePwd[i] >>> 8);
                pwdArray[i * 2 + 0] = (byte) (unicodePwd[i] & 0xff);
            }
            return pwdArray;
        }
    }
}
