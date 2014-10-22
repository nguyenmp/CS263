package com.nguyenmp.cs263_real.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
public class UserModel implements Serializable {

    /** true if the user is remotely logged in, false if they are local */
    public boolean isRemote;

    /** the username reported with who */
    public String username;
}
