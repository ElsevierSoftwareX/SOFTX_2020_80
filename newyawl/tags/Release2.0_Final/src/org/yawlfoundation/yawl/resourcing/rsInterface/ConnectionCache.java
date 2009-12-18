package org.yawlfoundation.yawl.resourcing.rsInterface;

import java.util.*;

/**
 * An extended HashMap to handle connections from external entities (such as the YAWL
 * Editor) and the resource service. The map is of the form [sessionHandle, connection].
 *
 * Author: Michael Adams
 * Date: Oct 24, 2007
 * Time: 9:47:08 AM
 * Version: 0.1
 *
 */


public class ConnectionCache extends HashMap<String,ServiceConnection> {

    private static ConnectionCache _me ;
    private HashMap<String,String> _userdb ;


    public ConnectionCache() {
        super();
        initUserDB() ;
        _me = this ;
    }

    /******************************************************************************/

    // PUBLIC METHODS //   

    public static ConnectionCache getInstance() {
        if (_me == null) _me = new ConnectionCache() ;
        return _me ;
    }

    public String connect(String userid, String password) {
        String result ;
   //     if (! connected(userid)) {
            if (validUser(userid))  {
                if (validPassword(userid, password)) {
                    ServiceConnection con = new ServiceConnection(userid) ;
                    result = con.getHandle();
                    this.put(result, con);
                }
                else result = failMsg("Incorrect Password");
            }
            else result = failMsg(String.format("Unknown Username: '%s'", userid));
 //       }
 //       else result = failMsg(String.format("User '%s' is already connected", userid));
        
        return result ;
    }


    public void disconnect(String handle) {
        this.remove(handle);
    }

    public boolean checkConnection(String handle) {
        boolean result = false;
        if (handle != null) {
            ServiceConnection con = this.get(handle) ;
            if (con != null) {
                con.resetActivityTimer();
                result = true ;
            }
        }
        return result ;
    }

    /******************************************************************************/

    // PRIVATE METHODS //

    private boolean connected(String userid) {
        Collection<ServiceConnection> cons = this.values() ;
        for (ServiceConnection con : cons)
            if (con.getUserID().equals(userid)) return true ;
        return false;
    }

    
    private boolean validPassword(String userid, String password) {
        return _userdb.get(userid).equals(password);
    }


    private boolean validUser(String userid) {
        return _userdb.containsKey(userid) ;

    }

    private void initUserDB() {
        _userdb = new HashMap<String,String>();

        // insert default users
        _userdb.put("admin", "YAWL");
        _userdb.put("editor", "YAWL");
    }


    private String failMsg(String msg) {
        return String.format("<failure>%s</failure>", msg) ;
    }



}
