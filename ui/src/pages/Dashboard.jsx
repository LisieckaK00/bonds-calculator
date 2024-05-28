import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom"
import axios from 'axios';
import { LineChart, Line, XAxis, YAxis, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import Nav from "../components/Nav";
import "../styles/dashboard.css";
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, TablePagination, Box } from '@mui/material';

export default function Dashboard(props) {
  const [chartData, setChartData] = useState([]);
  const [overviewChartData, setOverviewChartData] = useState([]);
  const [tableData, setTableData] = useState([]);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(12);

  const months = 480;
  const quantity = 40;

  useEffect(() => {
    axios.get(`http://localhost:9000/api/bonds/${props.type}/${quantity}/${months}`)
      .then(response => {
        const newChartData = response.data.map((row, index) => ({
          month: index + 1,
          Result: Math.round(row[9] * 100) / 100,
        }));
        setChartData(newChartData);

        const newTableData = response.data.map((row, index) => ({
          month: index + 1,
          quantity: row[1],
          buyPrice: Math.round(row[2] * 100) / 100,
          basePrice: Math.round(row[3] * 100) / 100,
          percentage: Math.round(row[4] * 10000) / 10000,
          grossValue: Math.round(row[5] * 100) / 100,
          penalty: Math.round(row[6] * 100) / 100,
          withdrawal: Math.round(row[7] * 100) / 100,
          account: Math.round(row[8] * 100) / 100,
          finalResult: Math.round(row[9] * 100) / 100,
        }));

        setTableData(newTableData);
      })
      .catch(error => console.error('Error:', error));
  }, [quantity, months, props.type]);

  useEffect(() => {
    axios.get(`http://localhost:9000/api/bonds/all/getAllBonds/${quantity}/${months}`)
      .then(response => {
        const allBondsChartData = [];
        Object.keys(response.data).forEach(bondName => {
          const bondData = response.data[bondName];
          const bondChartData = bondData.map((row, index) => ({
            bondName,
            month: index + 1,
            finalResult: Math.round(row[9] * 100) / 100,
          }));

          allBondsChartData.push(...bondChartData);
        });

        setOverviewChartData(allBondsChartData);
      })
      .catch(error => console.error('Error:', error));
  }, [quantity, months]);

  const colorMap = {
    'OTS': 'rgb(30,185,128)',
    'ROR': '#dc004e',
    'DOR': '#82ca9d',
    'TOS': '#ff7300',
    'COI': '#8884d8',
    'EDO': '#8dd1e1',
    'ROS': '#ffc658',
    'ROD': '#d0ed57',
    'Overview': 'rgb(255, 255, 255)',
    'defaultColor': 'rgb(255, 255, 255)'
  };

  const tableCellStyle = {
    border: '1px solid white',
    color: 'white'
  };

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const renderLines = (currentType) => {
    const dataToUse = currentType === 'Overview' ? overviewChartData : chartData;
  
    if (currentType !== 'Overview') {
      return (
        <Line
          key="individual"
          type="monotone"
          dataKey="Result"
          data={dataToUse}
          stroke={colorMap[currentType] || colorMap.defaultColor}
          dot={{ r: 0 }}
          activeDot={{ r: 8 }}
        />
      );
    }
  
    const bondNames = [...new Set(dataToUse.map(data => data.bondName))];
    return bondNames.map(bondName => {
      const bondData = dataToUse.filter(data => data.bondName === bondName);
      return (
        <Line
          key={bondName}
          type="monotone"
          dataKey="finalResult"
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
      <div className="dashboard--wrapper">
        <Nav />
        <div className="chart--wrapper">
          <p className="chart--text">Chart for <span style={{ color: colorMap[props.type] || 'defaultColor' }}>{props.type}</span></p>
          <div style={{ width: '70vw', height: '500px' }}>
          <ResponsiveContainer width="100%" height="100%">
            <LineChart
              key={`${props.type}-${Date.now()}`}
              data={props.type === 'Overview' ? overviewChartData : chartData}
              margin={{ top: 50, right: 100, left: 20, bottom: 20 }}
            >
              <XAxis dataKey="month" type="number" domain={[1, months]} allowDecimals={false} ticks={Array.from({ length: months }, (_, i) => i + 1)} label={{ value: 'Month', position: 'insideBottomRight', offset: -10 }} />
              <YAxis domain={['auto', 'auto']} label={{ value: 'Final Result', position: 'insideTop', offset: -40 }} />
              <Tooltip />
              <Legend />
              {renderLines(props.type)}
            </LineChart>
          </ResponsiveContainer>
          </div>
          {props.type !== 'Overview' && <div className="table--wrapper">
            <TableContainer component={Paper} sx={{
              backgroundColor: 'transparent',
              color: 'white',
              boxShadow: 'none'
            }}>
              <Table sx={{ width: '65vw', border: '1px solid white'}} aria-label="simple table">
                <TableHead>
                  <TableRow>
                    <TableCell sx={tableCellStyle}>Month</TableCell>
                    <TableCell sx={tableCellStyle}>Quantity</TableCell>
                    <TableCell sx={tableCellStyle}>Buy Price</TableCell>
                    <TableCell sx={tableCellStyle}>Base Price</TableCell>
                    <TableCell sx={tableCellStyle}>Percentage</TableCell>
                    <TableCell sx={tableCellStyle}>Gross Value</TableCell>
                    <TableCell sx={tableCellStyle}>Penalty</TableCell>
                    <TableCell sx={tableCellStyle}>Withdrawal</TableCell>
                    <TableCell sx={tableCellStyle}>Account</TableCell>
                    <TableCell sx={tableCellStyle}>Final Result</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {tableData.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map((row, index) => (
                    <TableRow key={index}>
                      <TableCell sx={tableCellStyle}>{row.month}</TableCell>
                      <TableCell sx={tableCellStyle}>{row.quantity}</TableCell>
                      <TableCell sx={tableCellStyle}>{row.buyPrice}</TableCell>
                      <TableCell sx={tableCellStyle}>{row.basePrice}</TableCell>
                      <TableCell sx={tableCellStyle}>{row.percentage}</TableCell>
                      <TableCell sx={tableCellStyle}>{row.grossValue}</TableCell>
                      <TableCell sx={tableCellStyle}>{row.penalty}</TableCell>
                      <TableCell sx={tableCellStyle}>{row.withdrawal}</TableCell>
                      <TableCell sx={tableCellStyle}>{row.account}</TableCell>
                      <TableCell sx={tableCellStyle}>{row.finalResult}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
              <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2}}>
                <TablePagination
                  rowsPerPageOptions={[12]}
                  component="div"
                  count={tableData.length}
                  rowsPerPage={rowsPerPage}
                  page={page}
                  onPageChange={handleChangePage}
                  onRowsPerPageChange={handleChangeRowsPerPage}
                />
              </Box>
            </TableContainer>
          </div>}
        </div>
      </div>
    </>
  );
}
