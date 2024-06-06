import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, ResponsiveContainer } from 'recharts';
import Nav from "../components/Nav";
import "../styles/start.css";

export default function Start() {
  const [months, setMonths] = useState();
  const [quantity, setQuantity] = useState();
  const [data, setData] = useState([]);

  useEffect(() => {
    const storedMonths = localStorage.getItem('months');
    const storedQuantity = localStorage.getItem('quantity');

    if (storedMonths !== null) {
      setMonths(Number(storedMonths));
    }
    if (storedQuantity !== null) {
      setQuantity(Number(storedQuantity));
    }
  }, []);

  useEffect(() => {
    if (months) {
      const numPoints = Math.floor(months / 12) + 1;
      const newData = Array.from({ length: numPoints }, (_, i) => ({
        year: i + 1,  // Start from 1
        inflationValue: 2.5
      }));
      setData(newData);
    }
  }, [months]);

  const handleDotDrag = (index, newY) => {
    const inflationValueValue = 16 - (newY / 300) * 17; // Assuming chart height is 300
    const newData = [...data];
    newData[index].inflationValue = Math.max(0, Math.min(16, inflationValueValue));
    setData(newData);
  };

  const CustomizedDot = (props) => {
    const { cx, cy, index } = props;

    const handleMouseDown = (event) => {
      const startY = event.clientY;
      const startValue = data[index].inflationValue;

      const onMouseMove = (moveEvent) => {
        const diffY = moveEvent.clientY - startY;
        const newY = cy + diffY;
        handleDotDrag(index, newY);
      };

      const onMouseUp = () => {
        document.removeEventListener('mousemove', onMouseMove);
        document.removeEventListener('mouseup', onMouseUp);
      };

      document.addEventListener('mousemove', onMouseMove);
      document.addEventListener('mouseup', onMouseUp);
      event.preventDefault();
    };

    return (
      <circle
        cx={cx}
        cy={cy}
        r={8}
        fill="#8884d8"
        onMouseDown={handleMouseDown}
        style={{ cursor: 'ns-resize' }}
      />
    );
  };

  const handleClick = () => {
    localStorage.setItem('quantity', quantity);
    localStorage.setItem('months', months);

    const data = {
      quantity: Number(quantity),
      period: Number(months)
    };

    axios.post(`http://localhost:9000/api/params/save`, data, {
      headers: {
        'Content-Type': 'application/json'
      }
    }).then(response => {
      console.log('Success:', response.data);
      alert('Success!');
    }).catch(error => {
      console.error('Error:', error.response ? error.response.data : error.message);
      alert('Oh no! Something went wrong.');
    });
  };

  return (
    <>
      <Nav />
      <div className="start--wrapper">
        <p className="start--header">Type quantity and investing period:</p>
        <div className="input--box">
          <div className='input months--input'>
            <label htmlFor="months-input">Months:</label>
            <input id="months-input" type="number" value={months} onChange={e => setMonths(e.target.value)} />
          </div>
          <div className='input quantity--input'>
            <label htmlFor="quantity-input">Quantity:</label>
            <input id="quantity-input" type="number" value={quantity} onChange={e => setQuantity(e.target.value)} />
          </div>
        </div>

        {months && quantity && <div className="chart--container">
          <p>Choose inflation each year:</p>
          <ResponsiveContainer width="100%" height={300}>
            <LineChart
              data={data}
              margin={{ top: 10, right: 30, left: 30, bottom: 0 }}
            >
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="year" type="number" domain={[1, 'dataMax']} />
              <YAxis domain={[0, 16]} />
              <Line type="monotone" dataKey="inflationValue" stroke="#8884d8" dot={<CustomizedDot />} isAnimationActive={false}/>
            </LineChart>
          </ResponsiveContainer>
          <div className="data-display">
              {console.log(data)}
              <p>{data.map(item => `${item.inflationValue.toFixed(2)}`).join(", ")}</p>
            </div>
          <button onClick={handleClick}>COMPUTE</button>
        </div>}
      </div>
    </>
  );
}
