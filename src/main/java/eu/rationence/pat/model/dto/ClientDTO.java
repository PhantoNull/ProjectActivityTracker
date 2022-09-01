package eu.rationence.pat.model.dto;

import eu.rationence.pat.model.ClientType;
import lombok.Data;

import java.io.Serializable;

@Data
public class ClientDTO implements Serializable {

    private String clientKey;

    private String clientDesc;

    private ClientType clientType;
}
