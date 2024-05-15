import Nav from "../components/Nav"
import "../styles/dashboard.css"
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { useTheme } from '@mui/material/styles';
import {
    processData,
    otsData,
    rorData,
    dorData,
    tosData,
    coiData,
    edoData,
    rosData,
    rodData,
} from '../data.js';

  
const processedOtsData = processData(otsData);
const processedRorData = processData(rorData);
const processedDorData = processData(dorData);
const processedTosData = processData(tosData);
const processedCoiData = processData(coiData);
const processedEdoData = processData(edoData);
const processedRosData = processData(rosData);
const processedRodData = processData(rodData);

export default function Dashboard(props) {
    const colorMap = {
      OTS: theme.palette.primary.main,
      ROR: theme.palette.secondary.main,
      DOR: '#82ca9d',
      TOS: '#ff7300',
      COI: '#8884d8',
      EDO: '#8dd1e1',
      ROS: '#ffc658',
      ROD: '#d0ed57'
    };

    const theme = useTheme();
    const xTicks = Array.from({ length: 144 }, (_, i) => i + 1);
  
    return (
      <div className="dashboard--wrapper">
        <Nav />
  
        <div className="chart--wrapper">
          <p className="chart--text">Chart for <span style={{ color: theme.palette.primary.main }}>{props.type}</span></p>
          <div style={{ width: '70%', height: 500 }}>
            <ResponsiveContainer>
                <LineChart
                    width={500}
                    height={300}
                    margin={{
                    top: 5, right: 30, left: 20, bottom: 5,
                    }}
                >
                
                <XAxis
                    dataKey="month"
                    ticks={xTicks}
                    type="number"
                    domain={[1, 24]}
                    allowDecimals={false}
                    scale="linear"
                />
                <YAxis
                    domain={[100, 'auto']}
                />
                <Tooltip />
                <Legend />

                {props.type === 'Overview' ? (
                <>

                    <Line type="monotone" dataKey="sum" data={processedOtsData} name="OTS" stroke={theme.palette.primary.main} dot={{ r: 0, fill: theme.palette.primary.main, opacity: 0.5 }} />
                    <Line type="monotone" dataKey="sum" data={processedRorData} name="ROR" stroke={theme.palette.secondary.main} dot={{ r: 0, fill: theme.palette.secondary.main, opacity: 0.5 }} />
                    <Line type="monotone" dataKey="sum" data={processedDorData} name="DOR" stroke="#82ca9d" dot={{ r: 0, fill: '#82ca9d', opacity: 0.5 }} />
                    <Line type="monotone" dataKey="sum" data={processedTosData} name="TOS" stroke="#ff7300" dot={{ r: 0, fill: '#ff7300', opacity: 0.5 }} />
                    <Line type="monotone" dataKey="sum" data={processedCoiData} name="COI" stroke="#8884d8" dot={{ r: 0, fill: '#8884d8', opacity: 0.5 }} />
                    <Line type="monotone" dataKey="sum" data={processedEdoData} name="EDO" stroke="#8dd1e1" dot={{ r: 0, fill: '#8dd1e1', opacity: 0.5 }} />
                    <Line type="monotone" dataKey="sum" data={processedRosData} name="ROS" stroke="#ffc658" dot={{ r: 0, fill: '#ffc658', opacity: 0.5 }} />
                    <Line type="monotone" dataKey="sum" data={processedRodData} name="ROD" stroke="#d0ed57" dot={{ r: 0, fill: '#d0ed57', opacity: 0.5 }} />
                </>
                ) : (
                <>
                    <Line type="monotone" dataKey="sum" data={eval(`processed${props.type}Data`)} name={props.type} stroke={theme.palette.primary.main} dot={{ r: 0, fill: theme.palette.primary.main, opacity: 0.5 }} />
                </>
                )}
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>
    );
  }