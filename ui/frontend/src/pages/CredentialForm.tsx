import React, {useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import axios from "../api/api";
import {useAuth} from "../auth/AuthContext";
import {useToast} from "../components/ToastContext";

interface CredentialEditDto {
    id?: string;
    name: string;
    domain: string;
    username: string;
    password: string;
    type: "BASIC" | "WEB";
    extra: {
        usernameSelector?: string;
        passwordSelector?: string;
        submitSelector?: string;
        [key: string]: any;
    };
}

const CredentialForm = () => {
    const {id} = useParams();
    const navigate = useNavigate();
    const {authorities} = useAuth();
    const {showToast} = useToast();

    const canEdit = authorities?.includes("CREDENTIAL_ADMIN");
    const [form, setForm] = useState<CredentialEditDto>({
        name: "",
        domain: "",
        username: "",
        password: "",
        type: "BASIC",
        extra: {},
    });

    useEffect(() => {
        if (id) {
            axios.get(`/credentials/${id}`).then(res => {
                setForm(res.data);
            });
        }
    }, [id]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const {name, value} = e.target;
        if (["usernameSelector", "passwordSelector", "submitSelector"].includes(name)) {
            setForm(prev => ({
                ...prev,
                extra: {
                    ...prev.extra,
                    [name]: value,
                }
            }));
        } else {
            setForm(prev => ({...prev, [name]: value}));
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await axios.post("/credentials", form);
            navigate("/credentials");
        } catch (err) {
            showToast("Failed to save credential.", "error");
            console.error(err);
        }
    };

    if (!canEdit) {
        return <div className="p-8 text-red-500">You are not authorized to edit credentials.</div>;
    }

    return (
        <div className="flex h-screen">
            <div className="flex flex-col flex-1 bg-white">
                <main className="p-6 overflow-y-auto">
                    <h1 className="text-2xl font-semibold mb-4">{id ? "Edit" : "Create"} Credential</h1>

                    <form onSubmit={handleSubmit} className="max-w-xl space-y-4">
                        <input
                            type="text"
                            name="name"
                            value={form.name}
                            onChange={handleChange}
                            placeholder="Name"
                            className="w-full border px-3 py-2 rounded"
                            required
                        />

                        <input
                            type="text"
                            name="domain"
                            value={form.domain}
                            onChange={handleChange}
                            placeholder="Domain"
                            className="w-full border px-3 py-2 rounded"
                            required
                        />

                        <input
                            type="text"
                            name="username"
                            value={form.username}
                            onChange={handleChange}
                            placeholder="Username"
                            className="w-full border px-3 py-2 rounded"
                            required
                        />

                        <input
                            type="password"
                            name="password"
                            value={form.password}
                            onChange={handleChange}
                            placeholder="Password"
                            className="w-full border px-3 py-2 rounded"
                            required
                        />

                        <select
                            name="type"
                            value={form.type}
                            onChange={handleChange}
                            className="w-full border px-3 py-2 rounded"
                        >
                            <option value="BASIC">BASIC</option>
                            <option value="WEB">WEB</option>
                        </select>

                        {form.type === "WEB" && (
                            <div className="space-y-2">
                                <input
                                    type="text"
                                    name="usernameSelector"
                                    value={form.extra.usernameSelector || ""}
                                    onChange={handleChange}
                                    placeholder="Username Selector"
                                    className="w-full border px-3 py-2 rounded"
                                />

                                <input
                                    type="text"
                                    name="passwordSelector"
                                    value={form.extra.passwordSelector || ""}
                                    onChange={handleChange}
                                    placeholder="Password Selector"
                                    className="w-full border px-3 py-2 rounded"
                                />

                                <input
                                    type="text"
                                    name="submitSelector"
                                    value={form.extra.submitSelector || ""}
                                    onChange={handleChange}
                                    placeholder="Submit Selector"
                                    className="w-full border px-3 py-2 rounded"
                                />
                            </div>
                        )}

                        <button
                            type="submit"
                            className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded"
                        >
                            Save
                        </button>
                    </form>
                </main>
            </div>
        </div>
    );
};

export default CredentialForm;
