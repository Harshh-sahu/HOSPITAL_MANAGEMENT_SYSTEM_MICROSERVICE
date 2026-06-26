import { Center, ScrollArea, Text } from "@mantine/core";
import { useEffect, useState } from "react";
import { getAllDoctor } from "../../../Service/DoctorProfileService";
import { IconStethoscope } from "@tabler/icons-react";

const DoctorChart = () => {
  const [doctor, setDoctor] = useState<any[]>([]);

  useEffect(() => {
    getAllDoctor().then((res) => setDoctor(res)).catch(console.error);
  }, []);

  const card = (app: any) => (
    <div className="p-3 m-3 border rounded-xl justify-between border-l-4 border-blue-500 shadow-md flex bg-blue-100" key={app.id}>
      <div>
        <div className="font-semibold">{app.name}</div>
        <div className="text-sm text-gray-500">{app.email}</div>
      </div>
      <div className="text-right">
        <div className="text-sm text-gray-500">{app.department}</div>
        <div className="text-sm text-gray-500">{app.specialization}</div>
      </div>
    </div>
  );

  return (
    <div className="p-3 border rounded-xl bg-blue-50 shadow-xl flex flex-col gap-3">
      <div className="flex items-center gap-2">
        <span className="font-semibold">Doctors</span>
        <span className="ml-auto text-xs font-medium text-blue-600 bg-blue-100 px-2 py-0.5 rounded-full">
          {doctor.length}
        </span>
      </div>
      <ScrollArea.Autosize mah={300} mx="auto">
        {doctor.length === 0 ? (
          <Center h={200}>
            <div className="flex flex-col items-center gap-3">
              <IconStethoscope size={52} stroke={1.2} className="text-gray-300" />
              <div className="text-center">
                <Text fw={500} c="dimmed">No doctors registered yet</Text>
                <Text size="xs" c="dimmed" mt={4}>Doctors will appear here once they are added.</Text>
              </div>
            </div>
          </Center>
        ) : doctor.map((app) => card(app))}
      </ScrollArea.Autosize>
    </div>
  );
};

export default DoctorChart;
