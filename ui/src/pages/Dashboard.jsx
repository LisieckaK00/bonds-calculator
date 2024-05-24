import React, { useEffect, useState } from "react";
import axios from 'axios';
import { LineChart, Line, XAxis, YAxis, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import Nav from "../components/Nav";
import "../styles/dashboard.css";
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper } from '@mui/material';

export default function Dashboard(props) {
  const [chartData, setChartData] = useState([]);
  const [tableData, setTableData] = useState([])

  const months = 480;

  useEffect(() => {
    axios.get(`http://localhost:9000/api/bonds/${props.type}/${months}`)
      .then(response => {
        setTableData([]);
        const newData = response.data.map((value, index) => ({
          month: index + 1,  
          value: Math.round(value * 100) / 100       
        }));
        setChartData(newData);
        // const apiData = response.data.map(item => ({
        //   col1: Math.round((item[0] || 0) * 100) / 100,
        //   col2: Math.round((item[1] || 0) * 100) / 100,
        //   col3: Math.round((item[2] || 0) * 100) / 100,
        //   col4: Math.round((item[3] || 0) * 100) / 100,
        //   col5: Math.round((item[4] || 0) * 100) / 100,
        //   col6: Math.round((item[5] || 0) * 100) / 100,
        //   col7: Math.round((item[6] || 0) * 10000) / 10000,
        //   col8: Math.round((item[7] || 0) * 100) / 100,
        //   col9: Math.round((item[8] || 0) * 100) / 100,
        //   col10: Math.round((item[9] || 0) * 100) / 100,
        //   col11: Math.round((item[10] || 0) * 100) / 100,
        //   col12: Math.round((item[11] || 0) * 100) / 100
        // }));
        
        // setTableData(prevData => [...prevData, ...apiData]);


      })
      .catch(error => console.error('Error:', error));
  }, [props.type]);  

  console.log(chartData)

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

  return (
    <>
    <div className="dashboard--wrapper">
      <Nav />
      <div className="chart--wrapper">
        <p className="chart--text">Chart for <span style={{ color: colorMap[props.type] || 'defaultColor' }}>{props.type}</span></p>
        <div style={{ width: '70vw', height: '500px' }}>
          <ResponsiveContainer width="100%" height="100%">
            <LineChart
              data={chartData}
              margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
            >
              <XAxis dataKey="month" type="number" domain={[1, months]} allowDecimals={false} ticks={Array.from({length: months}, (_, i) => i + 1)} />
              <YAxis domain={[10000, 'auto']} />
              <Tooltip />
              <Legend />
              <Line type="monotone" dataKey="value" stroke={colorMap[props.type] || 'defaultColor'} dot={{ r: 0 }} />
            </LineChart>
          </ResponsiveContainer>
        </div>
        {/* <div className="table--wrapper">
            <TableContainer component={Paper} sx={{
              backgroundColor: 'transparent', 
              color: 'white'
            }}>
              <Table sx={{ width: '65vw', border: '1px solid white' }} aria-label="simple table">
                <TableHead>
                  <TableRow>
                    <TableCell sx={{ border: '1px solid white', color: 'white' }}>End of month</TableCell>
                    <TableCell sx={{ border: '1px solid white', color: 'white' }}>Quantity</TableCell>
                    <TableCell sx={{ border: '1px solid white', color: 'white' }}>Buy Price</TableCell>
                    <TableCell sx={{ border: '1px solid white', color: 'white' }}>Base Price</TableCell>
                    <TableCell sx={{ border: '1px solid white', color: 'white' }}>Percentage</TableCell>
                    <TableCell sx={{ border: '1px solid white', color: 'white' }}>Current Value</TableCell>
                    <TableCell sx={{ border: '1px solid white', color: 'white' }}>Penalty</TableCell>
                    <TableCell sx={{ border: '1px solid white', color: 'white' }}>Withdrawal</TableCell>
                    <TableCell sx={{ border: '1px solid white', color: 'white' }}>Account</TableCell>
                    <TableCell sx={{ border: '1px solid white', color: 'white' }}>Final result</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {tableData.map((row, index) => (
                    <TableRow key={index}>
                      <TableCell sx={{ border: '1px solid white', color: 'white' }}>{row.col1}</TableCell>
                      <TableCell sx={{ border: '1px solid white', color: 'white' }}>{row.col3}</TableCell>
                      <TableCell sx={{ border: '1px solid white', color: 'white' }}>{row.col4}</TableCell>
                      <TableCell sx={{ border: '1px solid white', color: 'white' }}>{row.col6}</TableCell>
                      <TableCell sx={{ border: '1px solid white', color: 'white' }}>{row.col7}</TableCell>
                      <TableCell sx={{ border: '1px solid white', color: 'white' }}>{row.col8}</TableCell>
                      <TableCell sx={{ border: '1px solid white', color: 'white' }}>{row.col9}</TableCell>
                      <TableCell sx={{ border: '1px solid white', color: 'white' }}>{row.col10}</TableCell>
                      <TableCell sx={{ border: '1px solid white', color: 'white' }}>{row.col11}</TableCell>
                      <TableCell sx={{ border: '1px solid white', color: 'white' }}>{row.col12}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
        </div> */}
        </div>
      </div>
    </>
  );
}
