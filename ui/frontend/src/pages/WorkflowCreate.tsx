import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "../api/api";
import { useAuth } from "../auth/AuthContext";

type RegisteredAction = {
    name: string;
    displayName: string;
    teamId: string;
    agentId: string;
    inputSchema: Record<string, "string" | "array" | "number" | "boolean">;
};

type NewAction = {
    name: string;
    type: string;
    agentId: string;
    credentialId?: string;
    input: Record<string, any>;
};

export default function WorkflowCreate() {
    const { authorities } = useAuth();
    const canCreate = authorities?.includes("WORKFLOW_ADMIN");
    const navigate = useNavigate();

    const [name, setName] = useState("");
    const [actions, setActions] = useState<NewAction[]>([]);
    const [availableActions, setAvailableActions] = useState<RegisteredAction[]>([]);
    const [selectedActionType, setSelectedActionType] = useState("");
    const [inputSchema, setInputSchema] = useState<Record<string, string>>({});
    const [inputValues, setInputValues] = useState<Record<string, any>>({});
    const [actionName, setActionName] = useState("");
    const [actionCredential, setActionCredential] = useState("");
    const [actionAgentId, setActionAgentId] = useState("");

    const [triggers, setTriggers] = useState([]);
    const [credentials, setCredentials] = useState([]);
    const [selectedTriggers, setSelectedTriggers] = useState<string[]>([]);
    const [selectedCredentials, setSelectedCredentials] = useState<string[]>([]);

    useEffect(() => {
        if (!canCreate) return;
        axios.get("/actions").then(res => {
            setAvailableActions(res.data);
        });
        axios.get("/triggers").then(res => setTriggers(res.data));
        axios.get("/credentials").then(res => setCredentials(res.data));
    }, [canCreate]);

    useEffect(() => {
        if (!selectedActionType) return;
        axios.get(`/actions/${selectedActionType}`).then(res => {
            setInputSchema(res.data.inputSchema || {});
        });
    }, [selectedActionType]);

    const handleInputChange = (key: string, value: any) => {
        setInputValues(prev => ({ ...prev, [key]: value }));
    };

    const addAction = () => {
        if (!selectedActionType || !actionName || !actionAgentId) return;
        setActions(prev => [
            ...prev,
            {
                name: actionName,
                type: selectedActionType,
                agentId: actionAgentId,
                input: inputValues,
                credentialId: actionCredential,
            },
        ]);
        setActionName("");
        setSelectedActionType("");
        setActionAgentId("");
        setInputSchema({});
        setInputValues({});
        setActionCredential("");
    };

    const submitWorkflow = () => {
        axios
            .post("/workflows", {
                name,
                status: "ACTIVE",
                triggerIds: selectedTriggers,
                credentialIds: selectedCredentials,
                actions,
            })
            .then(() => {
                navigate("/workflows");
            });
    };

    if (!canCreate) {
        return <div className="p-6 text-red-600">Access denied: insufficient permissions.</div>;
    }

    return (
        <div className="p-6 max-w-3xl mx-auto">
            <h1 className="text-2xl font-bold mb-4">Create Workflow</h1>

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
                        const values = Array.from(e.target.selectedOptions).map(o => o.value);
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
                        const values = Array.from(e.target.selectedOptions).map(o => o.value);
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

            <div className="border rounded p-4 mb-6">
                <h2 className="text-lg font-semibold mb-2">Add Action</h2>

                <label className="block text-sm">Type</label>
                <select
                    className="border px-3 py-2 rounded mb-3 w-full"
                    value={selectedActionType}
                    onChange={(e) => setSelectedActionType(e.target.value)}
                >
                    <option value="">-- choose action --</option>
                    {[...new Set(availableActions.map(a => a.name))].map((type) => (
                        <option key={type} value={type}>
                            {type}
                        </option>
                    ))}
                </select>

                {selectedActionType && (
                    <>
                        <label className="block text-sm">Agent</label>
                        <select
                            className="border px-3 py-2 rounded mb-3 w-full"
                            value={actionAgentId}
                            onChange={(e) => setActionAgentId(e.target.value)}
                        >
                            <option value="">-- choose agent --</option>
                            {availableActions
                                .filter(a => a.name === selectedActionType)
                                .map(a => (
                                    <option key={a.agentId} value={a.agentId}>
                                        {a.displayName} ({a.teamId})
                                    </option>
                                ))}
                        </select>

                        <label className="block text-sm">Action Name</label>
                        <input
                            className="border px-3 py-2 rounded mb-3 w-full"
                            value={actionName}
                            onChange={(e) => setActionName(e.target.value)}
                        />

                        <label className="block text-sm">Credential</label>
                        <select
                            className="border px-3 py-2 rounded mb-4 w-full"
                            value={actionCredential}
                            onChange={(e) => setActionCredential(e.target.value)}
                        >
                            <option value="">-- none --</option>
                            {credentials.map((c: any) => (
                                <option key={c.id} value={c.id}>
                                    {c.name} ({c.type})
                                </option>
                            ))}
                        </select>

                        <h3 className="font-semibold mb-2">Input Parameters</h3>
                        {Object.entries(inputSchema).map(([key, type]) => (
                            <div key={key} className="mb-3">
                                <label className="block text-sm font-medium">{key}</label>
                                {type === "array" ? (
                                    <input
                                        className="border px-3 py-2 rounded w-full"
                                        type="text"
                                        placeholder="comma,separated,values"
                                        onChange={(e) => handleInputChange(key, e.target.value.split(","))}
                                    />
                                ) : (
                                    <input
                                        className="border px-3 py-2 rounded w-full"
                                        type="text"
                                        onChange={(e) => handleInputChange(key, e.target.value)}
                                    />
                                )}
                            </div>
                        ))}

                        <button
                            onClick={addAction}
                            className="mt-2 bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
                        >
                            Add Action
                        </button>
                    </>
                )}
            </div>

            {actions.length > 0 && (
                <div className="mb-4">
                    <h2 className="text-lg font-semibold">Actions in Workflow</h2>
                    <ul className="border rounded divide-y mt-2">
                        {actions.map((a, i) => (
                            <li key={i} className="p-3">
                                <div className="font-medium">{a.name} ({a.type})</div>
                                <pre className="text-sm text-gray-600">
                  {JSON.stringify(a.input, null, 2)}
                </pre>
                            </li>
                        ))}
                    </ul>
                </div>
            )}

            <button
                onClick={submitWorkflow}
                className="bg-green-600 text-white px-6 py-2 rounded hover:bg-green-700"
            >
                Create Workflow
            </button>
        </div>
    );
}
