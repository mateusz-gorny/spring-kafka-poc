import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "../api/api";

type Ping = {
    timestamp: string;
    hostStats: Record<string, any>;
};

export default function AgentPingHistory() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [pings, setPings] = useState<Ping[]>([]);

    useEffect(() => {
        axios
            .get(`/agents/${id}/ping-history`)
            .then(res => res.data)
            .then(setPings);
    }, [id]);

    return (
        <div className="p-6 max-w-3xl mx-auto space-y-6">
            <div className="flex justify-between items-center">
                <h1 className="text-xl font-semibold">Ping History</h1>
                <button
                    onClick={() => navigate(`/agents/${id}`)}
                    className="text-sm text-blue-700 underline"
                >
                    Back to Agent
                </button>
            </div>

            {pings.length === 0 && (
                <div className="text-sm text-gray-500">No pings found.</div>
            )}

            <div className="space-y-4">
                {pings.map((ping, index) => (
                    <div
                        key={index}
                        className="border rounded p-4 text-sm space-y-2"
                    >
                        <div><strong>Timestamp:</strong> {new Date(ping.timestamp).toLocaleString()}</div>
                        <div>
                            <strong>Host Stats:</strong>{" "}
                            <span className="text-gray-500">
                                {Object.keys(ping.hostStats).length} fields
                            </span>
                            <pre className="bg-gray-50 mt-2 p-2 rounded text-xs overflow-x-auto">
                                {JSON.stringify(ping.hostStats, null, 2)}
                            </pre>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}
