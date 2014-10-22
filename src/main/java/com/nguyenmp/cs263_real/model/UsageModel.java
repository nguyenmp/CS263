package com.nguyenmp.cs263_real.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
public class UsageModel implements Serializable {

    /** the timestamp of when this entry was logged */
    public long timestamp;

    /** the user this entry was for */
    public UserModel user;

    /** the computer that this user was logged into */
    public Computer computer;
}
