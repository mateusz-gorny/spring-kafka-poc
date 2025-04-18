package pl.monify.credentialstore.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.monify.credentialstore.dto.CredentialEditDto;
import pl.monify.credentialstore.dto.CredentialViewDto;
import pl.monify.credentialstore.service.CredentialService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/credentials")
public class CredentialController {

    private final CredentialService service;

    public CredentialController(CredentialService service) {
        this.service = service;
    }

    @PreAuthorize("hasAnyAuthority('CREDENTIAL_VIEW', 'CREDENTIAL_ADMIN')")
    @GetMapping
    public List<CredentialViewDto> getAll() {
        boolean masked = isMasked();
        return service.getAll(masked);
    }

    @PreAuthorize("hasAnyAuthority('CREDENTIAL_VIEW', 'CREDENTIAL_ADMIN')")
    @GetMapping("/{id}")
    public CredentialViewDto getById(@PathVariable("id") String id) {
        boolean masked = isMasked();
        return service.getById(id, masked);
    }

    @PreAuthorize("hasAuthority('CREDENTIAL_ADMIN')")
    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable("id") String id) {
        service.delete(id);

        return Map.of("status", "success");
    }

    @PreAuthorize("hasAuthority('CREDENTIAL_ADMIN')")
    @PostMapping
    public CredentialViewDto save(@RequestBody CredentialEditDto dto) {
        return service.save(dto);
    }

    private boolean isMasked() {
        return SecurityContextHolder.getContext().getAuthentication()
                        .getAuthorities().stream()
                        .noneMatch(auth -> auth.getAuthority().equals("CREDENTIAL_ADMIN"));
    }
}
