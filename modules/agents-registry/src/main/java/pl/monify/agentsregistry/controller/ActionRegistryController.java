package pl.monify.agentsregistry.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.monify.agentsregistry.dto.ActionInfoDto;
import pl.monify.agentsregistry.model.RegisteredActionInstance;
import pl.monify.agentsregistry.repository.RegisteredActionRepository;
import pl.monify.agentsregistry.service.ActionRegistryService;

import java.util.List;

@RestController
@RequestMapping("/api/actions")
public class ActionRegistryController {

    private static final Logger log = LoggerFactory.getLogger(ActionRegistryController.class);
    private final ActionRegistryService service;
    private final RegisteredActionRepository repository;

    public ActionRegistryController(ActionRegistryService service, RegisteredActionRepository repository) {
        this.service = service;
        this.repository = repository;
        log.info("ActionRegistryController initialized");
    }

    @PreAuthorize("hasAuthority('WORKFLOW_VIEW')")
    @GetMapping("/{name}")
    public List<RegisteredActionInstance> getActive(@PathVariable("name") String name) {
        return service.findActiveByName(name);
    }

    @PreAuthorize("hasAuthority('WORKFLOW_VIEW')")
    @GetMapping
    public List<ActionInfoDto> getActiveActions(@RequestParam(name = "teamId", required = false) String teamId) {
        return repository.findAll().stream()
                .filter(RegisteredActionInstance::isActive)
                .filter(a -> teamId == null || teamId.equals(a.getTeamId()))
                .map(a -> new ActionInfoDto(
                        a.getName(),
                        a.getDisplayName(),
                        a.getTeamId(),
                        a.getInputSchema(),
                        a.getOutputSchema()
                ))
                .toList();
    }
}
