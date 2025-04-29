import { useEffect, useState } from "react";
import { getWorkflowDefinitions, startWorkflow, deleteWorkflow } from "../api/api";
import { useNavigate } from "react-router-dom";

interface WorkflowDefinitionResponse {
    id: string;
    transitions: Record<string, any>;
    status: string;
}

export default function Workflows() {
    const [workflows, setWorkflows] = useState<WorkflowDefinitionResponse[]>([]);
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        loadWorkflows();
    }, []);

    const loadWorkflows = async () => {
        try {
            const data = await getWorkflowDefinitions();
            setWorkflows(data);
        } catch {
            setError("Failed to load workflows");
        }
    };

    const handleStart = async (id: string) => {
        try {
            await startWorkflow(id);
            await loadWorkflows();
        } catch {
            setError("Failed to start workflow");
        }
    };

    const handleDelete = async (id: string) => {
        if (!window.confirm("Are you sure you want to delete this workflow?")) return;
        try {
            await deleteWorkflow(id);
            await loadWorkflows();
        } catch {
            setError("Failed to delete workflow");
        }
    };

    if (error) {
        return <div className="p-4 text-red-600">{error}</div>;
    }

    return (
        <div className="p-4">
            <div className="flex justify-between items-center mb-4">
                <h1 className="text-2xl font-bold">Workflows</h1>
                <button
                    onClick={() => navigate("/workflows/create")}
                    className="bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded"
                >
                    Create Workflow
                </button>
            </div>

            <table className="min-w-full table-auto">
                <thead>
                <tr>
                    <th className="px-4 py-2">Workflow ID</th>
                    <th className="px-4 py-2">Status</th>
                    <th className="px-4 py-2">Actions</th>
                </tr>
                </thead>
                <tbody>
                {workflows.map((workflow) => (
                    <tr key={workflow.id}>
                        <td className="border px-4 py-2">
                            <button
                                onClick={() => navigate(`/workflows/${workflow.id}`)}
                                className="text-blue-600 underline"
                            >
                                {workflow.id}
                            </button>
                        </td>
                        <td className="border px-4 py-2">{workflow.status}</td>
                        <td className="border px-4 py-2 space-x-2">
                            <button
                                onClick={() => navigate(`/workflows/${workflow.id}/edit`)}
                                className="bg-yellow-400 hover:bg-yellow-600 text-white font-bold py-1 px-3 rounded"
                            >
                                Edit
                            </button>
                            <button
                                onClick={() => navigate(`/workflows/${workflow.id}/instances`)}
                                className="bg-purple-500 hover:bg-purple-700 text-white font-bold py-1 px-3 rounded"
                            >
                                Instances
                            </button>
                            <button
                                onClick={() => handleStart(workflow.id)}
                                className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-1 px-3 rounded"
                            >
                                Start
                            </button>
                            <button
                                onClick={() => handleDelete(workflow.id)}
                                className="bg-red-500 hover:bg-red-700 text-white font-bold py-1 px-3 rounded"
                            >
                                Delete
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}
