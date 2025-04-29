export default function WorkflowNode({
                                         node,
                                         onRemove,
                                         onEditInput,
                                     }: {
    node: {
        id: string;
        action: {
            name: string;
            teamId: string;
            inputSchema: Record<string, any>;
            outputSchema: Record<string, any>;
        };
        inputs: Record<string, string>;
    };
    onRemove: () => void;
    onEditInput: (nodeId: string, inputKey: string) => void;
}) {
    return (
        <div className="border rounded p-4 bg-gray-100 relative">
            <button
                onClick={onRemove}
                className="absolute top-2 right-2 text-red-500 font-bold"
            >
                ×
            </button>
            <h2 className="text-lg font-bold mb-2">{node.action.name}</h2>

            <div className="mt-2">
                <h3 className="font-semibold">Inputs:</h3>
                <div className="flex flex-wrap gap-2 mt-1">
                    {Object.keys(node.action.inputSchema?.properties || {}).map((key) => (
                        <div
                            key={key}
                            className="bg-white border rounded px-2 py-1 text-sm cursor-pointer"
                            title="Click to bind input"
                            onClick={() => onEditInput(node.id, key)}
                        >
                            {key} {node.inputs[key] ? `: ${node.inputs[key]}` : ""}
                        </div>
                    ))}
                </div>
            </div>

            <div className="mt-4">
                <h3 className="font-semibold">Outputs:</h3>
                <div className="flex flex-wrap gap-2 mt-1">
                    {Object.keys(node.action.outputSchema?.properties || {}).map((key) => (
                        <div
                            key={key}
                            className="bg-white border rounded px-2 py-1 text-sm"
                            title={`Output available: ${key}`}
                        >
                            {key}
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}
