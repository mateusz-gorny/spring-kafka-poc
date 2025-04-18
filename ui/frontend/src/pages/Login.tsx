import React, {useState} from "react";
import {useAuth} from "../auth/AuthContext";
import {useNavigate} from "react-router-dom";
import axios from "../api/api";

const Login = () => {
    const [username, setUsername] = useState("user");
    const [password, setPassword] = useState("user123");
    const [error, setError] = useState("");
    const {login} = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError("");

        try {
            const res = await axios.post("/login", {username, password});
            const token = res.data.access_token;
            const decoded = JSON.parse(atob(token.split(".")[1]));
            const role = decoded.role;

            login(token, role);
            navigate("/");
        } catch (err) {
            setError("Invalid username or password");
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100">
            <form onSubmit={handleSubmit} className="bg-white p-6 rounded shadow w-96">
                <h2 className="text-xl font-bold mb-4 text-center">Monify Login</h2>
                {error && <p className="text-red-500 mb-2 text-sm">{error}</p>}
                <input
                    className="w-full border mb-3 px-3 py-2 rounded"
                    placeholder="Username"
                    value={username}
                    onChange={e => setUsername(e.target.value)}
                />
                <input
                    type="password"
                    className="w-full border mb-4 px-3 py-2 rounded"
                    placeholder="Password"
                    value={password}
                    onChange={e => setPassword(e.target.value)}
                />
                <button
                    type="submit"
                    className="bg-blue-600 hover:bg-blue-700 text-white w-full py-2 rounded"
                >
                    Login
                </button>
            </form>
        </div>
    );
};

export default Login;
