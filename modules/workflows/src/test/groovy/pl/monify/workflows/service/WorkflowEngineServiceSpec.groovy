package pl.monify.workflows.service

import org.springframework.kafka.core.KafkaTemplate
import pl.monify.workflows.messaging.ActionExecutionRequestMessage
import pl.monify.workflows.model.*
import spock.lang.Specification

class WorkflowEngineServiceSpec extends Specification {

    def "should send message to Kafka if agentId is present"() {
        given:
        def kafkaTemplate = Mock(KafkaTemplate)
        def instanceService = Mock(WorkflowInstanceService)
        def service = new WorkflowEngineService(instanceService, kafkaTemplate)

        def action = new WorkflowActionDefinition(
                "a1", "type", "send-email", null,
                [email: "test@example.com"], null, "agent-xyz"
        )

        def defn = new WorkflowDefinition()
        defn.setId("wf1")
        defn.setName("Test WF")
        defn.setStatus(WorkflowDefinition.WorkflowStatus.ACTIVE)
        defn.setActions([action])

        def instance = new WorkflowInstance()
        instance.setTeamId("team-abc")

        when:
        service.execute(instance, defn)

        then:
        instance.getStatus() == WorkflowInstance.Status.IN_PROGRESS
        instance.getActions().size() == 1
        instance.getActions()[0].status == "QUEUED"

        1 * kafkaTemplate.send("action.execution.request", _ as ActionExecutionRequestMessage)
        1 * instanceService.save(_)
    }

    def "should mark action as FAILED if agentId is missing"() {
        given:
        def kafkaTemplate = Mock(KafkaTemplate)
        def instanceService = Mock(WorkflowInstanceService)
        def service = new WorkflowEngineService(instanceService, kafkaTemplate)

        def action = new WorkflowActionDefinition(
                "a2", "type", "scrape-html", null,
                [url: "https://example.com"], null, null // missing agentId
        )

        def defn = new WorkflowDefinition()
        defn.setId("wf2")
        defn.setName("Broken WF")
        defn.setStatus(WorkflowDefinition.WorkflowStatus.ACTIVE)
        defn.setActions([action])

        def instance = new WorkflowInstance()
        instance.setTeamId("team-abc")

        when:
        service.execute(instance, defn)

        then:
        instance.getActions().size() == 1
        instance.getActions()[0].status == "FAILED"
        instance.getActions()[0].log.contains("Missing agentId")

        0 * kafkaTemplate.send(_, _)
        1 * instanceService.save(_)
    }
}
