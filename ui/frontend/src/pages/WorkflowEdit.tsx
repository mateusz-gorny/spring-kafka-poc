import React, {useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import axios from "../api/api";
import {useAuth} from "../auth/AuthContext";

export default function WorkflowEdit() {
    const {authorities} = useAuth();
    const {id} = useParams();
    const navigate = useNavigate();

    const canEdit = authorities?.includes("WORKFLOW_ADMIN");

    const [name, setName] = useState("");
    const [triggers, setTriggers] = useState([]);
    const [credentials, setCredentials] = useState([]);
    const [selectedTriggers, setSelectedTriggers] = useState<string[]>([]);
    const [selectedCredentials, setSelectedCredentials] = useState<string[]>([]);
    const [actions, setActions] = useState<any[]>([]);

    useEffect(() => {
        if (!canEdit || !id) return;

        axios.get(`/workflows/${id}`).then((res) => {
            const wf = res.data;
            setName(wf.name);
            setSelectedTriggers(wf.triggerIds || []);
            setSelectedCredentials(wf.credentialIds || []);
            setActions(wf.actions || []);
        });

        axios.get("/triggers").then((res) => setTriggers(res.data));
        axios.get("/credentials").then((res) => setCredentials(res.data));
    }, [canEdit, id]);

    const submit = () => {
        axios
            .put(`/workflows/${id}`, {
                name,
                status: "ACTIVE",
                triggerIds: selectedTriggers,
                credentialIds: selectedCredentials,
                actions,
            })
            .then(() => navigate("/workflows"));
    };

    if (!canEdit) {
        return <div className="p-6 text-red-600">Access denied</div>;
    }

    return (
        <div className="p-6 max-w-3xl mx-auto">
            <h1 className="text-2xl font-bold mb-4">Edit Workflow</h1>

            <label className="block mb-2 font-medium">Workflow name</label>
            <input
                className="border px-3 py-2 rounded w-full mb-4"
                value={name}
                onChange={(e) => setName(e.target.value)}
            />

            <div className="mb-4">
                <label className="block font-semibold mb-1">Triggers</label>
                <select
                    multiple
                    className="border px-3 py-2 rounded w-full h-32"
                    value={selectedTriggers}
                    onChange={(e) => {
                        const values = Array.from(e.target.selectedOptions).map((o) => o.value);
                        setSelectedTriggers(values);
                    }}
                >
                    {triggers.map((t: any) => (
                        <option key={t.id} value={t.id}>
                            {t.name} ({t.type})
                        </option>
                    ))}
                </select>
            </div>

            <div className="mb-6">
                <label className="block font-semibold mb-1">Credentials</label>
                <select
                    multiple
                    className="border px-3 py-2 rounded w-full h-32"
                    value={selectedCredentials}
                    onChange={(e) => {
                        const values = Array.from(e.target.selectedOptions).map((o) => o.value);
                        setSelectedCredentials(values);
                    }}
                >
                    {credentials.map((c: any) => (
                        <option key={c.id} value={c.id}>
                            {c.name} ({c.type})
                        </option>
                    ))}
                </select>
            </div>

            <div className="mb-6">
                <h2 className="text-lg font-semibold">Actions</h2>
                {actions.map((a, i) => (
                    <div key={i} className="border rounded mb-3 p-3 bg-gray-50">
                        <div className="font-medium">{a.name} ({a.type})</div>
                        <pre className="text-sm text-gray-700 mt-1">{JSON.stringify(a.input, null, 2)}</pre>
                    </div>
                ))}
            </div>

            <button
                onClick={submit}
                className="bg-green-600 text-white px-6 py-2 rounded hover:bg-green-700"
            >
                Save Changes
            </button>
        </div>
    );
}
