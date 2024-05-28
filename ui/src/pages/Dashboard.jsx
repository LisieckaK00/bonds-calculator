import Nav from "../components/Nav";
import DataTable from "../components/DataTable"
import Chart from "../components/Chart";

export default function Dashboard(props) {

  const months = 480;
  const quantity = 40;

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




