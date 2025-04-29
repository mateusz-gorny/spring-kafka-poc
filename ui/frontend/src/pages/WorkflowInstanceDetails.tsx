import { useEffect, useState } from "react";
import { getWorkflowInstance, getWorkflowInstanceStatus } from "../api/api";

interface StepRecordResponse {
    actionName: string;
    input: Record<string, any>;
    output: Record<string, any>;
    timestamp: string;
    status: string;
}

interface WorkflowInstanceResponse {
    workflowInstanceId: string;
    definitionId: string;
    status: string;
    context: Record<string, any>;
    history: StepRecordResponse[];
}

export default function WorkflowInstanceDetails({ instanceId }: { instanceId: string }) {
    const [instance, setInstance] = useState<WorkflowInstanceResponse | null>(null);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        getWorkflowInstance(instanceId)
            .then(setInstance)
            .catch(() => setError("Failed to load instance details"));
    }, [instanceId]);

    useEffect(() => {
        if (!instance) return;

        let polling: number;

        if (instance.status === "RUNNING") {
            polling = setInterval(async () => {
                try {
                    const statusResponse = await getWorkflowInstanceStatus(instance.workflowInstanceId);
                    if (statusResponse.status !== "RUNNING") {
                        clearInterval(polling);
                        const updatedInstance = await getWorkflowInstance(instance.workflowInstanceId);
                        setInstance(updatedInstance);
                    }
                } catch (err) {
                    console.error("Polling failed", err);
                }
            }, 5000);
        }

        return () => {
            if (polling) {
                clearInterval(polling);
            }
        };
    }, [instance]);

    if (error) {
        return <div className="p-4 text-red-600">{error}</div>;
    }

    if (!instance) {
        return <div className="p-4">Loading...</div>;
    }

    return (
        <div className="p-4">
            <h1 className="text-2xl font-bold mb-4">Instance Details</h1>

            <h2 className="text-xl font-semibold mb-2">Instance ID: {instance.workflowInstanceId}</h2>
            <h2 className="text-xl font-semibold mb-2">Definition ID: {instance.definitionId}</h2>
            <h2 className="text-xl font-semibold mb-2">Status: {instance.status}</h2>

            <h2 className="text-xl font-semibold mt-6 mb-2">Steps:</h2>
            <div className="space-y-4">
                {instance.history.map((step, idx) => (
                    <div key={idx} className="p-4 border rounded">
                        <h3 className="font-semibold">Action: {step.actionName}</h3>
                        <p>Status: {step.status}</p>
                        <p>Timestamp: {new Date(step.timestamp).toLocaleString()}</p>

                        <div className="mt-2">
                            <strong>Input:</strong>
                            <pre className="bg-gray-100 p-2 rounded">{JSON.stringify(step.input, null, 2)}</pre>
                        </div>

                        <div className="mt-2">
                            <strong>Output:</strong>
                            <pre className="bg-gray-100 p-2 rounded">{JSON.stringify(step.output, null, 2)}</pre>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}
