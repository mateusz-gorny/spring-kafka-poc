import React, { useState } from "react";

type AgentCreateModalProps = {
    open: boolean;
    onClose: () => void;
    onConfirm: (name: string) => void;
};

export default function AgentCreateModal({ open, onClose, onConfirm }: AgentCreateModalProps) {
    const [name, setName] = useState("");
    const [error, setError] = useState("");

    const handleSubmit = () => {
        if (!name.trim()) {
            setError("Agent name is required");
            return;
        }
        onConfirm(name.trim());
        setName("");
        setError("");
        onClose();
    };

    if (!open) return null;

    return (
        <div className="fixed inset-0 bg-black bg-opacity-30 flex items-center justify-center z-50">
            <div className="bg-white rounded shadow-lg p-6 w-full max-w-sm">
                <h2 className="text-lg font-semibold mb-4">Create New Agent</h2>
                <input
                    type="text"
                    placeholder="Enter agent name"
                    value={name}
                    onChange={e => setName(e.target.value)}
                    className="w-full p-2 border rounded text-sm"
                />
                {error && <div className="text-red-600 text-xs mt-1">{error}</div>}
                <div className="flex justify-end gap-2 mt-4">
                    <button onClick={onClose} className="px-4 py-1 text-sm rounded border">Cancel</button>
                    <button
                        onClick={handleSubmit}
                        className="px-4 py-1 text-sm rounded bg-blue-600 text-white hover:bg-blue-700"
                    >
                        Create
                    </button>
                </div>
            </div>
        </div>
    );
}
