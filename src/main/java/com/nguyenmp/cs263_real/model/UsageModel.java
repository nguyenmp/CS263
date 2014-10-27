package com.nguyenmp.cs263_real.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
public class UsageModel implements Serializable {

    /** the internal database key for this item */
    public Long id = null;

    /** the timestamp of when this entry was logged */
    public long timestamp;

    /** true if the user is remotely logged in, false if they are local */
    public boolean isRemote;

    /** the username reported with who */
    public String username;

    /** the computer that this user was logged into */
    public String hostname;
}
