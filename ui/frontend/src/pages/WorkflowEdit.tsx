import { useEffect, useState } from "react";
import { getWorkflowDefinition, getAvailableActions, updateWorkflow } from "../api/api";
import ActionSidebar from "../components/ActionSidebar";
import WorkflowCanvas from "../components/WorkflowCanvas";
import InputDialog from "../components/InputDialog";
import { useParams, useNavigate } from "react-router-dom";

interface ActionInfoDto {
    name: string;
    teamId: string;
    inputSchema: Record<string, any>;
    outputSchema: Record<string, any>;
}

interface NodeInstance {
    id: string;
    action: ActionInfoDto;
    inputs: Record<string, string>;
}

export default function WorkflowEdit() {
    const { id } = useParams<{ id: string }>();
    const [availableActions, setAvailableActions] = useState<ActionInfoDto[]>([]);
    const [nodes, setNodes] = useState<NodeInstance[]>([]);
    const [inputEdit, setInputEdit] = useState<{ nodeId: string; inputKey: string } | null>(null);
    const [dialogOpen, setDialogOpen] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        if (id) {
            loadWorkflow(id);
        }
        getAvailableActions()
            .then(setAvailableActions)
            .catch(() => setError("Failed to load actions"));
    }, [id]);

    const loadWorkflow = async (id: string) => {
        try {
            const data = await getWorkflowDefinition(id);
            const transitions = data.transitions || {};
            const loadedNodes: NodeInstance[] = [];

            const visited = new Set<string>();
            let currentActionName = transitions["start"]?.[0]?.actionName;

            while (currentActionName && !visited.has(currentActionName)) {
                const action = availableActions.find(a => a.name === currentActionName);
                if (!action) break;
                const transition = transitions[currentActionName] || [];
                const inputs = transition[0]?.outputToInputMapping || {};
                loadedNodes.push({
                    id: crypto.randomUUID(),
                    action,
                    inputs,
                });
                visited.add(currentActionName);
                currentActionName = transition[0]?.actionName;
            }

            setNodes(loadedNodes);
        } catch {
            setError("Failed to load workflow");
        }
    };

    const addAction = (action: ActionInfoDto) => {
        const id = crypto.randomUUID();
        setNodes((prev) => [...prev, { id, action, inputs: {} }]);
    };

    const removeAction = (id: string) => {
        setNodes((prev) => prev.filter((node) => node.id !== id));
    };

    const openInputDialog = (nodeId: string, inputKey: string) => {
        setInputEdit({ nodeId, inputKey });
        setDialogOpen(true);
    };

    const updateInput = (value: string) => {
        if (!inputEdit) return;
        setNodes((prev) =>
            prev.map((node) => {
                if (node.id === inputEdit.nodeId) {
                    return {
                        ...node,
                        inputs: {
                            ...node.inputs,
                            [inputEdit.inputKey]: value,
                        },
                    };
                }
                return node;
            })
        );
        setDialogOpen(false);
    };

    const handleUpdate = async () => {
        if (!id) return;
        if (nodes.length === 0) {
            setError("Add at least one action to the workflow.");
            return;
        }

        const transitions: Record<string, any[]> = {};

        if (nodes.length > 0) {
            transitions["start"] = [
                { actionName: nodes[0].action.name, outputToInputMapping: nodes[0].inputs }
            ];
        }

        for (let i = 0; i < nodes.length; i++) {
            const current = nodes[i];
            const next = nodes[i + 1];
            if (next) {
                transitions[current.action.name] = [
                    { actionName: next.action.name, outputToInputMapping: next.inputs }
                ];
            } else {
                transitions[current.action.name] = [];
            }
        }

        try {
            await updateWorkflow(id, { transitions });
            navigate("/workflows");
        } catch {
            setError("Failed to update workflow");
        }
    };

    const buildAvailableOutputs = (currentNodeIndex: number) => {
        return nodes
            .slice(0, currentNodeIndex)
            .flatMap((node, idx) =>
                Object.keys(node.action.outputSchema?.properties || {}).map((outputKey) => ({
                    nodeId: node.id,
                    actionName: node.action.name,
                    outputKey,
                    index: idx,
                }))
            );
    };

    return (
        <div className="flex h-screen">
            <div className="flex-1 overflow-auto p-4">
                <WorkflowCanvas nodes={nodes} onRemove={removeAction} onEditInput={openInputDialog} />
                <div className="mt-6">
                    <button
                        onClick={handleUpdate}
                        className="bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-6 rounded"
                    >
                        Update Workflow
                    </button>
                </div>
            </div>
            <div className="w-64 border-l p-4 overflow-auto">
                <ActionSidebar actions={availableActions} onAdd={addAction} />
            </div>
            {dialogOpen && inputEdit && (
                <InputDialog
                    onSave={updateInput}
                    onClose={() => setDialogOpen(false)}
                    nodeId={inputEdit.nodeId}
                    inputKey={inputEdit.inputKey}
                    outputs={buildAvailableOutputs(nodes.findIndex((n) => n.id === inputEdit.nodeId))}
                />
            )}
            {error && <div className="p-4 text-red-600">{error}</div>}
        </div>
    );
}
