package pl.monify.credentialstore.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pl.monify.credentialstore.dto.CredentialEditDto;
import pl.monify.credentialstore.dto.CredentialViewDto;
import pl.monify.credentialstore.model.CredentialEntity;
import pl.monify.credentialstore.repository.MongoCredentialRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CredentialService {

    private final MongoCredentialRepository repository;

    public CredentialService(MongoCredentialRepository repository) {
        this.repository = repository;
    }

    public List<CredentialViewDto> getAll(boolean masked) {
        return repository.findAll().stream()
                .map(c -> toViewDto(c, masked))
                .collect(Collectors.toList());
    }

    public CredentialViewDto getById(String id, boolean masked) {
        CredentialEntity entity = repository.findById(id).orElseThrow();
        return toViewDto(entity, masked);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }

    public CredentialViewDto save(CredentialEditDto dto) {
        CredentialEntity entity = new CredentialEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDomain(dto.getDomain());
        entity.setUsername(dto.getUsername());
        entity.setPassword(dto.getPassword());
        entity.setType(dto.getType());
        entity.setExtra(dto.getExtra());

        return toViewDto(repository.save(entity), false);
    }

    private CredentialViewDto toViewDto(CredentialEntity entity, boolean masked) {
        return new CredentialViewDto(
                entity.getId(),
                entity.getName(),
                entity.getDomain(),
                masked ? maskUsername(entity.getUsername()) : entity.getUsername(),
                masked ? "********" : entity.getPassword(),
                entity.getType(),
                entity.getExtra()
        );
    }

    private String maskUsername(String username) {
        if (username == null || username.length() < 2) return "*".repeat(username.length());
        return username.charAt(0) + "*".repeat(username.length() - 2) + username.charAt(username.length() - 1);
    }
}
