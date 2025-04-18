import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import axios from "../api/api";
import { useAuth } from "../auth/AuthContext";

type Instance = {
    id: string;
    status: string;
    triggeredBy: string;
    startedAt: string;
    finishedAt?: string;
};

const statusOptions = ["ALL", "IN_PROGRESS", "SUCCESS", "FAILED"];

export default function WorkflowInstances() {
    const { authorities } = useAuth();
    const canView = authorities?.includes("WORKFLOW_VIEW");

    const { id } = useParams(); // workflowId
    const [instances, setInstances] = useState<Instance[]>([]);
    const [filtered, setFiltered] = useState<Instance[]>([]);
    const [statusFilter, setStatusFilter] = useState("ALL");
    const [sortAsc, setSortAsc] = useState(false);
    const navigate = useNavigate();

    const fetchInstances = () => {
        if (!canView) return;
        axios.get(`/workflows/${id}/instances`).then((res) => {
            setInstances(res.data);
        });
    };

    useEffect(() => {
        fetchInstances();
        const interval = setInterval(fetchInstances, 10000); // auto-refresh
        return () => clearInterval(interval);
    }, [id, canView]);

    useEffect(() => {
        let data = [...instances];
        if (statusFilter !== "ALL") {
            data = data.filter((i) => i.status === statusFilter);
        }
        data.sort((a, b) =>
            sortAsc
                ? new Date(a.startedAt).getTime() - new Date(b.startedAt).getTime()
                : new Date(b.startedAt).getTime() - new Date(a.startedAt).getTime()
        );
        setFiltered(data);
    }, [instances, statusFilter, sortAsc]);

    if (!canView) {
        return <div className="p-6 text-red-600">Access denied</div>;
    }

    const statusColor = (status: string) => {
        switch (status) {
            case "SUCCESS":
                return "bg-green-200 text-green-800";
            case "FAILED":
                return "bg-red-200 text-red-800";
            case "IN_PROGRESS":
                return "bg-yellow-200 text-yellow-800";
            default:
                return "bg-gray-200 text-gray-800";
        }
    };

    return (
        <div className="p-6">
            <h1 className="text-2xl font-bold mb-4">Workflow Executions</h1>

            <div className="flex justify-between items-center mb-4">
                <div>
                    <label className="mr-2 font-medium">Status:</label>
                    <select
                        className="border px-2 py-1 rounded"
                        value={statusFilter}
                        onChange={(e) => setStatusFilter(e.target.value)}
                    >
                        {statusOptions.map((s) => (
                            <option key={s} value={s}>
                                {s}
                            </option>
                        ))}
                    </select>
                </div>
                <button
                    className="text-sm text-blue-600 underline"
                    onClick={() => setSortAsc((prev) => !prev)}
                >
                    Sort: {sortAsc ? "Oldest first" : "Newest first"}
                </button>
            </div>

            <div className="bg-white rounded shadow overflow-hidden">
                <table className="w-full text-left">
                    <thead className="bg-gray-100">
                    <tr>
                        <th className="px-4 py-2">ID</th>
                        <th className="px-4 py-2">Status</th>
                        <th className="px-4 py-2">Triggered By</th>
                        <th className="px-4 py-2">Started</th>
                        <th className="px-4 py-2">Finished</th>
                    </tr>
                    </thead>
                    <tbody>
                    {filtered.map((i) => (
                        <tr
                            key={i.id}
                            onClick={() => navigate(`/instances/${i.id}`)}
                            className="hover:bg-gray-50 cursor-pointer"
                        >
                            <td className="px-4 py-2">{i.id.slice(0, 8)}…</td>
                            <td className="px-4 py-2">
                  <span
                      className={`px-2 py-1 rounded text-sm font-semibold ${statusColor(
                          i.status
                      )}`}
                  >
                    {i.status}
                  </span>
                            </td>
                            <td className="px-4 py-2">{i.triggeredBy}</td>
                            <td className="px-4 py-2">
                                {new Date(i.startedAt).toLocaleString()}
                            </td>
                            <td className="px-4 py-2">
                                {i.finishedAt
                                    ? new Date(i.finishedAt).toLocaleString()
                                    : "—"}
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
                {filtered.length === 0 && (
                    <div className="p-4 text-center text-gray-500">No instances found.</div>
                )}
            </div>
        </div>
    );
}
