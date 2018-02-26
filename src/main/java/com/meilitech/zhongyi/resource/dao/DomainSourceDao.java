
package com.meilitech.zhongyi.resource.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

import java.util.UUID;

/**
 * Sample user class.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @author Mark Paluch
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "domain_source")
public class DomainSourceDao {


    public static final int STATUS_OK = 0;
    public static final int STATUS_DEL = -1;

    @PrimaryKey("id")
    private UUID id;
    @Column("domain")
    private String domain;
    @Column("status")
    private int status;


    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}