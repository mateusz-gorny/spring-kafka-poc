# Monify

Monify is a Spring boot application between monolith and microservices. It allows the user to create agents with some actions to send an email. It uses websocket to communicate between agent and application, and it uses the kafka for communication between application modules.

Application modules:
- Agent-gateway is a webhook spring boot application that is a public endpoint to be able to communicate with agent in both directions. Agent is registered in the application using JWT token, and then it is allowed to be triggered by UI.
- Application is a standard spring application with a security module. It configures all other modules, it registers public endpoints, it allows users and agents to log in to the application.
- modules - is a folder for modules in an application
- - agents—Here we are able to put some actions in the agent. Send-email is some example agent. It connects to the websocket, register agent, and then in ui we can trigger it.
- - agents-registry - is a module that allows registering agents into application. Here we have an endpoint which allows UI to show agents also it stores agent information.
- - agents-token - module responsible for the token-related code. It creates a token, validates it and stores it.
- - triggers—module responsible for communication with the frontend to show triggers and to allow to create trigger. When UI fire triggers, this module sends events to the kafka to start the workflow.
- - user - here is code for user configuration and manipulation
- - workflows—it connects actions one by one. It stores the information about actions, about the status of the workflow and actions.
- UI/Frontend - here we have React application to utilize different functionalities.

## Agent ↔ Agent-Gateway ↔ Workflows - Connection Guide

### 📌 Diagram komunikacji

```
+---------------------+          WebSocket          +---------------------+           Kafka            +---------------------+
|      Agent           |  <-----------------------> |    Agent-Gateway      | <-----------------------> |     Workflows        |
|----------------------|                             |----------------------|                           |----------------------|
| - Connects via WS    |                             | - Accepts WebSocket   |                           | - Workflow Engine    |
| - Sends 'register'   |                             | - Registers session   |                           | - Listens to Results |
| - Receives 'ping'    |                             | - Sends 'ping'        |                           | - Orchestrates       |
| - Handles requests   |                             | - Forwards actions    |                           | - Sends new actions  |
+----------------------+                             +----------------------+                           +----------------------+
```

---

### 📌 WebSocket Connection

- **URL**:
  ```
  ws://{gateway-host}:{gateway-port}/agent/ws
  ```

- **Example**:
  ```
  ws://localhost:8090/agent/ws
  ```

- **Protocol**: WebSocket (JSON messages)

---

### 📌 Connection Flow

| Step | Sender | Receiver | Message Type | Description |
|:----|:--------|:---------|:-------------|:------------|
| 1 | Agent | Gateway | `register` | Registers the agent |
| 2 | Gateway | Agent | `ping` (optional) | Health-check ping |
| 3 | Workflows | Kafka Topic | `ActionExecutionRequest` | Sends an action request |
| 4 | Gateway | Agent | `ActionExecutionRequest` | Forwards request via WebSocket |
| 5 | Agent | Gateway | `ActionExecutionResult` | Returns action result |
| 6 | Gateway | Kafka Topic | `ActionExecutionResult` | Forwards result to workflows |

---

### 📌 Message Types

#### 1. Register Agent

**Agent → Gateway**

```json
{
  "type": "register",
  "agentId": "agent-xyz",
  "teamId": "team-abc",
  "actions": [
    "send-email",
    "jira-listener"
  ]
}
```

---

#### 2. Ping Message

**Gateway → Agent**

```json
{
  "type": "ping",
  "timestamp": "2025-04-28T12:00:00Z"
}
```

---

#### 3. ActionExecutionRequest

**Workflows → Gateway → Agent**

```json
{
  "type": "ActionExecutionRequest",
  "correlationId": "workflow-abc-step-1",
  "action": "send-email",
  "teamId": "team-abc",
  "input": {
    "recipient": "someone@example.com",
    "subject": "Important",
    "body": "This is a test email."
  }
}
```

---

#### 4. ActionExecutionResult

**Agent → Gateway → Workflows**

```json
{
  "type": "ActionExecutionResult",
  "correlationId": "workflow-abc-step-1",
  "payload": {
    "status": "OK",
    "messageId": "12345",
    "result": "Email sent successfully."
  },
  "logs": [
    "Email created",
    "Email sent via SMTP"
  ]
}
```

---

### 📌 Important Rules

- 🔥 Agent **MUST** respond using the same `correlationId` from the request.
- 🔥 Agent must **register** first before receiving any action requests.
- 🔥 Connection is **persistent** — agent should reconnect automatically if dropped.
- 🔥 If agent cannot process a request, it must return an appropriate error payload.

---

### 📌 Example Connection Sequence

1. **Agent connects** to `ws://localhost:8090/agent/ws`.
2. **Agent sends** `register` message.
3. **Gateway responds** with periodic `ping` (optional).
4. **Workflows sends** `ActionExecutionRequest` to Kafka.
5. **Gateway reads** request from Kafka and sends it via WebSocket.
6. **Agent processes** the request.
7. **Agent sends back** `ActionExecutionResult`.
8. **Gateway publishes** result to Kafka.
9. **Workflows consumes** result and proceeds with workflow execution.

## Installation

There is no special installation instruction. We can build whole project with:
```shell
./gradlew clean build
```
it will build the project and run the tests.

## Usage
We have two different ways to use the application. Local and dev (test and production are not covered here) usage is allowed by using some scripts.

### Local
```shell
cd docker
./up-local.sh
```
The above command will prepare the local configuration. It will spin up the databases, kafka and agent-gateway.

```shell
cd ui/frontend
npm run dev
```
The above command will start the normal frontend application

```shell
./gradlew :application:bootRun
```
It will run a normal spring boot application on a local machine.

### Dev
To run in dev we should just run:
```shell
cd docker
docker compose up
```

## Tests
Right now we have unit tests in our application. To run tests we can run a command:
```shell
./gradlew clean build
```

## TODO
Right now the application is still in progress. Many elements are only for learning purposes, so they are basic and are not production ready. This shouldn't be considered as a security issue. This application is in strong development.

## License

[MIT](https://choosealicense.com/licenses/mit/)