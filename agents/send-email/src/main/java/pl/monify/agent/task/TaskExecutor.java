package pl.monify.agent.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskExecutor {

    private static final Logger log = LoggerFactory.getLogger(TaskExecutor.class);

    public void execute() {
        try {
            log.info("[AGENT] Executing task...");
            Thread.sleep(5000);
            log.info("[AGENT] Task completed.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
