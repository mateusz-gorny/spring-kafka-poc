import React, {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import axios from "../api/api";
import {useAuth} from "../auth/AuthContext";
import DeleteWorkflowModal from "../components/DeleteWorkflowModal";

type WorkflowStatus = "ACTIVE" | "INACTIVE" | "IN_PROGRESS" | "ARCHIVED";

type Workflow = {
    id: string;
    name: string;
    status: WorkflowStatus;
    actions: unknown[];
    triggerIds: string[];
    createdAt: string;
    updatedAt: string;
};

export default function Workflows() {
    const {authorities} = useAuth();
    const navigate = useNavigate();

    const canView = authorities?.includes("WORKFLOW_VIEW");
    const canCreate = authorities?.includes("WORKFLOW_ADMIN");

    const [workflows, setWorkflows] = useState<Workflow[]>([]);
    const [filtered, setFiltered] = useState<Workflow[]>([]);
    const [includeArchived, setIncludeArchived] = useState(false);
    const [search, setSearch] = useState("");
    const [sortBy, setSortBy] = useState<"name" | "createdAt">("createdAt");

    const [deletingWorkflow, setDeletingWorkflow] = useState<Workflow | null>(null);

    useEffect(() => {
        if (canView) {
            axios
                .get("/workflows", {
                    params: {includeArchived},
                })
                .then((res) => setWorkflows(res.data));
        }
    }, [includeArchived, canView]);

    useEffect(() => {
        const filteredList = workflows
            .filter((w) =>
                w.name.toLowerCase().includes(search.toLowerCase())
            )
            .sort((a, b) => {
                if (sortBy === "name") {
                    return a.name.localeCompare(b.name);
                } else {
                    return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
                }
            });

        setFiltered(filteredList);
    }, [workflows, search, sortBy]);

    const handleDelete = async () => {
        if (!deletingWorkflow) return;
        await axios.delete(`/workflows/${deletingWorkflow.id}`);
        setWorkflows((prev) => prev.filter(w => w.id !== deletingWorkflow.id));
        setDeletingWorkflow(null);
    };

    if (!canView) {
        return <div className="p-6 text-red-600">Access denied: insufficient permissions.</div>;
    }

    return (
        <div className="p-6">
            <div className="flex justify-between mb-4 items-center">
                <h1 className="text-2xl font-bold">Workflows</h1>
                {canCreate && (
                    <button
                        onClick={() => navigate("/workflows/create")}
                        className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                    >
                        + New Workflow
                    </button>
                )}
            </div>

            <div className="flex gap-4 mb-4 items-center">
                <input
                    type="text"
                    placeholder="Search..."
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                    className="border px-3 py-2 rounded w-60"
                />

                <select
                    value={sortBy}
                    onChange={(e) => setSortBy(e.target.value as any)}
                    className="border px-3 py-2 rounded"
                >
                    <option value="createdAt">Sort by Created At</option>
                    <option value="name">Sort by Name</option>
                </select>

                <label className="flex items-center gap-2">
                    <input
                        type="checkbox"
                        checked={includeArchived}
                        onChange={(e) => setIncludeArchived(e.target.checked)}
                    />
                    Include Archived
                </label>
            </div>

            <div className="grid gap-4">
                {filtered.map((w) => (
                    <div
                        key={w.id}
                        className="border rounded p-4 flex justify-between items-center hover:shadow"
                    >
                        <div
                            className="cursor-pointer"
                            onClick={() => navigate(`/workflows/${w.id}`)}
                        >
                            <h2 className="text-lg font-semibold">{w.name}</h2>
                            <p className="text-gray-500 text-sm">{w.status}</p>
                        </div>
                        {canCreate && (
                            <button
                                onClick={() => setDeletingWorkflow(w)}
                                className="text-red-600 hover:underline text-sm"
                            >
                                🗑 Delete
                            </button>
                        )}
                    </div>
                ))}
            </div>

            {deletingWorkflow && (
                <DeleteWorkflowModal
                    workflowName={deletingWorkflow.name}
                    onCancel={() => setDeletingWorkflow(null)}
                    onConfirm={handleDelete}
                />
            )}
        </div>
    );
}
