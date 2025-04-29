import React, {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import axios from "../api/api";
import AgentCreateModal from "../components/AgentCreateModal";

type Agent = {
    id: string;
    name: string;
    lastPingAt?: string;
    isActive: boolean;
};

export default function Agents() {
    const [agents, setAgents] = useState<Agent[]>([]);
    const [newAgent, setNewAgent] = useState<{ id: string; secret: string } | null>(null);
    const [showSecret, setShowSecret] = useState(false);
    const [showCreateModal, setShowCreateModal] = useState(false);
    const navigate = useNavigate();

    const loadAgents = () => {
        axios
            .get("/agents")
            .then(res => res.data)
            .then(setAgents);
    };

    const createAgent = async (name: string) => {
        const res = await axios.post("/agents", {name});
        const data = await res.data;
        setNewAgent(data);
        setShowSecret(true);
        loadAgents();
    };

    useEffect(() => {
        loadAgents();
    }, []);

    return (
        <div className="p-6 space-y-6">
            <div className="flex justify-between items-center">
                <h1 className="text-xl font-semibold">Agents</h1>
                <button
                    onClick={() => setShowCreateModal(true)}
                    className="px-4 py-2 text-sm font-medium bg-blue-600 text-white rounded hover:bg-blue-700"
                >
                    Add Agent
                </button>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {agents.map(agent => (
                    <div
                        key={agent.id}
                        className="border rounded p-4 cursor-pointer hover:border-blue-500"
                        onClick={() => navigate(`/agents/${agent.id}`)}
                    >
                        <div className="flex justify-between items-center mb-1">
                            <div className="font-medium">{agent.name}</div>
                            <div
                                className={`text-xs font-semibold px-2 py-1 rounded ${
                                    agent.isActive ? "bg-green-100 text-green-800" : "bg-red-100 text-red-800"
                                }`}
                            >
                                {agent.isActive ? "Alive" : "Offline"}
                            </div>
                        </div>
                        <div className="text-sm text-gray-500">
                            Last ping: {agent.lastPingAt ? new Date(agent.lastPingAt).toLocaleString() : "never"}
                        </div>
                    </div>
                ))}
            </div>

            {showSecret && newAgent && (
                <div className="border border-blue-500 bg-blue-50 p-4 rounded space-y-2 text-sm max-w-md mt-4">
                    <div className="font-semibold">New Agent Created</div>
                    <div><strong>ID:</strong> {newAgent.id}</div>
                    <div><strong>Secret:</strong> <code>{newAgent.secret}</code></div>
                    <div className="text-gray-500">Use these values in the agent configuration.</div>
                    <button
                        onClick={() => setShowSecret(false)}
                        className="mt-2 text-sm text-blue-700 underline"
                    >
                        Close
                    </button>
                </div>
            )}

            <AgentCreateModal
                open={showCreateModal}
                onClose={() => setShowCreateModal(false)}
                onConfirm={createAgent}
            />
        </div>
    );
}
