package com.nguyenmp.cs263_real.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
public class Computer implements Serializable {
    public String hostname;
    public String ipAddress;
}
