import { ReactNode } from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "./AuthContext";

export const ProtectedRoute = ({ children }: { children: ReactNode }) => {
    const { token } = useAuth();
    return token ? <>{children}</> : <Navigate to="/login" replace />;
};

