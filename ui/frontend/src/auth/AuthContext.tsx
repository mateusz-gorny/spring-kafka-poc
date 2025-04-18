import React, { createContext, useContext, useState } from "react";

interface AuthContextProps {
    token: string | null;
    authorities: string[] | null;
    login: (token: string, role: string) => void;
    logout: () => void;
}

const AuthContext = createContext<AuthContextProps>({
    token: null,
    authorities: null,
    login: () => {},
    logout: () => {}
});

export const useAuth = () => useContext(AuthContext);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [token, setToken] = useState(localStorage.getItem("token"));
    const [authorities, setAuthorities] = useState<string[]>(
        JSON.parse(localStorage.getItem("authorities") || "[]")
    );

    const login = (jwt: string) => {
        const decoded = JSON.parse(atob(jwt.split(".")[1]));
        console.log(decoded);
        const roleList: string[] = decoded.authorities || [];
        localStorage.setItem("token", jwt);
        localStorage.setItem("authorities", JSON.stringify(roleList));
        setToken(jwt);
        setAuthorities(roleList);

        console.log(roleList);
    };

    const logout = () => {
        localStorage.clear();
        setToken(null);
        setAuthorities([]);
    };


    return (
        <AuthContext.Provider value={{ token, authorities, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};
