
import { useEffect, useState } from "react";
import {
  medicines,

} from "../../../Data/DashboardData";

import { ScrollArea } from "@mantine/core";
import { getAllMedicines } from "../../../Service/MedicineService";

const MedicineChart = () => {
const [data,setData]=useState<any[]>(medicines);
   useEffect(() => {
      fetchData();
    }, []);
  
    const fetchData = () => {
      getAllMedicines()
        .then((res) => {
          
          setData(res);
        })
        .catch((error) => {
          console.error("Error fetching medicine data:", error);
        });
    };
  const card = (app: any) => {
    return (
      <div className="p-3 m-3 border rounded-xl justify-between border-l-4
       border-orange-500 shadow-md flex bg-orange-100" key={app.id}>
        <div>
          <div className="font-semibold">{app.name}</div>
          <div className="text-xs text-gray-500">{app.manufacturer}</div>
        </div>
        <div className="text-right">
          <div className="text-xs text-gray-500">{app.dosage}</div>
          <div className="text-xs text-gray-500">Stock: {app.stock}</div>
        </div>
      </div>
    );
  };
 
  return (
    <div className="p-3 border rounded-xl bg-orange-50 shadow-xl flex flex-col gap-3">
      <div className="font-xl font-semibold"> Medicines</div>
      <div>
        <ScrollArea.Autosize mah={300} mx="auto">
          {data.map((app) => card(app))}
        </ScrollArea.Autosize>
      </div>
    </div>
  );
};

export default MedicineChart
