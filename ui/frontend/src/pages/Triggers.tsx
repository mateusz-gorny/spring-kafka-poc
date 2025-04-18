import React, {useEffect, useState} from "react";
import {Link, useNavigate} from "react-router-dom";
import axios from "../api/api";
import {useAuth} from "../auth/AuthContext";
import {useToast} from "../components/ToastContext";
import JsonInputModal from "../components/JsonInputModal";

interface Trigger {
    id: string;
    name: string;
    key: string;
    type: string;
    workflowIds: string[];
}

const Triggers = () => {
    const [triggers, setTriggers] = useState<Trigger[]>([]);
    const [fireId, setFireId] = useState<string | null>(null);
    const navigate = useNavigate();
    const {authorities} = useAuth();
    const {showToast} = useToast();

    const canView = authorities?.includes("TRIGGER_VIEW");
    const canEdit = authorities?.includes("TRIGGER_ADMIN");

    useEffect(() => {
        if (canView) {
            fetchTriggers();
        }
    }, [canView]);

    const fetchTriggers = async () => {
        const res = await axios.get("/triggers");
        setTriggers(res.data);
    };

    const handleDelete = async (id: string) => {
        if (!window.confirm("Are you sure you want to delete this trigger?")) return;
        try {
            await axios.delete(`/triggers/${id}`);
            setTriggers(prev => prev.filter(t => t.id !== id));
        } catch (err) {
            console.error("Delete failed", err);
            showToast("Failed to delete trigger", "error");
        }
    };

    const handleFire = (id: string) => {
        setFireId(id);
    };

    const sendPayload = async (json: object) => {
        if (!fireId) return;
        try {
            await axios.post(`/triggers/${fireId}/fire`, json);
            showToast("Trigger fired!", "success");
        } catch (err) {
            console.error("Fire failed", err);
            showToast("Failed to fire trigger. Make sure your payload is valid JSON.", "error");
        } finally {
            setFireId(null);
        }
    };

    if (!canView) {
        return (
            <div className="p-8 text-red-500">
                You are not authorized to view triggers.
            </div>
        );
    }

    return (
        <div className="flex h-screen">
            {fireId && (
                <JsonInputModal
                    onCancel={() => setFireId(null)}
                    onConfirm={sendPayload}
                    title="Send Trigger Payload"
                />
            )}
            <div className="flex flex-col flex-1 bg-white">
                <main className="p-6 overflow-y-auto">
                    <div className="flex justify-between items-center mb-4">
                        <h1 className="text-2xl font-semibold">Triggers</h1>
                        {canEdit && (
                            <Link
                                to="/triggers/new"
                                className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded"
                            >
                                Add New Trigger
                            </Link>
                        )}
                    </div>

                    <div className="grid gap-4">
                        {triggers.map(trigger => (
                            <div key={trigger.id} className="p-4 border rounded shadow-sm">
                                <div className="font-bold text-lg">{trigger.name}</div>
                                <div className="text-sm text-gray-500">Type: {trigger.type}</div>
                                <div className="text-sm text-gray-500 mb-1">Key: <code>{trigger.key}</code></div>
                                <div className="text-sm text-gray-500 mb-2">
                                    Workflows: {trigger.workflowIds.join(", ") || "None"}
                                </div>

                                {canEdit && (
                                    <div className="flex gap-4 text-sm">
                                        <button
                                            onClick={() => navigate(`/triggers/${trigger.id}`)}
                                            className="text-blue-600 hover:underline"
                                        >
                                            Edit
                                        </button>
                                        <button
                                            onClick={() => handleFire(trigger.id)}
                                            className="text-green-600 hover:underline"
                                        >
                                            Fire
                                        </button>
                                        <button
                                            onClick={() => handleDelete(trigger.id)}
                                            className="text-red-600 hover:underline"
                                        >
                                            Delete
                                        </button>
                                    </div>
                                )}
                            </div>
                        ))}
                    </div>
                </main>
            </div>
        </div>
    );
};

export default Triggers;
