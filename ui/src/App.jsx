import Dashboard from "./pages/Dashboard"
import Info from "./pages/Info"
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
            <Route path="/info" element={<Info />} />
          </Routes>
        </BrowserRouter>
      </ThemeProvider>
    </>
  )
}

export default App
