package org.cloud.provider.entity;


import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "SOS_TRIPS")
public class Trips implements java.io.Serializable {
    /***/
    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 50)
    private String id;
    private String start;
    private String end;
}
