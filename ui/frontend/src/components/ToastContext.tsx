import React, {createContext, useCallback, useContext, useState} from "react";

type Toast = {
    id: number;
    message: string;
    type?: "success" | "error" | "info";
};

type ToastContextType = {
    showToast: (message: string, type?: "success" | "error" | "info") => void;
};

const ToastContext = createContext<ToastContextType | undefined>(undefined);

let toastId = 0;

export const ToastProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [toasts, setToasts] = useState<Toast[]>([]);

    const showToast = useCallback((message: string, type: "success" | "error" | "info" = "info") => {
        const id = toastId++;
        setToasts(prev => [...prev, { id, message, type }]);
        setTimeout(() => {
            setToasts(prev => prev.filter(t => t.id !== id));
        }, 3000);
    }, []);

    return (
        <ToastContext.Provider value={{ showToast }}>
            {children}
            <div className="fixed bottom-4 right-4 z-50 space-y-2">
                {toasts.map(toast => (
                    <div
                        key={toast.id}
                        className={`px-4 py-2 rounded shadow text-white ${
                            toast.type === "success" ? "bg-green-600"
                                : toast.type === "error" ? "bg-red-600"
                                    : "bg-gray-800"
                        }`}
                    >
                        {toast.message}
                    </div>
                ))}
            </div>
        </ToastContext.Provider>
    );
};

export const useToast = (): ToastContextType => {
    const context = useContext(ToastContext);
    if (!context) {
        throw new Error("useToast must be used within a ToastProvider");
    }
    return context;
};
