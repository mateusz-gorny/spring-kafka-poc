import React, {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import axios from "../api/api";
import {useAuth} from "../auth/AuthContext";

type Action = {
    actionId: string;
    name: string;
    status: string;
    log?: string;
    output?: Record<string, any>;
};

type Instance = {
    id: string;
    workflowId: string;
    status: string;
    triggeredBy: string;
    payload: Record<string, any>;
    startedAt: string;
    finishedAt?: string;
    actions: Action[];
};

export default function WorkflowInstanceDetails() {
    const {id} = useParams(); // instance ID
    const {authorities} = useAuth();
    const canView = authorities?.includes("WORKFLOW_VIEW");

    const [instance, setInstance] = useState<Instance | null>(null);

    useEffect(() => {
        if (!canView) return;
        axios.get(`/workflows/instances/${id}`).then((res) => {
            setInstance(res.data);
        });
    }, [id, canView]);

    if (!canView) {
        return <div className="p-6 text-red-600">Access denied</div>;
    }

    if (!instance) {
        return <div className="p-6">Loading instance...</div>;
    }

    return (
        <div className="p-6">
            <h1 className="text-2xl font-bold mb-4">Workflow Execution</h1>

            <div className="mb-6 grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                    <p><strong>Status:</strong> {instance.status}</p>
                    <p><strong>Triggered by:</strong> {instance.triggeredBy}</p>
                    <p><strong>Started:</strong> {new Date(instance.startedAt).toLocaleString()}</p>
                    <p>
                        <strong>Finished:</strong> {instance.finishedAt ? new Date(instance.finishedAt).toLocaleString() : "—"}
                    </p>
                </div>
                <div>
                    <p><strong>Payload:</strong></p>
                    <pre className="bg-gray-100 text-sm p-3 rounded">{JSON.stringify(instance.payload, null, 2)}</pre>
                </div>
            </div>

            <h2 className="text-xl font-semibold mb-2">Actions</h2>
            <ul className="border rounded divide-y">
                {instance.actions.map((a) => (
                    <li key={a.actionId} className="p-4">
                        <div className="font-semibold">{a.name}</div>
                        <p className="text-sm text-gray-600">Status: {a.status}</p>
                        {a.log && (
                            <>
                                <p className="text-sm mt-2 font-medium">Log:</p>
                                <pre className="text-xs bg-gray-50 rounded p-2 text-gray-800">{a.log}</pre>
                            </>
                        )}
                        {a.output && (
                            <>
                                <p className="text-sm mt-2 font-medium">Output:</p>
                                <pre
                                    className="text-xs bg-gray-50 rounded p-2 text-gray-800">{JSON.stringify(a.output, null, 2)}</pre>
                            </>
                        )}
                    </li>
                ))}
            </ul>
        </div>
    );
}
