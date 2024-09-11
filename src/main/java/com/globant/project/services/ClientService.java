package com.globant.project.services;

import java.util.List;

import com.globant.project.domain.dto.ClientDTO;
import com.globant.project.domain.entities.ClientEntity;

/**
 * ClientService
 */

public interface ClientService {

    ClientDTO createClient(ClientDTO clientDto);

    Boolean clientExists(String document);

    void updateClient(String document, ClientDTO clientDto);

    void deleteClient(String document);

    ClientDTO getClient(String document);

    List<ClientDTO> getClients();

    ClientEntity getClientEntity(String uuid);
}
