import React, { useState } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

const Sidebar = () => {
    const [collapsed, setCollapsed] = useState(false);
    const { authorities, logout } = useAuth();

    return (
        <div className={`bg-black text-white transition-all duration-300 ${collapsed ? "w-16" : "w-64"} flex flex-col`}>
            <div className="flex items-center justify-between px-4 py-4 border-b border-gray-700">
                <span className={`${collapsed ? "hidden" : "text-lg font-bold"}`}>Monify</span>
                <button onClick={() => setCollapsed(!collapsed)} className="text-white">
                    {collapsed ? "➕" : "➖"}
                </button>
            </div>

            <nav className="flex-1 px-2 py-4 space-y-2">
                <Link to="/" className="block px-2 py-2 hover:bg-gray-800 rounded">
                    Project Dashboard
                </Link>

                {authorities?.includes("ADMIN") && (
                    <Link to="/projects/new" className="block px-2 py-2 hover:bg-gray-800 rounded">
                        Add Project
                    </Link>
                )}

                {authorities?.includes("AGENT_USER") && (
                    <Link to="/agents" className="block px-2 py-2 hover:bg-gray-800 rounded">
                        Agents
                    </Link>
                )}

                {authorities?.includes("ACTUATOR") && (
                    <Link to="/actuator" className="block px-2 py-2 hover:bg-gray-800 rounded">
                        Actuator
                    </Link>
                )}

                {(authorities?.includes("CREDENTIAL_ADMIN") || authorities?.includes("CREDENTIAL_VIEW")) && (
                    <Link to="/credentials" className="block px-2 py-2 hover:bg-gray-800 rounded">
                        Credentials
                    </Link>
                )}

                {(authorities?.includes("TRIGGER_ADMIN") || authorities?.includes("TRIGGER_VIEW")) && (
                    <Link to="/triggers" className="block px-2 py-2 hover:bg-gray-800 rounded">
                        Triggery
                    </Link>
                )}

                {authorities?.includes("WORKFLOW_VIEW") && (
                    <Link to="/workflows" className="block px-2 py-2 hover:bg-gray-800 rounded">
                        Workflows
                    </Link>
                )}
            </nav>

            <div className="p-2">
                <button
                    onClick={logout}
                    className="w-full bg-red-600 hover:bg-red-700 text-white py-2 rounded"
                >
                    Logout
                </button>
            </div>
        </div>
    );
};

export default Sidebar;
