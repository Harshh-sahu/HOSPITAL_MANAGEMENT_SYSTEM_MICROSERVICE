import { Center, ScrollArea, Text } from "@mantine/core";
import { useEffect, useState } from "react";
import { getAllPatient } from "../../../Service/PatientProfileService";
import { bloodGroupMap } from "../../../Data/DropDownData";
import { IconUserOff } from "@tabler/icons-react";

const PatientChart = () => {
  const [patient, setPatient] = useState<any[]>([]);

  useEffect(() => {
    getAllPatient().then((res) => setPatient(res)).catch(console.error);
  }, []);

  const card = (app: any) => (
    <div className="p-3 m-3 border rounded-xl justify-between border-l-4 border-green-500 shadow-md flex bg-green-100" key={app.id}>
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

  return (
    <div className="p-3 border rounded-xl bg-green-50 shadow-xl flex flex-col gap-3">
      <div className="flex items-center gap-2">
        <span className="font-semibold">Patients</span>
        <span className="ml-auto text-xs font-medium text-green-600 bg-green-100 px-2 py-0.5 rounded-full">
          {patient.length}
        </span>
      </div>
      <ScrollArea.Autosize mah={300} mx="auto">
        {patient.length === 0 ? (
          <Center h={200}>
            <div className="flex flex-col items-center gap-3">
              <IconUserOff size={52} stroke={1.2} className="text-gray-300" />
              <div className="text-center">
                <Text fw={500} c="dimmed">No patients registered yet</Text>
                <Text size="xs" c="dimmed" mt={4}>Patients will appear here once they sign up.</Text>
              </div>
            </div>
          </Center>
        ) : patient.map((app) => card(app))}
      </ScrollArea.Autosize>
    </div>
  );
};

export default PatientChart;
