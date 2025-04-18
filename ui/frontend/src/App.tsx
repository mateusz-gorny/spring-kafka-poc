import React from "react";
import {BrowserRouter as Router, Navigate, Route, Routes} from "react-router-dom";
import {AuthProvider} from "./auth/AuthContext";
import {ProtectedRoute} from "./auth/ProtectedRoute";
import {ToastProvider} from "./components/ToastContext";

import Login from "./pages/Login";
import Layout from "./components/Layout";
import Dashboard from "./pages/Dashboard";

import Credentials from "./pages/Credentials";
import CredentialForm from "./pages/CredentialForm";

import Triggers from "./pages/Triggers";
import TriggerForm from "./pages/TriggerForm";

import Workflows from "./pages/Workflows";
import WorkflowCreate from "./pages/WorkflowCreate";
import WorkflowEdit from "./pages/WorkflowEdit";
import WorkflowDetails from "./pages/WorkflowDetails";
import WorkflowInstances from "./pages/WorkflowInstances";
import WorkflowInstanceDetails from "./pages/WorkflowInstanceDetails";
import ProjectForm from "./pages/ProjectForm";
import ActuatorExplorer from "./pages/ActuatorExplorer";

const App = () => (
    <AuthProvider>
        <ToastProvider>
            <Router>
                <Routes>
                    <Route path="/login" element={<Login/>}/>
                    <Route
                        path="/"
                        element={
                            <ProtectedRoute>
                                <Layout/>
                            </ProtectedRoute>
                        }
                    >
                        <Route index element={<Dashboard/>}/>

                        {/* Credentials */}
                        <Route path="credentials" element={<Credentials/>}/>
                        <Route path="credentials/new" element={<CredentialForm/>}/>
                        <Route path="credentials/:id/edit" element={<CredentialForm/>}/>

                        <Route path="/projects/new" element={<ProjectForm/>}/>
                        <Route path="/actuator/:endpoint?" element={<ActuatorExplorer/>}/>
                        <Route path="/actuator/metrics/:name" element={<ActuatorExplorer/>}/>

                        {/* Triggers */}
                        <Route path="triggers" element={<Triggers/>}/>
                        <Route path="triggers/new" element={<TriggerForm/>}/>
                        <Route path="triggers/:id" element={<TriggerForm/>}/>

                        {/* Workflows */}
                        <Route path="workflows" element={<Workflows/>}/>
                        <Route path="workflows/create" element={<WorkflowCreate/>}/>
                        <Route path="workflows/:id/edit" element={<WorkflowEdit/>}/>
                        <Route path="workflows/:id" element={<WorkflowDetails/>}/>
                        <Route path="workflows/:id/instances" element={<WorkflowInstances/>}/>
                        <Route path="instances/:id" element={<WorkflowInstanceDetails/>}/>

                        {/* Catch-all */}
                        <Route path="*" element={<Navigate to="/"/>}/>
                    </Route>
                </Routes>
            </Router>
        </ToastProvider>
    </AuthProvider>
);

export default App;
