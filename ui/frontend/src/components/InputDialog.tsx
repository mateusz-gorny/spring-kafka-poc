import { useState } from "react";

export default function InputDialog({
                                        nodeId,
                                        inputKey,
                                        outputs,
                                        onSave,
                                        onClose,
                                    }: {
    nodeId: string;
    inputKey: string;
    outputs: { nodeId: string; actionName: string; outputKey: string; index: number }[];
    onSave: (value: string) => void;
    onClose: () => void;
}) {
    const [value, setValue] = useState("");

    return (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center">
            <div className="bg-white p-6 rounded space-y-4 w-96">
                <h2 className="text-xl font-bold">Bind Input: {inputKey}</h2>

                <input
                    type="text"
                    className="border p-2 w-full"
                    placeholder="Enter static value or use output"
                    value={value}
                    onChange={(e) => setValue(e.target.value)}
                />

                <div>
                    <h3 className="font-semibold mb-2">Available outputs:</h3>
                    <div className="space-y-1 max-h-40 overflow-auto">
                        {outputs.map((o, idx) => (
                            <div
                                key={o.nodeId + o.outputKey}
                                className="bg-gray-100 p-2 rounded cursor-pointer"
                                onClick={() => setValue(`#{${o.outputKey}}`)}
                            >
                                {`${o.index}: ${o.actionName} - ${o.outputKey}`}
                            </div>
                        ))}
                    </div>
                </div>

                <div className="flex justify-end gap-2">
                    <button
                        onClick={onClose}
                        className="bg-gray-400 hover:bg-gray-600 text-white py-1 px-4 rounded"
                    >
                        Cancel
                    </button>
                    <button
                        onClick={() => onSave(value)}
                        className="bg-green-500 hover:bg-green-700 text-white py-1 px-4 rounded"
                    >
                        Save
                    </button>
                </div>
            </div>
        </div>
    );
}
