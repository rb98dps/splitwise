package org.rb98dps.splitwise.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * DTO for group creation
 */
@Data
@AllArgsConstructor
public class CreateGroupRequest {

    String name;
    Integer leaderId;
    List<Integer> users;

}
