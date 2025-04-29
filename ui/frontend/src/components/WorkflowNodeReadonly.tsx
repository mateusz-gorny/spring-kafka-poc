export default function WorkflowNodeReadonly({ actionName }: { actionName: string }) {
    return (
        <div className="border rounded p-4 bg-gray-100">
            <h2 className="text-lg font-bold">{actionName}</h2>
            <div className="mt-2 text-sm text-gray-600">Inputs and Outputs available (readonly)</div>
        </div>
    );
}
