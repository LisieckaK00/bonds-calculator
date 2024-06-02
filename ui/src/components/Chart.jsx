import React, { useEffect, useState } from "react";
import axios from 'axios';
import { LineChart, Line, XAxis, YAxis, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { colorMap } from "./Colors";

export default function Chart(props){
    const [chartData, setChartData] = useState([]);
    const url = props.type === 'Overview' ? 
    `http://localhost:9000/api/bonds/all/getAllBonds/${props.quantity}/${props.months}` : 
    `http://localhost:9000/api/bonds/${props.type}/${props.quantity}/${props.months}`

    useEffect(() => {
        axios.get(url)
          .then(response => {
            const allBondsChartData = [];
            Object.keys(response.data).forEach(bondName => {
              const bondData = response.data[bondName];
              const bondChartData = bondData.map((row, index) => ({
                bondName,
                month: index + 1,
                Result: Math.round(row[9] * 100) / 100,
              }));
              allBondsChartData.push(...bondChartData);
            });
            setChartData(allBondsChartData);
          })
          .catch(error => console.error('Error:', error));
      }, [props.type]);

    const renderLines = () => {
        const bondNames = [...new Set(chartData.map(data => data.bondName))];
        return bondNames.map(bondName => {
          const bondData = chartData.filter(data => data.bondName === bondName);
          return (
            <Line
              key={bondName}
              type="monotone"
              dataKey="Result"
              name={bondName}
              data={bondData}
              stroke={colorMap[bondName] || colorMap.defaultColor}
              dot={{ r: 0 }}
              activeDot={{ r: 8 }}
            />
          );
        });
    };

    return (
        <>
        <div className="chart--wrapper">
            <p className="chart--text">Chart for <span style={{ color: colorMap[props.type] || colorMap.defaultColor }}>{props.type}</span></p>
            <div style={{ width: '70vw', height: '500px' }}>
                <ResponsiveContainer width="100%" height="100%">
                    <LineChart
                        key={`${props.type}-${Date.now()}`}
                        data={chartData}
                        margin={{ top: 50, right: 100, left: 20, bottom: 20 }}
                    >
                        <XAxis dataKey="month" type="number" domain={[1, props.months]} allowDecimals={false} ticks={Array.from({ length: props.months }, (_, i) => i + 1)} label={{ value: 'Month', position: 'insideBottomRight', offset: -10 }} />
                        <YAxis domain={['auto', 'auto']} label={{ value: 'Final Result', position: 'insideTop', offset: -40 }} />
                        <Tooltip />
                        <Legend />
                        {renderLines(props.type)}
                    </LineChart>
                </ResponsiveContainer>
            </div>
        </div>
        </>
    )
}