import React, {useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import axios from "../api/api";
import {useAuth} from "../auth/AuthContext";
import DeleteWorkflowModal from "../components/DeleteWorkflowModal";

type Workflow = {
    id: string;
    name: string;
    status: string;
    triggerIds: string[];
    credentialIds: string[];
    actions: {
        id: string;
        name: string;
        type: string;
        credentialId?: string;
        input: Record<string, unknown>;
        parallelGroup?: string;
    }[];
    createdAt: string;
    updatedAt: string;
};

export default function WorkflowDetails() {
    const {id} = useParams();
    const navigate = useNavigate();
    const {authorities} = useAuth();

    const canView = authorities?.includes("WORKFLOW_VIEW");
    const canEdit = authorities?.includes("WORKFLOW_ADMIN");

    const [workflow, setWorkflow] = useState<Workflow | null>(null);
    const [loading, setLoading] = useState(true);
    const [showDeleteModal, setShowDeleteModal] = useState(false);

    useEffect(() => {
        if (!canView) return;

        axios
            .get(`/workflows/${id}`)
            .then((res) => setWorkflow(res.data))
            .finally(() => setLoading(false));
    }, [id, canView]);

    const handleDelete = async () => {
        if (!workflow) return;
        await axios.delete(`/workflows/${workflow.id}`);
        navigate("/workflows");
    };

    if (!canView) {
        return <div className="p-6 text-red-600">Access denied</div>;
    }

    if (loading || !workflow) {
        return <div className="p-6">Loading workflow...</div>;
    }

    const groupBy = (list: any[], key: string) =>
        list.reduce((acc, item) => {
            const value = item[key] || "_";
            acc[value] = acc[value] || [];
            acc[value].push(item);
            return acc;
        }, {} as Record<string, typeof list>);

    const grouped: Record<string, Workflow["actions"]> = groupBy(workflow.actions, "parallelGroup");

    return (
        <div className="p-6">
            <div className="mb-4 flex justify-between items-center">
                <h1 className="text-2xl font-bold">{workflow.name}</h1>
                <div className="flex gap-2">
                    <button
                        onClick={() => navigate(`/workflows/${id}/instances`)}
                        className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
                    >
                        View Executions
                    </button>
                    {canEdit && (
                        <button
                            onClick={() => setShowDeleteModal(true)}
                            className="bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700"
                        >
                            Delete
                        </button>
                    )}
                </div>
            </div>

            <div className="mb-6 grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                    <h2 className="text-lg font-semibold">Status</h2>
                    <p className="text-gray-700">{workflow.status}</p>
                </div>

                <div>
                    <h2 className="text-lg font-semibold">Triggers</h2>
                    {workflow.triggerIds.length > 0 ? (
                        <ul className="list-disc ml-4 text-gray-700">
                            {workflow.triggerIds.map((t) => (
                                <li key={t}>{t}</li>
                            ))}
                        </ul>
                    ) : (
                        <p className="text-gray-500">No triggers</p>
                    )}
                </div>

                <div>
                    <h2 className="text-lg font-semibold">Credentials</h2>
                    {workflow.credentialIds.length > 0 ? (
                        <ul className="list-disc ml-4 text-gray-700">
                            {workflow.credentialIds.map((c) => (
                                <li key={c}>{c}</li>
                            ))}
                        </ul>
                    ) : (
                        <p className="text-gray-500">No credentials</p>
                    )}
                </div>
            </div>

            <div>
                <h2 className="text-lg font-semibold mb-2">Actions</h2>
                {Object.entries(grouped).map(([group, actions]) => (
                    <div key={group} className="mb-4">
                        <h3 className="text-gray-600 mb-1">
                            Group: {group === "_" ? "None" : group}
                        </h3>
                        <ul className="list-disc ml-6 text-gray-700">
                            {actions.map((action) => (
                                <li key={action.id}>
                                    <strong>{action.name}</strong> ({action.type})
                                </li>
                            ))}
                        </ul>
                    </div>
                ))}
            </div>

            {showDeleteModal && (
                <DeleteWorkflowModal
                    workflowName={workflow.name}
                    onCancel={() => setShowDeleteModal(false)}
                    onConfirm={handleDelete}
                />
            )}
        </div>
    );
}
