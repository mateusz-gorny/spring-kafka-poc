import React, {useEffect, useState} from "react";
import axios from "../api/api";
import {useAuth} from "../auth/AuthContext";

interface Project {
    id: number;
    name: string;
    url: string;
    environment: string;
}

const Dashboard = () => {
    const [projects, setProjects] = useState<Project[]>([]);
    const {authorities} = useAuth();

    useEffect(() => {
        axios.get("/projects").then(res => setProjects(res.data));
    }, []);

    return (
        <div className="flex h-screen">
            <div className="flex flex-col flex-1 bg-white">
                <main className="p-6 overflow-y-auto">
                    <h1 className="text-2xl font-semibold mb-4">Projects</h1>
                    <div className="grid gap-4">
                        {projects.map(project => (
                            <div key={project.id} className="p-4 border rounded shadow-sm">
                                <div className="font-bold">{project.name}</div>
                                <div className="text-sm text-gray-500">{project.url}</div>
                                <div className="text-sm">{project.environment}</div>
                            </div>
                        ))}
                    </div>
                    {authorities?.includes("ADMIN") && (
                        <div className="mt-6">
                            <a
                                href="/projects/new"
                                className="inline-block bg-blue-600 text-white px-4 py-2 rounded"
                            >
                                Add New Project
                            </a>
                        </div>
                    )}
                </main>
            </div>
        </div>
    );
};

export default Dashboard;
