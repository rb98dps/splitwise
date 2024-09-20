package org.rb98dps.splitwise.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for Add user to group
 */
@Data
@AllArgsConstructor
public class AddUserToGroupRequest {

    private Integer groupId;
    private Integer userId;
    private Integer leaderId;

}
