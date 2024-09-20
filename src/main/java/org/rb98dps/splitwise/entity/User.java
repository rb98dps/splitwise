package org.rb98dps.splitwise.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class User {

    private static final AtomicInteger atomicInteger = new AtomicInteger(0);
    private String name;
    private String password;

    private String phNo;
    private int userId;
    private List<Group> groups;

    public User(String name, String password, String phNo) {
        this.name = name;
        this.password = password;
        this.phNo = phNo;
        userId = atomicInteger.getAndIncrement();
    }

    @Override
    public String toString() {
        return "User{" +
                       "name='" + name + '\'' +
                       ", password='" + password + '\'' +
                       ", phNo='" + phNo + '\'' +
                       ", userId=" + userId +
                       ", groups=" + groups +
                       '}';
    }


}
