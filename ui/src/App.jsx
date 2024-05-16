import Dashboard from "./pages/Dashboard"
import { ThemeProvider, createTheme } from '@mui/material/styles';
import { BrowserRouter, Routes, Route } from 'react-router-dom';

const theme = createTheme({
  palette: {
    primary: {
      main: 'rgb(30,185,128)',
    },
    text: {
      primary: '#ffffff',
    },
    secondary: {
      main: '#dc004e',
    },
  },
});

function App() {
  return (
    <>
      <ThemeProvider theme={theme}>
        <BrowserRouter>
          <Routes>
            <Route path="/overview" element={<Dashboard type="Overview" />} />
            <Route path="/ots" element={<Dashboard type="OTS" />} />
            <Route path="/ror" element={<Dashboard type="ROR" />} />
            <Route path="/dor" element={<Dashboard type="DOR" />} />
            <Route path="/tos" element={<Dashboard type="TOS" />} />
            <Route path="/coi" element={<Dashboard type="COI" />} />
            <Route path="/edo" element={<Dashboard type="EDO" />} />
            <Route path="/ros" element={<Dashboard type="ROS" />} />
            <Route path="/rod" element={<Dashboard type="ROD" />} />
          </Routes>
        </BrowserRouter>
      </ThemeProvider>
    </>
  )
}

export default App
