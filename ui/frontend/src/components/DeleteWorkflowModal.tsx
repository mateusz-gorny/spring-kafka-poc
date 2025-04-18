import React from "react";

type DeleteWorkflowModalProps = {
    workflowName: string;
    onConfirm: () => void;
    onCancel: () => void;
};

export default function DeleteWorkflowModal({
                                                workflowName,
                                                onConfirm,
                                                onCancel
                                            }: DeleteWorkflowModalProps) {
    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white rounded-lg p-6 shadow-lg w-full max-w-md">
                <h2 className="text-xl font-semibold mb-4">Delete Workflow</h2>
                <p className="mb-6 text-gray-700">
                    Are you sure you want to delete <strong>{workflowName}</strong>? This action cannot be undone.
                </p>
                <div className="flex justify-end gap-4">
                    <button
                        onClick={onCancel}
                        className="px-4 py-2 rounded border border-gray-300 hover:bg-gray-100"
                    >
                        Cancel
                    </button>
                    <button
                        onClick={onConfirm}
                        className="px-4 py-2 rounded bg-red-600 text-white hover:bg-red-700"
                    >
                        Delete
                    </button>
                </div>
            </div>
        </div>
    );
}
