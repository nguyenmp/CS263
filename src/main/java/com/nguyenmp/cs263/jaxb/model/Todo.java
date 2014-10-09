package com.nguyenmp.cs263.jaxb.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Todo {
    private String summary, description;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
