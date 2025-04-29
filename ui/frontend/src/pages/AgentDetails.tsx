import React, {useEffect, useState} from "react";
import {useParams, useNavigate} from "react-router-dom";
import axios from "../api/api";

type AgentDetails = {
    id: string;
    name: string;
    lastPingAt?: string;
    isActive: boolean;
    hostStats?: Record<string, any>;
    secret: string;
};

export default function AgentDetails() {
    const {id} = useParams();
    const navigate = useNavigate();
    const [details, setDetails] = useState<AgentDetails | null>(null);
    const [revealedSecret, setRevealedSecret] = useState<string | null>(null);
    const [credentials, setCredentials] = useState({username: "", password: ""});
    const [error, setError] = useState("");

    useEffect(() => {
        axios
            .get(`/agents/${id}`)
            .then(res => res.data)
            .then(setDetails);
    }, [id]);

    const revealSecret = async () => {
        const res = await axios
            .post(`/agents/${id}/reveal-secret`, {
                credentials: credentials
            });
        if (res.status === 200) {
            const data = await res.data;
            setRevealedSecret(data.secret);
            setError("");
        } else {
            setError("Invalid credentials.");
        }
    };

    if (!details) return null;

    return (
        <div className="p-6 space-y-6 max-w-2xl">
            <div className="flex justify-between items-center">
                <h1 className="text-xl font-semibold">{details.name}</h1>
                <span
                    className={`text-xs font-semibold px-2 py-1 rounded ${
                        details.isActive ? "bg-green-100 text-green-800" : "bg-red-100 text-red-800"
                    }`}
                >
                    {details.isActive ? "Alive" : "Offline"}
                </span>
            </div>

            <div className="border rounded p-4 space-y-2 text-sm">
                <div><strong>ID:</strong> {details.id}</div>
                <div>
                    <strong>Last ping:</strong>{" "}
                    {details.lastPingAt ? new Date(details.lastPingAt).toLocaleString() : "never"}
                </div>
                <div>
                    <strong>Secret:</strong>{" "}
                    {revealedSecret && revealedSecret !== "show" && (
                        <code>{revealedSecret}</code>
                    )}
                    {!revealedSecret && (
                        <>
                            ***************
                            <button
                                onClick={() => setRevealedSecret("show")}
                                className="ml-2 text-blue-700 underline text-sm"
                            >
                                Reveal
                            </button>
                        </>
                    )}
                </div>

                {revealedSecret === "show" && (
                    <div className="mt-2 space-y-2">
                        <div className="text-sm font-medium">Confirm identity</div>
                        <input
                            className="border p-2 w-full rounded text-sm"
                            placeholder="Username"
                            value={credentials.username}
                            onChange={e => setCredentials({...credentials, username: e.target.value})}
                        />
                        <input
                            className="border p-2 w-full rounded text-sm"
                            type="password"
                            placeholder="Password"
                            value={credentials.password}
                            onChange={e => setCredentials({...credentials, password: e.target.value})}
                        />
                        <button
                            className="bg-blue-600 text-white px-4 py-2 rounded text-sm"
                            onClick={revealSecret}
                        >
                            Submit
                        </button>
                        {error && <div className="text-red-600 text-sm">{error}</div>}
                    </div>
                )}
            </div>

            {details.hostStats && (
                <div className="border rounded p-4">
                    <div className="text-sm font-semibold mb-2">Latest Host Stats</div>
                    <pre className="text-xs bg-gray-100 rounded p-2">
                        {JSON.stringify(details.hostStats, null, 2)}
                    </pre>
                </div>
            )}

            <div>
                <button
                    onClick={() => navigate(`/agents/${id}/ping-history`)}
                    className="text-sm text-blue-700 underline"
                >
                    View Ping History
                </button>
            </div>
        </div>
    );
}
