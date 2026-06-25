
import { useEffect, useState } from "react";
import { Center, ScrollArea, Text } from "@mantine/core";
import { IconUsers } from "@tabler/icons-react";
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
       border-red-500 shadow-md flex bg-red-100" key={app.id}>
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
    <div className="p-3 border rounded-xl bg-red-50 shadow-xl flex flex-col gap-3">
      <div className="flex items-center gap-2">
        <IconUsers size={18} className="text-red-500" />
        <div className="font-semibold">Patients</div>
        <span className="ml-auto text-xs font-medium text-red-600 bg-red-100 px-2 py-0.5 rounded-full">
          {patient.length}
        </span>
      </div>
      <div>
        <ScrollArea.Autosize mah={300} mx="auto">
          {patient.length === 0 ? (
            <Center py="xl">
              <Text size="sm" c="dimmed">No patients registered yet.</Text>
            </Center>
          ) : (
            patient.map((app) => card(app))
          )}
        </ScrollArea.Autosize>
      </div>
    </div>
  );
};

export default PatientChart

