import React, {useEffect, useState} from "react";
import axios from "../api/api";
import {CartesianGrid, Legend, Line, LineChart, Tooltip, XAxis, YAxis} from "recharts";

type Measurement = {
    stat: string;
    value: number;
};

type Metric = {
    name: string;
    measurements: Measurement[];
    baseUnit: string;
};

const metricsToShow: string[] = ["system.cpu.usage", "jvm.memory.used", "jvm.memory.max"];

const ActuatorDashboard = () => {
    const [metricData, setMetricData] = useState<Metric[]>([]);

    useEffect(() => {
        axios.get("/actuator/metrics").then(res => {
            const all: string[] = res.data.names || [];
            const selected: string[] = all.filter((name: string) =>
                metricsToShow.includes(name)
            );

            Promise.all(
                selected.map((name: string) => axios.get(`/actuator/metrics/${name}`))
            ).then(results => {
                const data: Metric[] = results.map((res, i): Metric => ({
                    name: selected[i],
                    measurements: res.data.measurements || [],
                    baseUnit: res.data.baseUnit || ""
                }));
                setMetricData(data);
            });
        });
    }, []);

    return (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {metricData.map((metric: Metric, index: number) => (
                <div key={index} className="bg-gray-100 p-4 rounded shadow">
                    <h2 className="font-bold mb-2">{metric.name}</h2>
                    {metric.measurements.map((m: Measurement, i: number) => (
                        <div key={i} className="text-sm mb-1">
                            <strong>{m.stat}: </strong>
                            {m.value} {metric.baseUnit}
                        </div>
                    ))}
                    <div className="mt-2">
                        <LineChart
                            width={250}
                            height={100}
                            data={metric.measurements.map((m: Measurement) => ({
                                stat: m.stat,
                                value: m.value
                            }))}
                        >
                            <CartesianGrid stroke="#ccc"/>
                            <XAxis dataKey="stat"/>
                            <YAxis/>
                            <Tooltip/>
                            <Legend/>
                            <Line type="monotone" dataKey="value" stroke="#8884d8"/>
                        </LineChart>
                    </div>
                </div>
            ))}
        </div>
    );
};

export default ActuatorDashboard;
