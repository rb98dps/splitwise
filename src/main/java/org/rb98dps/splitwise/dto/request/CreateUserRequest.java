package org.rb98dps.splitwise.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for user creation
 */
@Data
@AllArgsConstructor
public class CreateUserRequest {

    String name;
    String password;
    String phoneNo;

}
