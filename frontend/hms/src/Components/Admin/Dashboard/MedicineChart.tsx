import { Center, ScrollArea, Text } from "@mantine/core";
import { useEffect, useState } from "react";
import { getAllMedicines } from "../../../Service/MedicineService";
import { IconPillOff } from "@tabler/icons-react";

const MedicineChart = () => {
  const [data, setData] = useState<any[]>([]);

  useEffect(() => {
    getAllMedicines().then((res) => setData(res))
      .catch((err) => console.error("Error fetching medicine data:", err));
  }, []);

  const card = (app: any) => (
    <div className="p-3 m-3 border rounded-xl justify-between border-l-4 border-orange-500 shadow-md flex bg-orange-100" key={app.id}>
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

  return (
    <div className="p-3 border rounded-xl bg-orange-50 shadow-xl flex flex-col gap-3">
      <div className="flex items-center gap-2">
        <span className="font-semibold">Medicines</span>
        <span className="ml-auto text-xs font-medium text-orange-600 bg-orange-100 px-2 py-0.5 rounded-full">
          {data.length}
        </span>
      </div>
      <ScrollArea.Autosize mah={300} mx="auto">
        {data.length === 0 ? (
          <Center h={180}>
            <div className="flex flex-col items-center gap-2">
              <IconPillOff size={44} stroke={1.2} className="text-gray-300" />
              <Text c="dimmed" size="sm">No medicines in the catalog yet.</Text>
            </div>
          </Center>
        ) : data.map((app) => card(app))}
      </ScrollArea.Autosize>
    </div>
  );
};

export default MedicineChart;
