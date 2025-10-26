
import { useEffect, useState } from "react";
import {
  medicines,

} from "../../../Data/DashboardData";

import { ScrollArea } from "@mantine/core";
import { getMedicineConsumeByPatient } from "../../../Service/AppointmentService";
import { useSelector } from "react-redux";

const MedicineChart = () => {
const user = useSelector((state:any)=>state.user);
  const [data,setData]  = useState<any[]>(medicines);

  useEffect(()=>{

getMedicineConsumeByPatient(user.profileId).then((res)=>{
  setData(res);
}).catch((err)=>{
  console.error("Error fetching medicine consumption data:", err);
});
  },[]);


  const card = (app: any) => {
    return (
      <div className="p-3 m-3 border rounded-xl justify-between border-l-4
       border-orange-500 shadow-md flex bg-orange-100 items-center" key={app.id}>
        <div>
          <div className="font-semibold">{app.name}</div>
          <div className="text-sm text-gray-500">{app.manufacturer}</div>
        </div>
        <div className="text-right">
          <div className="text-sm text-gray-500">{app.dosage}</div>
              <div className="text-sm text-gray-500">{app.frequency}</div>
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
