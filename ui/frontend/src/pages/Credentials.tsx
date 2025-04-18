import React, {useEffect, useState} from "react";
import {Link} from "react-router-dom";
import axios from "../api/api";
import {useAuth} from "../auth/AuthContext";
import {useToast} from "../components/ToastContext";

interface Credential {
    id: string;
    name: string;
    domain: string;
    username: string;
    password: string;
    type: string;
    extra: Record<string, any>;
}

const Credentials = () => {
    const [credentials, setCredentials] = useState<Credential[]>([]);
    const {authorities} = useAuth();
    const {showToast} = useToast();

    const canEdit = authorities?.includes("CREDENTIAL_ADMIN");

    const [showModal, setShowModal] = useState(false);
    const [selectedId, setSelectedId] = useState<string | null>(null);

    useEffect(() => {
        axios.get("/credentials").then(res => setCredentials(res.data));
    }, []);

    const handleDeleteClick = (id: string) => {
        setSelectedId(id);
        setShowModal(true);
    };

    const confirmDelete = async () => {
        if (!selectedId) return;

        try {
            await axios.delete(`/credentials/${selectedId}`);
            setCredentials(prev => prev.filter(c => c.id !== selectedId));
            setShowModal(false);
            setSelectedId(null);
        } catch (err) {
            console.error("Failed to delete credential", err);
            showToast("Error deleting credential", "error");
        }
    };

    return (
        <div className="flex h-screen">
            {showModal && (
                <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
                    <div className="bg-white rounded-lg p-6 w-full max-w-md shadow-lg">
                        <h2 className="text-lg font-semibold mb-4">Confirm Deletion</h2>
                        <p className="mb-6 text-gray-700">Are you sure you want to delete this credential?</p>
                        <div className="flex justify-end gap-4">
                            <button
                                className="px-4 py-2 bg-gray-200 hover:bg-gray-300 rounded"
                                onClick={() => setShowModal(false)}
                            >
                                Cancel
                            </button>
                            <button
                                className="px-4 py-2 bg-red-600 hover:bg-red-700 text-white rounded"
                                onClick={confirmDelete}
                            >
                                Yes, delete
                            </button>
                        </div>
                    </div>
                </div>
            )}
            <div className="flex flex-col flex-1 bg-white">
                <main className="p-6 overflow-y-auto">
                    <div className="flex justify-between items-center mb-4">
                        <h1 className="text-2xl font-semibold">Credentiale</h1>
                        {canEdit && (
                            <Link
                                to="/credentials/new"
                                className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded"
                            >
                                Add New
                            </Link>
                        )}
                    </div>

                    <div className="grid gap-4">
                        {credentials.map((cred) => (
                            <div key={cred.id} className="p-4 border rounded shadow-sm">
                                <div className="font-bold">{cred.name}</div>
                                <div className="text-sm text-gray-500">{cred.domain}</div>
                                <div className="text-sm">
                                    <strong>User:</strong> {cred.username}
                                </div>
                                <div className="text-sm">
                                    <strong>Pass:</strong> {cred.password}
                                </div>
                                <div className="text-sm">
                                    <strong>Type:</strong> {cred.type}
                                </div>

                                {cred.type === "WEB" && (
                                    <div className="text-sm mt-2 space-y-1">
                                        <div><strong>Username Selector:</strong> {cred.extra?.usernameSelector}</div>
                                        <div><strong>Password Selector:</strong> {cred.extra?.passwordSelector}</div>
                                        <div><strong>Submit Selector:</strong> {cred.extra?.submitSelector}</div>
                                    </div>
                                )}

                                {canEdit && (
                                    <div className="mt-3 flex gap-4 items-center text-sm">
                                        <Link
                                            to={`/credentials/${cred.id}/edit`}
                                            className="text-blue-600 hover:underline"
                                        >
                                            Edit
                                        </Link>
                                        <button
                                            onClick={() => handleDeleteClick(cred.id)}
                                            className="text-red-600 hover:underline"
                                        >
                                            Delete
                                        </button>
                                    </div>
                                )}
                            </div>
                        ))}
                    </div>
                </main>
            </div>
        </div>
    );
};

export default Credentials;
