import Nav from "../components/Nav"
import "../styles/dashboard.css"
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper } from '@mui/material';
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

  
const processedOTSData = processData(otsData);
const processedRORData = processData(rorData);
const processedDORData = processData(dorData);
const processedTOSData = processData(tosData);
const processedCOIData = processData(coiData);
const processedEDOData = processData(edoData);
const processedROSData = processData(rosData);
const processedRODData = processData(rodData);
const rows = [
  { col1: 'Data 1.1', col2: 'Data 1.2', col3: 'Data 1.3', col4: 'Data 1.4', col5: 'Data 1.5', col6: 'Data 1.6', col7: 'Data 1.7', col8: 'Data 1.8', col9: 'Data 1.9', col10: 'Data 1.10', col11: 'Data 1.11' },
  { col1: 'Data 2.1', col2: 'Data 2.2', col3: 'Data 2.3', col4: 'Data 2.4', col5: 'Data 2.5', col6: 'Data 2.6', col7: 'Data 2.7', col8: 'Data 2.8', col9: 'Data 2.9', col10: 'Data 2.10', col11: 'Data 2.11' },
  { col1: 'Data 3.1', col2: 'Data 3.2', col3: 'Data 3.3', col4: 'Data 3.4', col5: 'Data 3.5', col6: 'Data 3.6', col7: 'Data 3.7', col8: 'Data 3.8', col9: 'Data 3.9', col10: 'Data 3.10', col11: 'Data 3.11' },
  { col1: 'Data 4.1', col2: 'Data 4.2', col3: 'Data 4.3', col4: 'Data 4.4', col5: 'Data 4.5', col6: 'Data 4.6', col7: 'Data 4.7', col8: 'Data 4.8', col9: 'Data 4.9', col10: 'Data 4.10', col11: 'Data 4.11' },
];

export default function Dashboard(props) {
  
    const theme = useTheme();
    const xTicks = Array.from({ length: 144 }, (_, i) => i + 1);
    const colorMap = {
      'OTS': 'rgb(30,185,128)',
      'ROR': '#dc004e',
      'DOR': '#82ca9d',
      'TOS': '#ff7300',
      'COI': '#8884d8',
      'EDO': '#8dd1e1',
      'ROS': '#ffc658',
      'ROD': '#d0ed57', 
      'defaultColor': 'rgb(30,185,128)'
    };

    const dataKey = `processed${props.type}Data`;
  
    return (
      <>

      <div className="dashboard--wrapper">
        <Nav />

        <div className="chart--wrapper">
          <p className="chart--text">Chart for <span style={{ color: colorMap[props.type] || 'defaultColor' }}>{props.type}</span></p>
          <div style={{ width: '70vw', height: 500 }}>
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
                    <Line type="monotone" dataKey="sum" data={processedOTSData} name="OTS" stroke={theme.palette.primary.main} dot={{ r: 0, fill: theme.palette.primary.main, opacity: 0.5 }} />
                    <Line type="monotone" dataKey="sum" data={processedRORData} name="ROR" stroke={theme.palette.secondary.main} dot={{ r: 0, fill: theme.palette.secondary.main, opacity: 0.5 }} />
                    <Line type="monotone" dataKey="sum" data={processedDORData} name="DOR" stroke="#82ca9d" dot={{ r: 0, fill: '#82ca9d', opacity: 0.5 }} />
                    <Line type="monotone" dataKey="sum" data={processedTOSData} name="TOS" stroke="#ff7300" dot={{ r: 0, fill: '#ff7300', opacity: 0.5 }} />
                    <Line type="monotone" dataKey="sum" data={processedCOIData} name="COI" stroke="#8884d8" dot={{ r: 0, fill: '#8884d8', opacity: 0.5 }} />
                    <Line type="monotone" dataKey="sum" data={processedEDOData} name="EDO" stroke="#8dd1e1" dot={{ r: 0, fill: '#8dd1e1', opacity: 0.5 }} />
                    <Line type="monotone" dataKey="sum" data={processedROSData} name="ROS" stroke="#ffc658" dot={{ r: 0, fill: '#ffc658', opacity: 0.5 }} />
                    <Line type="monotone" dataKey="sum" data={processedRODData} name="ROD" stroke="#d0ed57" dot={{ r: 0, fill: '#d0ed57', opacity: 0.5 }} />
                </>
                ) : (
                <>
                    <Line type="monotone" dataKey="sum" data={eval(dataKey)} name={props.type} stroke={colorMap[props.type] || 'defaultColor'} dot={{ r: 0, fill: colorMap[props.type] || 'defaultColor', opacity: 0.5 }} />
                </>
                )}
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>
        <div className="table--wrapper">
          <TableContainer component={Paper} sx={{
              backgroundColor: 'transparent', // Ustawienie przezroczystego tła
              color: 'white' // Ustawienie białego tekstu
          }}>
              <Table sx={{ width: '65vw', border: '1px solid white' }} aria-label="simple table">
                  <TableHead>
                      <TableRow>
                          <TableCell sx={{ border: '1px solid white', color: 'white' }}>Column 1</TableCell>
                          <TableCell sx={{ border: '1px solid white', color: 'white' }}>Column 2</TableCell>
                          <TableCell sx={{ border: '1px solid white', color: 'white' }}>Column 3</TableCell>
                          <TableCell sx={{ border: '1px solid white', color: 'white' }}>Column 4</TableCell>
                          <TableCell sx={{ border: '1px solid white', color: 'white' }}>Column 5</TableCell>
                          <TableCell sx={{ border: '1px solid white', color: 'white' }}>Column 6</TableCell>
                          <TableCell sx={{ border: '1px solid white', color: 'white' }}>Column 7</TableCell>
                          <TableCell sx={{ border: '1px solid white', color: 'white' }}>Column 8</TableCell>
                          <TableCell sx={{ border: '1px solid white', color: 'white' }}>Column 9</TableCell>
                          <TableCell sx={{ border: '1px solid white', color: 'white' }}>Column 10</TableCell>
                          <TableCell sx={{ border: '1px solid white', color: 'white' }}>Column 11</TableCell>
                      </TableRow>
                  </TableHead>
                  <TableBody>
                      {rows.map((row, index) => (
                          <TableRow key={index}>
                              <TableCell sx={{ border: '1px solid white', color: 'white' }}>{row.col1}</TableCell>
                              <TableCell sx={{ border: '1px solid white', color: 'white' }}>{row.col2}</TableCell>
                              <TableCell sx={{ border: '1px solid white', color: 'white' }}>{row.col3}</TableCell>
                              <TableCell sx={{ border: '1px solid white', color: 'white' }}>{row.col4}</TableCell>
                              <TableCell sx={{ border: '1px solid white', color: 'white' }}>{row.col5}</TableCell>
                              <TableCell sx={{ border: '1px solid white', color: 'white' }}>{row.col6}</TableCell>
                              <TableCell sx={{ border: '1px solid white', color: 'white' }}>{row.col7}</TableCell>
                              <TableCell sx={{ border: '1px solid white', color: 'white' }}>{row.col8}</TableCell>
                              <TableCell sx={{ border: '1px solid white', color: 'white' }}>{row.col9}</TableCell>
                              <TableCell sx={{ border: '1px solid white', color: 'white' }}>{row.col10}</TableCell>
                              <TableCell sx={{ border: '1px solid white', color: 'white' }}>{row.col11}</TableCell>
                          </TableRow>
                      ))}
                  </TableBody>
              </Table>
          </TableContainer>
        </div>
      </div>
      </>
    );
    
  }