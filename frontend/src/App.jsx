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
            <Route path="/overview" element={<Dashboard type="Overview"/>} />
            <Route path="/ots" element={<Dashboard type="Ots" />} />
            <Route path="/ror" element={<Dashboard type="Ror" />} />
            <Route path="/dor" element={<Dashboard type="Dor" />} />
            <Route path="/tos" element={<Dashboard type="Tos" />} />
            <Route path="/coi" element={<Dashboard type="Coi" />} />
            <Route path="/edo" element={<Dashboard type="Edo" />} />
            <Route path="/ros" element={<Dashboard type="Ros" />} />
            <Route path="/rod" element={<Dashboard type="Rod" />} />
          </Routes>
        </BrowserRouter>
      </ThemeProvider>
    </>
  )
}

export default App

