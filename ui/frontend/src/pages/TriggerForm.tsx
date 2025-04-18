import React, {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import axios from "../api/api";
import {useAuth} from "../auth/AuthContext";
import {useToast} from "../components/ToastContext";

interface Trigger {
    id?: string;
    name: string;
    key?: string;
    type: "WEBHOOK" | "SCHEDULER";
    workflowIds: string[];
    metadata: Record<string, any>;
}

const TriggerForm = () => {
    const {authorities} = useAuth();
    const {showToast} = useToast();

    if (!authorities?.includes("TRIGGER_ADMIN")) {
        return (
            <div className="p-8 text-red-500">
                You are not authorized to manage triggers.
            </div>
        );
    }

    const {id} = useParams();
    const [form, setForm] = useState<Trigger>({
        name: "",
        type: "WEBHOOK",
        workflowIds: [],
        metadata: {}
    });

    const [metadataText, setMetadataText] = useState("{}");

    useEffect(() => {
        if (id) {
            axios.get(`/triggers/${id}`).then(res => {
                setForm(res.data);
                setMetadataText(JSON.stringify(res.data.metadata || {}, null, 2));
            });
        }
    }, [id]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const {name, value} = e.target;
        setForm(prev => ({...prev, [name]: value}));
    };

    const handleWorkflowIdsChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        const ids = value.split(",").map(s => s.trim()).filter(Boolean);
        setForm(prev => ({...prev, workflowIds: ids}));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        try {
            const parsedMetadata = metadataText ? JSON.parse(metadataText) : {};
            const payload = {...form, metadata: parsedMetadata};

            const response = id
                ? await axios.put(`/triggers/${id}`, payload)
                : await axios.post("/triggers", payload);

            setForm(response.data);
            setMetadataText(JSON.stringify(response.data.metadata || {}, null, 2));
            showToast("Trigger saved!", "success");
        } catch (err) {
            console.error("Failed to save trigger", err);
            showToast("Error saving trigger.", "error");
        }
    };

    return (
        <div className="flex h-screen">
            <div className="flex flex-col flex-1 bg-white">
                <main className="p-6 overflow-y-auto">
                    <h1 className="text-2xl font-semibold mb-4">{id ? "Edit" : "Create"} Trigger</h1>

                    <form onSubmit={handleSubmit} className="max-w-xl space-y-4">
                        <input
                            type="text"
                            name="name"
                            value={form.name}
                            onChange={handleChange}
                            placeholder="Trigger Name"
                            className="w-full border px-3 py-2 rounded"
                            required
                        />

                        <select
                            name="type"
                            value={form.type}
                            onChange={handleChange}
                            className="w-full border px-3 py-2 rounded"
                        >
                            <option value="WEBHOOK">WEBHOOK</option>
                            <option value="SCHEDULER">SCHEDULER</option>
                        </select>

                        <input
                            type="text"
                            value={form.workflowIds.join(", ")}
                            onChange={handleWorkflowIdsChange}
                            placeholder="Workflow IDs (comma separated)"
                            className="w-full border px-3 py-2 rounded"
                        />

                        <textarea
                            value={metadataText}
                            onChange={e => setMetadataText(e.target.value)}
                            rows={5}
                            className="w-full border px-3 py-2 rounded font-mono text-sm"
                            placeholder="Metadata as JSON"
                        />

                        {form.key && (
                            <div className="text-sm text-gray-500 border px-3 py-2 rounded bg-gray-50">
                                <strong>Trigger Key:</strong> <code>{form.key}</code>
                            </div>
                        )}

                        <button
                            type="submit"
                            className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded"
                        >
                            Save Trigger
                        </button>
                    </form>
                </main>
            </div>
        </div>
    );
};

export default TriggerForm;
