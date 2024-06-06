import React, { useEffect, useState } from "react";
import axios from 'axios';
import "../styles/dashboard.css";
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, TablePagination, Box } from '@mui/material';


export default function DataTable(props) {
    const [tableData, setTableData] = useState([]);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(12); 

    const columns = [
      { id: 'month', label: 'Month' },
      { id: 'quantity', label: 'Quantity' },
      { id: 'buyPrice', label: 'Buy Price' },
      { id: 'basePrice', label: 'Base Price' },
      { id: 'percentage', label: 'Percentage' },
      { id: 'grossValue', label: 'Gross Value' },
      { id: 'penalty', label: 'Penalty' },
      { id: 'withdrawal', label: 'Withdrawal' },
      { id: 'account', label: 'Account' },
      { id: 'finalResult', label: 'Final Result' },
    ];

    useEffect(() => {
        axios.get(`http://localhost:9000/api/bond/${props.type}`)
        .then(response => {
            const newTableData = [];
            Object.keys(response.data).forEach(bondName => {
              const bondData = response.data[bondName];
              const bondTableData = bondData.map((row, index) => ({
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

              newTableData.push(...bondTableData)
            })
            setTableData(newTableData);
        })

        
    });

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


    return (
        <>
        <div className="table--wrapper">
            <TableContainer component={Paper} sx={{
              backgroundColor: 'transparent',
              color: 'white',
              boxShadow: 'none'
            }}>
              <Table sx={{ width: '65vw', border: '1px solid white'}} aria-label="simple table">
                <TableHead>
                  <TableRow>
                    {columns.map(column => (
                      <TableCell key={column.id} sx={tableCellStyle}>
                        {column.label}
                      </TableCell>
                    ))}
                  </TableRow>
                </TableHead>
                <TableBody>
                {tableData
                  .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                  .map((row, index) => (
                    <TableRow key={index}>
                      {columns.map(column => (
                        <TableCell key={`${index}-${column.id}`} sx={tableCellStyle}>
                          {row[column.id]}
                        </TableCell>
                      ))}
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
        </div>
        </>
    )
}