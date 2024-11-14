package com.xpert.security;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.NamingException;
import javax.naming.directory.InvalidAttributeValueException;
import javax.naming.ldap.LdapContext;

/**
 *
 * @author ayslan
 */
public class ActiveDirectoryTest implements Serializable {

    private static final long serialVersionUID = 7690715185495674651L;

    public static void testConnection(String username, String password) {
        try {
            LdapContext context = ActiveDirectory.getConnection(username, password);
            context.close();
            System.out.println("Success!");
        } catch (AuthenticationException ex) {
            System.out.println(ex.getMessage());
        } catch (CommunicationException ex) {
            System.out.println(ex.getMessage());
        } catch (NamingException ex) {
            Logger.getLogger(ActiveDirectoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void testChangePassword(String username, String oldPass, String newPass) {
        try {
            LdapContext context = ActiveDirectory.getConnection(username, oldPass);
            ActiveDirectory.getUser(username, context).changePassword(oldPass, newPass, context);
            context.close();
            System.out.println("Success!");
        } catch (AuthenticationException ex) {
            System.out.println(ex.getMessage());
        } catch (CommunicationException ex) {
            System.out.println(ex.getMessage());
        } catch (InvalidAttributeValueException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(ActiveDirectoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {

        testConnection("ayslan", "123456");

    }

}
