# Agent Gateway WebSocket API Documentation

## Overview

The Agent Gateway provides a WebSocket interface for agents to connect, register actions, and receive action execution requests. This document describes the protocol and message formats used for communication between agents and the gateway.

## Connection

### Endpoint

```
ws://<host>:<port>/ws/agent?token=<jwt_token>
```

### Authentication

Authentication is performed using a JWT token passed as a query parameter. The token must contain the following claims:
- `sub`: Agent ID
- `team_id`: Team ID
- `agent`: Boolean flag (must be true)

Example:
```
ws://localhost:8090/ws/agent?token=eyJhbGciOiJIUzI1NiJ9...
```

### Rate Limiting

The WebSocket connections are rate-limited to protect the system from abuse. The default limits are:
- 10 connections per second
- 50 messages per second per agent
- 100 tokens per connection

## Message Format

All messages exchanged between the agent and the gateway follow a common JSON format:

```json
{
  "type": "messageType",
  "correlationId": "optional-correlation-id",
  "payload": {
    "field1": "value1",
    "field2": "value2"
  }
}
```

### Common Fields

- `type`: The type of message, which determines how it will be processed.
- `correlationId`: An optional identifier to correlate requests and responses.
- `payload`: The message-specific payload.

## Message Types

### 1. Register Action

#### Request

Sent by the agent to register an action it can perform.

```json
{
  "type": "register",
  "payload": {
    "action": "actionName",
    "inputSchema": {
      "type": "object",
      "properties": {
        "param1": { "type": "string" },
        "param2": { "type": "number" }
      }
    },
    "outputSchema": {
      "type": "object",
      "properties": {
        "result": { "type": "string" }
      }
    }
  }
}
```

#### Response

Success:
```json
{
  "type": "registered"
}
```

Error:
```json
{
  "type": "error",
  "payload": {
    "message": "Error message"
  }
}
```

### 2. Action Execution Request

#### Request

Sent by the gateway to the agent to request execution of an action.

```json
{
  "type": "ActionExecutionRequest",
  "correlationId": "unique-id",
  "payload": {
    "workflowInstanceId": "workflow-instance-id",
    "action": "actionName",
    "input": {
      "param1": "value1",
      "param2": 42
    }
  }
}
```

#### Response

The agent should respond with an ActionExecutionResult message.

### 3. Action Execution Result

#### Request

Sent by the agent to the gateway with the result of an action execution.

```json
{
  "type": "ActionExecutionResult",
  "correlationId": "unique-id",
  "payload": {
    "status": "SUCCESS|FAILURE",
    "output": {
      "result": "Operation completed successfully",
      "data": {
        "id": "123",
        "value": "example"
      }
    },
    "logs": [
      "Log line 1",
      "Log line 2"
    ]
  }
}
```

#### Response

No explicit response is sent by the gateway for this message type.

### 4. Error

#### Response

Sent by the gateway when an error occurs.

```json
{
  "type": "error",
  "payload": {
    "message": "Error message"
  }
}
```

## Error Handling

### Common Error Codes

- 4000: Missing token
- 4001: Invalid JWT
- 4002: Missing or invalid claims
- 4029: Rate limit exceeded

### Error Responses

When an error occurs, the gateway will send an error message with a descriptive message. For connection-level errors, the WebSocket connection will be closed with an appropriate status code.

## Best Practices

1. **Implement Reconnection Logic**: Agents should implement reconnection logic with exponential backoff to handle temporary disconnections.

3. **Correlation IDs**: Always include correlation IDs in your messages to facilitate debugging and tracing.

4. **Validate Messages**: Validate all messages against the expected schema before sending them to avoid errors.

5. **Error Handling**: Implement robust error handling to gracefully handle unexpected errors.
