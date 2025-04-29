import { useEffect, useState } from "react";
import { getWorkflowDefinition, getWorkflowInstancesByDefinition } from "../api/api";
import { useParams, useNavigate } from "react-router-dom";
import WorkflowNodeReadonly from "../components/WorkflowNodeReadonly";

interface WorkflowDefinitionResponse {
    id: string;
    transitions: Record<string, any>;
    status: string;
}

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

export default function WorkflowDetails() {
    const { id } = useParams<{ id: string }>();
    const [workflow, setWorkflow] = useState<WorkflowDefinitionResponse | null>(null);
    const [instances, setInstances] = useState<WorkflowInstanceResponse[]>([]);
    const [error, setError] = useState<string | null>(null);
    const [showActions, setShowActions] = useState(false);
    const [showInstance, setShowInstance] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        if (id) {
            getWorkflowDefinition(id)
                .then(setWorkflow)
                .catch(() => setError("Failed to load workflow"));

            getWorkflowInstancesByDefinition(id)
                .then(setInstances)
                .catch(() => {
                    setInstances([]);
                });
        }
    }, [id]);

    if (error) {
        return <div className="p-4 text-red-600">{error}</div>;
    }

    if (!workflow) {
        return <div className="p-4">Loading...</div>;
    }

    const latestInstance = instances
        .sort((a, b) => {
            const dateA = new Date(a.history[0]?.timestamp || 0).getTime();
            const dateB = new Date(b.history[0]?.timestamp || 0).getTime();
            return dateB - dateA;
        })[0];

    const actionsFromTransitions = Object.keys(workflow.transitions).filter((a) => a !== "start");

    return (
        <div className="p-4 space-y-4">
            <h1 className="text-2xl font-bold mb-4">Workflow Details</h1>

            <div className="space-y-2">
                <div><strong>ID:</strong> {workflow.id}</div>
                <div><strong>Status:</strong> {workflow.status}</div>
            </div>

            <div className="flex gap-2 mt-4">
                <button
                    onClick={() => navigate(`/workflows/${workflow.id}/edit`)}
                    className="bg-yellow-400 hover:bg-yellow-600 text-white font-bold py-2 px-4 rounded"
                >
                    Edit
                </button>
                <button
                    onClick={() => navigate(`/workflows/${workflow.id}/instances`)}
                    className="bg-purple-500 hover:bg-purple-700 text-white font-bold py-2 px-4 rounded"
                >
                    Instances
                </button>
            </div>

            <div className="mt-6">
                <button
                    onClick={() => setShowActions((prev) => !prev)}
                    className="bg-gray-300 hover:bg-gray-500 text-black font-bold py-2 px-4 rounded w-full text-left"
                >
                    {showActions ? "Hide Actions" : "Show Actions"}
                </button>
                {showActions && (
                    <div className="mt-4 space-y-4">
                        {actionsFromTransitions.map((actionName, idx) => (
                            <WorkflowNodeReadonly key={idx} actionName={actionName} />
                        ))}
                    </div>
                )}
            </div>

            <div className="mt-6">
                <button
                    onClick={() => setShowInstance((prev) => !prev)}
                    className="bg-gray-300 hover:bg-gray-500 text-black font-bold py-2 px-4 rounded w-full text-left"
                >
                    {showInstance ? "Hide Latest Instance" : "Show Latest Instance"}
                </button>
                {showInstance && (
                    <div className="mt-4">
                        {instances.length === 0 ? (
                            <div>No instances found for this workflow.</div>
                        ) : latestInstance ? (
                            <div className="space-y-2">
                                <div><strong>Instance ID:</strong> {latestInstance.workflowInstanceId}</div>
                                <div><strong>Status:</strong> {latestInstance.status}</div>
                                <div className="mt-4">
                                    <h2 className="text-lg font-semibold mb-2">History:</h2>
                                    {latestInstance.history.map((step, idx) => (
                                        <div key={idx} className="border rounded p-2 bg-gray-50 space-y-1">
                                            <div><strong>Action:</strong> {step.actionName}</div>
                                            <div><strong>Status:</strong> {step.status}</div>
                                            <div><strong>Timestamp:</strong> {new Date(step.timestamp).toLocaleString()}</div>
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
                        ) : (
                            <div>No latest instance available.</div>
                        )}
                    </div>
                )}
            </div>

            <div className="mt-6">
                <h2 className="text-xl font-semibold mb-2">Transitions:</h2>
                <div className="space-y-2">
                    {Object.entries(workflow.transitions).map(([from, tos]) => (
                        <div key={from} className="border rounded p-2 bg-gray-100">
                            <div className="font-bold">{from}</div>
                            {tos.length > 0 ? (
                                <ul className="list-disc ml-6">
                                    {tos.map((to: any, idx: number) => (
                                        <li key={idx}>
                                            ➔ {to.actionName}
                                        </li>
                                    ))}
                                </ul>
                            ) : (
                                <div className="text-gray-600">No transitions</div>
                            )}
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}
