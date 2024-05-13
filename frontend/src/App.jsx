import Dashboard from "./pages/Dashboard"
import { ThemeProvider, createTheme } from '@mui/material/styles';

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
        <Dashboard />
      </ThemeProvider>
    </>
  )
}

export default App

