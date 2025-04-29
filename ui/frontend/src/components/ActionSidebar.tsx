interface ActionInfoDto {
    name: string;
    teamId: string;
    inputSchema: Record<string, any>;
    outputSchema: Record<string, any>;
}

export default function ActionSidebar({
                                          actions,
                                          onAdd,
                                      }: {
    actions: ActionInfoDto[];
    onAdd: (action: ActionInfoDto) => void;
}) {
    return (
        <div className="space-y-2">
            {actions.map((action) => (
                <button
                    key={action.name}
                    onClick={() => onAdd(action)}
                    className="w-full bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
                >
                    {action.name}
                </button>
            ))}
        </div>
    );
}
