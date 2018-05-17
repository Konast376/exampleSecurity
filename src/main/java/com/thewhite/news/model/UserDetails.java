package com.thewhite.news.model;

import lombok.*;

import javax.xml.ws.soap.Addressing;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Vdovin S. on 17.05.18.
 * <p>
 * TODO: replace on javadoc
 *
 * @author Sergey Vdovin
 * @version 1.0
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetails {
    private UUID id;
    private String userFirstName;
    private String userMiddleName;
    private String userLastName;
    private String userLogin;
    private String postName;
    private Set<String> authorities;
}
