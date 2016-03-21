package com.prodigius.util;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.prodigius.model.Festivity;

@XmlRootElement
public class FestivityList {

    List <Festivity> festivities;

    public List<Festivity> getFestivities() {
            return festivities;
    }

    public void setFestivities(List<Festivity> festivities) {
            this.festivities = festivities;
    }


}
