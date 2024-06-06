import Nav from "../components/Nav";
import DataTable from "../components/DataTable"
import Chart from "../components/Chart";
import {useEffect, useState} from "react";

export default function Dashboard(props) {

    const [months, setMonths] = useState(10);
    const [quantity, setQuantity] = useState(10);

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

  return (
    <>
      <div className="dashboard--wrapper">
        <Nav />
        <Chart type={props.type} quantity={quantity} months={months} />
        {props.type !== 'Overview' && <DataTable type={props.type} quantity={quantity} months={months} />}
      </div>
    </>
  );
}




