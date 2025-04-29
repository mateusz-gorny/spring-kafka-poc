import { useEffect, useState } from "react";
import { getWorkflowInstancesByDefinition } from "../api/api";
import { useParams, useNavigate } from "react-router-dom";

interface WorkflowInstanceResponse {
    workflowInstanceId: string;
    definitionId: string;
    status: string;
    context: Record<string, any>;
    history: {
        actionName: string;
        input: Record<string, any>;
        output: Record<string, any>;
        timestamp: string;
        status: string;
    }[];
}

export default function WorkflowInstances() {
    const { id } = useParams<{ id: string }>();
    const [instances, setInstances] = useState<WorkflowInstanceResponse[]>([]);
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        if (id) {
            getWorkflowInstancesByDefinition(id)
                .then(setInstances)
                .catch(() => setError("Failed to load instances"));
        }
    }, [id]);

    if (error) {
        return <div className="p-4 text-red-600">{error}</div>;
    }

    return (
        <div className="p-4">
            <h1 className="text-2xl font-bold mb-4">Workflow Instances</h1>

            {instances.length === 0 ? (
                <div>No instances found for this workflow.</div>
            ) : (
                <table className="min-w-full table-auto">
                    <thead>
                    <tr>
                        <th className="px-4 py-2">Instance ID</th>
                        <th className="px-4 py-2">Status</th>
                        <th className="px-4 py-2">First Step Timestamp</th>
                        <th className="px-4 py-2">Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    {instances.map((instance) => (
                        <tr key={instance.workflowInstanceId}>
                            <td className="border px-4 py-2">{instance.workflowInstanceId}</td>
                            <td className="border px-4 py-2">{instance.status}</td>
                            <td className="border px-4 py-2">
                                {instance.history.length > 0
                                    ? new Date(instance.history[0].timestamp).toLocaleString()
                                    : "-"}
                            </td>
                            <td className="border px-4 py-2">
                                <button
                                    onClick={() => navigate(`/instances/${instance.workflowInstanceId}`)}
                                    className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-1 px-3 rounded"
                                >
                                    View
                                </button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}
