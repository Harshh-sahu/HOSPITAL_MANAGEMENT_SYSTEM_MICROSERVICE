import { useEffect, useState } from "react";
import { ScrollArea } from "@mantine/core";
import { getAllPatient } from "../../../Service/PatientProfileService";
import { bloodGroupMap } from "../../../Data/DropDownData";

const PatientChart = () => {
  
      useEffect(()=>{
  getAllPatient().then((res)=>{
  
      setPatient(res);
  }).catch((error)=>{
      console.error(error);
  });
      },[])
      const [patient,setPatient] = useState<any[]>([]);
  
  const card = (app: any) => {
    return (
      <div className="p-3 m-3 border rounded-xl justify-between border-l-4
       border-green-500 shadow-md flex bg-green-100" key={app.id}>
        <div>
          <div className="font-semibold">{app.name}</div>
          <div className="text-sm text-gray-500">{app.email}</div>
        </div>
        <div className="text-right">
          <div className="text-sm text-gray-500">{app.address}</div>
          <div className="text-sm text-gray-500">BloodGroup: {bloodGroupMap[app.bloodGroup]}</div>
        </div>
      </div>
    );
  };

  return (
    <div className="p-3 border rounded-xl bg-green-50 shadow-xl flex flex-col gap-3">
      <div className="font-xl font-semibold"> Patients</div>
      <div>
        <ScrollArea.Autosize mah={300} mx="auto">
          {patient.map((app) => card(app))}
        </ScrollArea.Autosize>
      </div>
    </div>
  );
};

export default PatientChart

