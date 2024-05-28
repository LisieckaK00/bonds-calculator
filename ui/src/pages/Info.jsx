import Nav from "../components/Nav";
import React, { useEffect, useState } from "react";
import axios from 'axios';
import "../styles/info.css";

export default function Info() {

    const [bondsProperties, setBondsProperties] = useState([])

    useEffect(() => {
        axios.get(`http://localhost:9000/api/bonds/getProperties`)
          .then(response => {
            const data = response.data.map((row, index) => ({
                percentage: row.percentage,
                name: row.name,
                description: row.description,
                multiplierActivation: row.multiplierActivation,
                multiplier: row.multiplier,
                type: row.type === "acc" ? "accumulative" : "distributive"
              }));
              setBondsProperties(data);
          })
          .catch(error => console.error('Error:', error));
    }, []);

    const colorMap = {
    'OTS': 'rgb(30,185,128)',
    'ROR': '#dc004e',
    'DOR': '#82ca9d',
    'TOS': '#ff7300',
    'COI': '#8884d8',
    'EDO': '#8dd1e1',
    'ROS': '#ffc658',
    'ROD': '#d0ed57',
    'defaultColor': '#3769DA'
    };


    return (
        <>
        <Nav />
        <div className="bonds--wrapper">
            {bondsProperties.map((bond, index) => (
            <div className="bond" key={index}>
                <div className="bond--header" style={{ color: colorMap[bond.name] || colorMap['defaultColor'] }}> {bond.percentage}% <p className="bond--name">{bond.name}</p></div>
                <div className="bond--description">{bond.description}</div>
                <div className="bond--type" style={{ color: colorMap[bond.name] || colorMap['defaultColor'] }}>{bond.type}</div>
                {bond.multiplier > 0 && 
                    <>
                    <div className="bond--multiplier">After {bond.multiplierActivation} months inflation + {bond.multiplier}% interest</div>
                    </>
                }
            </div>
            ))}
        </div>
        </>
    )
}