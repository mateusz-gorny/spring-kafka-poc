import React, { useState } from "react";

type JsonInputModalProps = {
    title?: string;
    onConfirm: (json: object) => void;
    onCancel: () => void;
};

export default function JsonInputModal({
                                           title = "Enter JSON Payload",
                                           onConfirm,
                                           onCancel,
                                       }: JsonInputModalProps) {
    const [value, setValue] = useState("{}");
    const [error, setError] = useState<string | null>(null);

    const handleConfirm = () => {
        try {
            const parsed = JSON.parse(value);
            onConfirm(parsed);
        } catch (e) {
            setError("Invalid JSON");
        }
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white rounded-lg p-6 shadow-lg w-full max-w-lg">
                <h2 className="text-xl font-semibold mb-4">{title}</h2>

                <textarea
                    rows={6}
                    className="w-full border px-3 py-2 rounded font-mono text-sm"
                    value={value}
                    onChange={(e) => {
                        setValue(e.target.value);
                        setError(null);
                    }}
                />

                {error && <p className="text-red-600 mt-2">{error}</p>}

                <div className="flex justify-end gap-3 mt-4">
                    <button
                        onClick={onCancel}
                        className="px-4 py-2 border rounded hover:bg-gray-100"
                    >
                        Cancel
                    </button>
                    <button
                        onClick={handleConfirm}
                        className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                    >
                        Send
                    </button>
                </div>
            </div>
        </div>
    );
}
