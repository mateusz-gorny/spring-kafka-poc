import axios from "axios";

const instance = axios.create({
    baseURL: "/api"
});

instance.interceptors.request.use(config => {
    const token = localStorage.getItem("token");
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
});

export const getWorkflowDefinitions = async () => {
    const response = await instance.get("/workflows/definitions");
    return response.data;
};

export const getWorkflowDefinition = async (id: string) => {
    const response = await instance.get(`/workflows/definitions/${id}`);
    return response.data;
};

export const startWorkflow = async (id: string) => {
    const response = await instance.post(`/workflows/execution/${id}/start`);
    return response.data;
};

export const getWorkflowInstances = async () => {
    const response = await instance.get("/workflows/instances");
    return response.data;
};

export const getWorkflowInstance = async (id: string) => {
    const response = await instance.get(`/workflows/instances/${id}`);
    return response.data;
};

export const getWorkflowInstanceStatus = async (id: string) => {
    const response = await instance.get(`/workflows/instances/${id}/status`);
    return response.data;
};

export const createWorkflow = async (definition: any) => {
    const response = await instance.post("/workflows/definitions", definition);
    return response.data;
};

export const updateWorkflow = async (id: string, definition: any) => {
    const response = await instance.put(`/workflows/definitions/${id}`, definition);
    return response.data;
};

export const getAvailableActions = async () => {
    const response = await instance.get("/actions");
    return response.data;
};

export const deleteWorkflow = async (id: string) => {
    const response = await instance.delete(`/workflows/definitions/${id}`);
    return response.data;
};

export const getWorkflowInstancesByDefinition = async (definitionId: string) => {
    const response = await instance.get(`/workflows/instances/by/${definitionId}`);
    return response.data;
};

export default instance;
