import React, {useEffect, useState} from "react";
import axios from "../api/api";
import {Prism as SyntaxHighlighter} from 'react-syntax-highlighter';
import {atomDark} from 'react-syntax-highlighter/dist/esm/styles/prism';
import {useNavigate, useParams} from "react-router-dom";
import ActuatorDashboard from "./ActuatorDashboard";

const ActuatorExplorer = () => {
    const [endpoints, setEndpoints] = useState<string[]>([]);
    const [json, setJson] = useState<Record<string, any> | null>(null);
    const [metricsList, setMetricsList] = useState<string[]>([]);
    const {endpoint, name} = useParams();  // Destructure `endpoint` and `name` from `useParams()`
    const navigate = useNavigate();

    useEffect(() => {
        axios.get("/actuator")
            .then(res => {
                const links = res.data._links || {};
                setEndpoints(Object.keys(links));
            })
            .catch(error => {
                console.error("Error fetching endpoints:", error);
            });
    }, []);

    useEffect(() => {
        // Handle case when no `endpoint` is selected
        if (!endpoint && !name) {
            setJson(null);
            setMetricsList([]);
            return;
        }

        // Handle the case where the "metrics" endpoint is selected, but no specific metric name is provided
        if (endpoint === "metrics" && !name) {
            axios.get("/actuator/metrics")
                .then(res => {
                    setMetricsList(res.data.names || []);
                    setJson(null); // Reset json when listing metrics
                })
                .catch(error => {
                    console.error("Error fetching metrics:", error);
                });
            return;
        }

        // If a specific metric `name` is selected (e.g., /actuator/metrics/disk.total)
        if (name) {
            axios.get(`/actuator/metrics/${name}`)
                .then(res => {
                    setJson(res.data);  // Set the metric data for the selected metric
                })
                .catch(error => {
                    console.error("Error fetching metric data:", error);
                });
            return;
        }

        // Handle other actuator endpoints (non-metric endpoints)
        axios.get(`/actuator/${endpoint}`)
            .then(res => {
                setJson(res.data);
                setMetricsList([]);
            })
            .catch(error => {
                console.error("Error fetching actuator data:", error);
            });
    }, [endpoint, name]);  // Watch for both `endpoint` and `name`

    // Navigate to the metric page
    const loadMetric = (name: string) => {
        navigate(`/actuator/metrics/${name}`);
    };

    return (
        <div className="flex h-screen overflow-hidden">
            <div className="flex flex-col flex-1 bg-white overflow-hidden">
                <main className="flex flex-1 overflow-hidden">
                    <aside className="w-64 border-r pr-4 overflow-y-auto sticky top-0 max-h-screen">
                        <h2 className="font-bold mb-2 p-4">Actuator Endpoints</h2>
                        <ul className="space-y-1 px-4">
                            <li key="dashboard">
                                <button
                                    onClick={() => navigate("/actuator")}
                                    className={`text-left w-full px-2 py-1 rounded ${
                                        !endpoint && !name ? "bg-blue-100 font-bold" : "hover:bg-gray-100"
                                    }`}
                                >
                                    Dashboard
                                </button>
                            </li>
                            {endpoints.map(ep => (
                                <li key={ep}>
                                    <button
                                        onClick={() => navigate(`/actuator/${ep}`)}
                                        className={`text-left w-full px-2 py-1 rounded ${
                                            endpoint === ep ? "bg-blue-100 font-bold" : "hover:bg-gray-100"
                                        }`}
                                    >
                                        {ep}
                                    </button>
                                </li>
                            ))}
                        </ul>
                    </aside>
                    <section className="flex-1 overflow-auto p-6">
                        <h2 className="font-bold text-lg mb-2">{endpoint || "Dashboard"}</h2>

                        {/* Only show ActuatorDashboard if no endpoint or name is set */}
                        {!endpoint && !name && <ActuatorDashboard/>}

                        {json && (
                            <div className="bg-gray-900 text-white p-4 rounded text-sm overflow-auto max-h-[80vh]">
                                <SyntaxHighlighter
                                    language="json"
                                    style={atomDark}
                                    wrapLongLines={true}
                                    customStyle={{backgroundColor: "transparent"}}
                                >
                                    {JSON.stringify(json, null, 2)}
                                </SyntaxHighlighter>
                            </div>
                        )}

                        {metricsList.length > 0 && (
                            <div className="space-y-2 mb-4">
                                <h3 className="font-semibold">Available Metrics:</h3>
                                {metricsList.map((name) => (
                                    <button
                                        key={name}
                                        onClick={() => loadMetric(name)}
                                        className="block text-left px-3 py-1 bg-white border rounded hover:bg-gray-100"
                                    >
                                        {name}
                                    </button>
                                ))}
                            </div>
                        )}
                    </section>
                </main>
            </div>
        </div>
    );
};

export default ActuatorExplorer;
