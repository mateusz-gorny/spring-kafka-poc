import React, {useState} from "react";
import axios from "../api/api";
import {useNavigate} from "react-router-dom";

const ProjectForm = () => {
    const [name, setName] = useState("");
    const [url, setUrl] = useState("");
    const [environment, setEnvironment] = useState("dev");
    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        await axios.post("/projects", {name, url, environment});
        navigate("/");
    };

    return (
        <div className="flex h-screen">
            <div className="flex flex-col flex-1 bg-white">
                <main className="p-6">
                    <h1 className="text-2xl font-semibold mb-4">Create Project</h1>
                    <form onSubmit={handleSubmit} className="max-w-md space-y-4">
                        <input
                            className="w-full border px-3 py-2 rounded"
                            placeholder="Project Name"
                            value={name}
                            onChange={e => setName(e.target.value)}
                        />
                        <input
                            className="w-full border px-3 py-2 rounded"
                            placeholder="URL"
                            value={url}
                            onChange={e => setUrl(e.target.value)}
                        />
                        <select
                            className="w-full border px-3 py-2 rounded"
                            value={environment}
                            onChange={e => setEnvironment(e.target.value)}
                        >
                            <option value="dev">dev</option>
                            <option value="test">test</option>
                            <option value="prod">prod</option>
                        </select>
                        <button type="submit" className="bg-blue-600 text-white px-4 py-2 rounded">
                            Save
                        </button>
                    </form>
                </main>
            </div>
        </div>
    );
};

export default ProjectForm;
