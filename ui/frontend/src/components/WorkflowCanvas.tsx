import WorkflowNode from "./WorkflowNode";

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

export default function WorkflowCanvas({
                                           nodes,
                                           onRemove,
                                           onEditInput,
                                       }: {
    nodes: NodeInstance[];
    onRemove: (id: string) => void;
    onEditInput: (nodeId: string, inputKey: string) => void;
}) {
    return (
        <div className="space-y-4">
            {nodes.map((node) => (
                <WorkflowNode key={node.id} node={node} onRemove={() => onRemove(node.id)} onEditInput={onEditInput} />
            ))}
        </div>
    );
}
