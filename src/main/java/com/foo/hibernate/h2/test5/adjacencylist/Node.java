
package com.foo.hibernate.h2.test5.adjacencylist;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name = "NODE")
public class Node {

    @Id
    @Column(name = "UUID")
    private String uuid;

    @Column(name = "PARENT_UUID")
    private String parentUuid;

    @Column(name = "NAME")
    private String name;

    @Transient
    private List<Node> children = new ArrayList<>();

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }
    @Override
    public String toString() {
        //return "Node{" + "uuid=" + uuid + ", parentUuid=" + parentUuid + ", name=" + name + ", children=" + children + '}';
        return "ANode{" + "name=" + name + ", children=" + children + '}';
    }


}





